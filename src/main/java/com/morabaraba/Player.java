package com.morabaraba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;



public class Player {
    private String name;
    private HashMap<Cow, Position> cows;
    private GameBoard board; 
    private List<Cow> placedCows;
    
    public Player(String name, GameBoard board) {
        this.name = name;
        this.board = board;
        this.cows =  initaializeCows();
        this.placedCows = new ArrayList<>();
        // placedCows.sort( null );
    }

    public String getName() {
        return name;
    }
    public List<Cow> getCows() {
        return cows.keySet().stream().toList();
    }

    public List<Cow> getUnplacedCows() {
        return cows.keySet()
                .stream()
                .filter( cow -> cow.getPosition() == null).toList();
    }

    /**
     * Moves a cow to a valid position if not occupied and marks the postion as occupied if successful
     * This method also updates the state variable of placed cows by incrementing it
     * @param cow
     * @param position
     * @return cow that was moved
     */

    public Cow moveCow(Cow cow, Position position) {
        
        if(!cow.isPlaced() && !position.isOccupied()) { 
            cow.setPosition(position); // assign a cow a position
            placedCows.add(cow); // add cow to placed cows 
            position.occupy(); // mark the postion as occupired
            cows.put(cow, position); // update the cow hashmap to mark the cow as placed
            return cow;
        } else {
            ////////////////////////////////// to fix

            List<Position> neigbourList =  this.board.getNeighbors(position);
            // getting valid neighbours
            List<Position> validNeighbours = neigbourList.stream().filter( n -> !n.isOccupied() ).collect(Collectors.toList());

            if (validNeighbours.contains(position)) {
                cow.setPosition(position);
                return cow;
            }
                                                        
        }
        return cow;
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
