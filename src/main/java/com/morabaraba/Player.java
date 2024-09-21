package com.morabaraba;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;



public class Player {
    private String name;
    private HashMap<Cow, Position> cows;
    private GameBoard board; 
    
    public Player(String name, List<Cow> cows) {
        this.name = name;
        this.board = new GameBoard();
        this.cows =  initaializeCows();
    }


    /**
     * Moves a cow to a valid position
     * @param cow
     * @param position
     * @return boolean return true if successful and false otherwise
     */

    public boolean moveCow(Cow cow, Position position) {
        
        if(!cow.isPlaced()) {
            cow.setPosition(position);
            return true;
        } else {

            // if cow is placed check for neighbouring positions
            List<Position> neigbourList =  this.board.getNeighbors(position);
            // Filterring the neighbours to get a list of all valid neighbours
            List<Position> validNeighbours = neigbourList.stream().filter( n -> !n.isOccupied() ).collect(Collectors.toList());

            if (validNeighbours.contains(position)) {
                cow.setPosition(position);
                return true;
            }
                                                        
        }
        return false;
    }


    /**
     * Initializes cows for a player
     * @return a HashMap of the players cows mapped to their positions
     */
    private HashMap<Cow, Position> initaializeCows() {
        HashMap<Cow, Position> cows = new HashMap<Cow, Position>();
        for(int i = 1; i <= 12; i++) {

            cows.put(new Cow(this, i), null); // assigns a cow an owner
        }
        return cows;
    }


}
