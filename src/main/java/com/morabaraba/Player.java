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

    public List<Cow> getPlacedCows() {
        return placedCows;
    }

    /**
     * Moves a cow to a valid position if not occupied and marks the postion as occupied if successful.
     * During placement phase (cow not yet placed), places the cow on the target position.
     * During movement phase (cow already placed), moves the cow to an adjacent unoccupied position.
     * If the player has exactly 3 cows they may "fly" to any empty position.
     * @param cow
     * @param position
     * @return cow that was moved
     */
    public Cow moveCow(Cow cow, Position position) {
        if (!cow.isPlaced() && !position.isOccupied()) {
            // Placement phase
            cow.setPosition(position);  // also calls position.occupy() internally
            placedCows.add(cow);
            cows.put(cow, position);
            return cow;
        } else if (cow.isPlaced() && !position.isOccupied()) {
            // Movement phase
            List<Position> neighbours = this.board.getNeighbors(cow.getPosition());
            boolean canFly = (placedCows.size() == 3);
            boolean validMove = canFly ||
                (neighbours != null && neighbours.stream()
                    .anyMatch(n -> n.getName().equals(position.getName())));
            if (validMove) {
                cow.getPosition().release();
                cow.setPosition(position);
                cows.put(cow, position);
            }
        }
        return cow;
    }

    /**
     * Removes a cow from the board. Used when an opponent forms a mill and shoots.
     * @param cow the cow to remove
     */
    public void removeCow(Cow cow) {
        if (cow.getPosition() != null) {
            cow.getPosition().release();
        }
        cow.clearPosition();
        placedCows.remove(cow);
        cows.remove(cow);
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
