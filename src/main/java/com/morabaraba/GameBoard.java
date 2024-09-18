package com.morabaraba;

import java.util.*;

public class GameBoard {
    private static Map<Position, List<Position>> adjacencyList; // Adjacency list for graph representation

    public GameBoard() {
        adjacencyList = new HashMap<>();
        initializeBoard();
    }
 

    public List<Position> getNeighbors(Position position) {
        return adjacencyList.get(position);
    }

    public Position getPosition(Position position) {
        return adjacencyList.containsKey(position) ? position: null;
    }

    public boolean isPositionOccupied(Position position) {
        return position != null && position.isOccupied();
    }

    public void resetBoard() {
        for(Map.Entry<Position, List<Position>> position: adjacencyList.entrySet()) {
            position.getKey().release(); // clears a postition marking it as unoccupied
        }
    }

    @Override
    public String toString() {
        return "GameBoard{" +
                "positions=" + adjacencyList.keySet() +
                ", adjacencyList=" + adjacencyList +
                '}';
    }

       /**
     * Initializes the board by createng a graph of positions mapped to thier neighbours
     */
    private static void initializeBoard() {


        // Set up adjacency list
        adjacencyList.put(new Position("A1"), getNeighborPositions(Arrays.asList("A2", "D1")));
        adjacencyList.put(new Position("A2"), getNeighborPositions(Arrays.asList("A1", "A3", "B2")));
        adjacencyList.put(new Position("A3"), getNeighborPositions(Arrays.asList("A2", "B3", "E3")));


        adjacencyList.put(new Position("B1"), getNeighborPositions(Arrays.asList("A1", "B2", "C1", "D2")));
        adjacencyList.put(new Position("B2"), getNeighborPositions(Arrays.asList("A2", "B1", "C2", "B3")));
        adjacencyList.put(new Position("B3"), getNeighborPositions(Arrays.asList("A3", "B2", "C3", "E2")));


        adjacencyList.put(new Position("C1"), getNeighborPositions(Arrays.asList("B1", "C2", "D3")));
        adjacencyList.put(new Position("C2"), getNeighborPositions(Arrays.asList("B2", "C1", "C3")));
        adjacencyList.put(new Position("C3"), getNeighborPositions(Arrays.asList("B3","C2","E1")));


        adjacencyList.put(new Position("D1"), getNeighborPositions(Arrays.asList("A1", "D2", "H1")));
        adjacencyList.put(new Position("D2"), getNeighborPositions(Arrays.asList("B1", "D1", "D3", "G1")));
        adjacencyList.put(new Position("D3"), getNeighborPositions(Arrays.asList("C1","D2","F1")));


        adjacencyList.put(new Position("E1"), getNeighborPositions(Arrays.asList("C3", "E2", "F3")));
        adjacencyList.put(new Position("E2"), getNeighborPositions(Arrays.asList("B3", "E1", "E3", "G3")));
        adjacencyList.put(new Position("E3"), getNeighborPositions(Arrays.asList("A3","E2","H3")));
        

        adjacencyList.put(new Position("F1"), getNeighborPositions(Arrays.asList("D3", "G1", "F2")));
        adjacencyList.put(new Position("F2"), getNeighborPositions(Arrays.asList("F1", "F3", "G2")));
        adjacencyList.put(new Position("F3"), getNeighborPositions(Arrays.asList("E1","F2","G3")));


        adjacencyList.put(new Position("G1"), getNeighborPositions(Arrays.asList("D2", "F1", "G2", "H1")));
        adjacencyList.put(new Position("G2"), getNeighborPositions(Arrays.asList("F2", "G1", "G3", "H2")));
        adjacencyList.put(new Position("G3"), getNeighborPositions(Arrays.asList("E2","F3","G2", "H2")));

        
        adjacencyList.put(new Position("H1"), getNeighborPositions(Arrays.asList("D1", "G1", "H2")));
        adjacencyList.put(new Position("H2"), getNeighborPositions(Arrays.asList("H1", "G2", "H3")));
        adjacencyList.put(new Position("H3"), getNeighborPositions(Arrays.asList("E3", "G3","H2")));
    }
    /**
     * Takes a string of postion names and convert them to position objects 
     * @param positions
     * @return an array of positions
     */
    private static List<Position> getNeighborPositions( List<String> positions) {
        List<Position> neighbours = new ArrayList<>();
        for(String pos: positions) {
            neighbours.add(new Position(pos));
        }
        return neighbours;
    }
}
