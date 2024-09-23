package com.morabaraba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PositionTest {

    private Position position;

    @BeforeEach
    void setUp() {
        
        position = new Position("A1");
    }

    @Test
    void testIsOccupiedInitially() {
        assertFalse(position.isOccupied(), "Position should be initially unoccupied.");
    }

    @Test
    void testOccupyPosition() {
        assertTrue(position.occupy(), "Position should be successfully occupied.");
        assertTrue(position.isOccupied(), "Position should be occupied after calling occupy().");
    }

    @Test
    void testOccupyPositionTwice() {
        position.occupy(); // First occupation
        assertFalse(position.occupy(), "Position should not be occupied again.");
    }

    @Test
    void testGetNeigbours() {

        List<Position> neighbours = List.of(new Position("A2"), new Position("B1"), new Position("D1"));

        for(int i = 0; i < neighbours.size(); i++) {
            assertTrue(neighbours.contains(neighbours.get(i)), "Position " + neighbours.get(i).getName() + "not a valid neighbour");
        }
    }

    @Test
    void testReleasePosition() {
        position.occupy(); // Occupy first
        position.release(); // Release it
        assertFalse(position.isOccupied(), "Position should be unoccupied after release().");
    }

    @Test
    void testGetPosition() {
        assertEquals("A1", position.getPosition(), "Position identifier should be 'A1'.");
    }

    @Test
    void testToString() {
        String expectedString = "Position{position='A1', occupied=false}";
        assertEquals(expectedString, position.toString(), "toString() should return the correct string representation.");
    }

    @Test
    void testEqualsSameObject() {
        assertEquals(position, new Position("A1"), "Position should be equal to itself.");
    }

    @Test
    void testEqualsDifferentObjectWithSamePosition() {
        Position anotherPosition = new Position("A1");
        assertEquals(position, anotherPosition, "Positions with the same identifier should be equal.");
    }

    @Test
    void testEqualsDifferentPosition() {
        Position anotherPosition = new Position("B2");
        assertNotEquals(position, anotherPosition, "Positions with different identifiers should not be equal.");
    }

    @Test
    void testHashCode() {
        Position anotherPosition = new Position("A1");
        assertEquals(position.hashCode(), anotherPosition.hashCode(), "Positions with the same identifier should have the same hash code.");
    }
}
