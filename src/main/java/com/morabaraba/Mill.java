package com.morabaraba;

import java.util.ArrayList;
import java.util.List;

public class Mill {
    private final List<Position> positions; 
    private final String owner; 

    public Mill(String owner) {
        this.owner = owner;
        this.positions = new ArrayList<>(3); 
    }

    /**
     * Adds a position to the mill.
     * @param position The position to add.
     * @return true if the position was successfully added, false otherwise.
     */
    public boolean addPosition(Position position) {
        if (positions.size() < 3 && !position.isOccupied()) {
            positions.add(position);
            position.occupy(); 
            return true;
        }
        return false; // Cannot add the position
    }

    /**
     * Checks if the mill is complete (contains three positions).
     * @return true if the mill is complete, false otherwise.
     */
    public boolean isComplete() {
        return positions.size() == 3;
    }

    /**
     * Returns the owner of the mill.
     * @return the owner's identifier.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Releases the positions that make up the mill.
     */
    public void release() {
        for (Position position : positions) {
            position.release(); // Mark each position as unoccupied
        }
        positions.clear(); // Clear the positions list
    }

    @Override
    public String toString() {
        return "Mill{" +
                "positions=" + positions +
                ", owner='" + owner + '\'' +
                '}';
    }
}
