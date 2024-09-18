package com.morabaraba;

import java.util.*;

public class GameBoard {
    private final Map<String, Position> positions; // Map of position identifiers to Position objects
    private final Map<String, List<Position>> adjacencyList; // Adjacency list for graph representation

    public GameBoard() {
        positions = new HashMap<>();
        adjacencyList = new HashMap<>();
        initializeBoard();
    }
    /**
     * Initializes the board by createng a graph of positions mapped to thier neighbours
     */
    private void initializeBoard() {
        
        String[] posKeys = {
            "A1", "A2", "A3",
            "B1", "B2", "B3",
            "C1", "C2", "C3",
            "D1", "D2", "D3",
            "E1", "E2", "E3",
            "F1", "F2", "F3",
            "G1", "G2", "G3",
            "H1", "H2", "H3"
        };
        for (String key : posKeys) {
            positions.put(key, new Position(key));
        }

        // Set up adjacency list
        adjacencyList.put("A1", getNeighborPositions(Arrays.asList("A2", "D1")));
        adjacencyList.put("A2", getNeighborPositions(Arrays.asList("A1", "A3", "B2")));
        adjacencyList.put("A3", getNeighborPositions(Arrays.asList("A2", "B3", "E3")));

        adjacencyList.put("B1", getNeighborPositions(Arrays.asList("A1", "B2", "C1", "D2")));
        adjacencyList.put("B2", getNeighborPositions(Arrays.asList("A2", "B1", "C2", "B3")));
        adjacencyList.put("B3", getNeighborPositions(Arrays.asList("A3", "B2", "C3", "E2")));

        adjacencyList.put("C1", getNeighborPositions(Arrays.asList("B1", "C2", "D3")));
        adjacencyList.put("C2", getNeighborPositions(Arrays.asList("B2", "C1", "C3")));
        adjacencyList.put("C3", getNeighborPositions(Arrays.asList("B3","C2","E1")));

        adjacencyList.put("D1", getNeighborPositions(Arrays.asList("A1", "D2", "H1")));
        adjacencyList.put("D2", getNeighborPositions(Arrays.asList("B1", "D1", "D3", "G1")));
        adjacencyList.put("D3", getNeighborPositions(Arrays.asList("C1","D2","F1")));

        adjacencyList.put("E1", getNeighborPositions(Arrays.asList("C3", "E2", "F3")));
        adjacencyList.put("E2", getNeighborPositions(Arrays.asList("B3", "E1", "E3", "G3")));
        adjacencyList.put("E3", getNeighborPositions(Arrays.asList("A3","E2","H3")));
        
        adjacencyList.put("F1", getNeighborPositions(Arrays.asList("D3", "G1", "F2")));
        adjacencyList.put("F2", getNeighborPositions(Arrays.asList("F1", "F3", "G2")));
        adjacencyList.put("F3", getNeighborPositions(Arrays.asList("E1","F2","G3")));


        adjacencyList.put("G1", getNeighborPositions(Arrays.asList("D2", "F1", "G2", "H1")));
        adjacencyList.put("G2", getNeighborPositions(Arrays.asList("F2", "G1", "G3", "H2")));
        adjacencyList.put("G3", getNeighborPositions(Arrays.asList("E2","F3","G2", "H2")));

        adjacencyList.put("H1", getNeighborPositions(Arrays.asList("D1", "G1", "H2")));
        adjacencyList.put("H2", getNeighborPositions(Arrays.asList("H1", "G2", "H3")));
        adjacencyList.put("H3", getNeighborPositions(Arrays.asList("E3", "G3","H2")));
    }
    /**
     * Takes a string of postion names and convert them to position objects 
     * @param positions
     * @return an array of positions
     */
    private List<Position> getNeighborPositions( List<String> positions) {
        List<Position> neighbours = new ArrayList<>();
        for(String pos: positions) {
            neighbours.add(new Position(pos));
        }
        return neighbours;
    }

    public List<Position> getNeighbors(String positionKey) {
        List<String> neighborKeys = new ArrayList<>();
        List<Position> neighbors = new ArrayList<>();
        for (String key : neighborKeys) {
            neighbors.add(positions.get(key));
        }
        return neighbors;
    }

    public Position getPosition(String positionKey) {
        return positions.get(positionKey);
    }

    public boolean isPositionOccupied(String positionKey) {
        Position position = getPosition(positionKey);
        return position != null && position.isOccupied();
    }

    public void resetBoard() {
        for (Position position : positions.values()) {
            position.release(); // Release all positions
        }
    }

    @Override
    public String toString() {
        return "GameBoard{" +
                "positions=" + positions.keySet() +
                ", adjacencyList=" + adjacencyList +
                '}';
    }
}
