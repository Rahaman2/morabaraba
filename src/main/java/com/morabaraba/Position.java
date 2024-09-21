package com.morabaraba;

public class Position {
    private final String position; // Position identifier
    private boolean occupied; 


    public Position(String position) {
        this.position = position;
        this.occupied = false;
    }
    /**
     * 
     * @return name of the postition
     */
    public String getName() {
        return position;
    }

    /**
     * Checks if the position is occupied.
     * @return true if the position is occupied, false otherwise.
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Occupies the position if it is not already occupied.

     * @return true if the position was successfully occupied, false otherwise.
     */
    public boolean occupy() {
        if (!occupied) {
            this.occupied = true; // Mark as occupied
            // You can also add logic here to associate the mill with the position if needed
            return true;
        }
        return false; // Position is already occupied
    }

    /**
     * Releases the position, marking it as unoccupied.
     */
    public void release() {
        this.occupied = false; // Mark as unoccupied
    }

    /**
     * Returns the position identifier.
     * @return the position as a string.
     */
    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Position{" +
                "position='" + position + '\'' +
                ", occupied=" + occupied +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.position.equals(other.position) && this.occupied == other.occupied;
    }


    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
