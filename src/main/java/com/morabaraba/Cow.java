package com.morabaraba;

public class Cow {
    private final Player owner; // Owner of the cow (player identifier)
    private Position position;   // Current position of the cow
    private int cowTag;

    public Cow(Player owner, int cowTag) {
        this.owner = owner;
        this.cowTag = cowTag;
        this.position = null; // Initially, the cow is not placed on the board
    }

    /**
     * Gets the owner of the cow.
     * @return the owner's identifier.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the current position of the cow.
     * @return the position of the cow.
     */
    public Position getPosition() {
        return position;
    }


    /**
     * Sets the position of the cow on the board.
     * @param position The position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Checks if the cow is currently placed on the board.
     * @return true if the cow is placed, false otherwise.
     */
    public boolean isPlaced() {
        return position != null && position.isOccupied();
    }

    @Override
    public String toString() {
        return "Cow{" +
                "owner='" + owner + '\'' +
                ", position=" + (position != null ? position.getPosition() : "not placed") +
                '}';
    }
}
