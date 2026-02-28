package com.morabaraba;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GameBoard {
    private  Map<Position, List<Position>> adjacencyList; // Adjacency list for graph representation
    private boolean gameON = true;

    public static final List<List<String>> VALID_MILLS = Arrays.asList(
        // Outer square
        Arrays.asList("A1", "A2", "A3"), Arrays.asList("A3", "E3", "H3"),
        Arrays.asList("H3", "H2", "H1"), Arrays.asList("H1", "D1", "A1"),
        // Middle square
        Arrays.asList("B1", "B2", "B3"), Arrays.asList("B3", "E2", "G3"),
        Arrays.asList("G3", "G2", "G1"), Arrays.asList("G1", "D2", "B1"),
        // Inner square
        Arrays.asList("C1", "C2", "C3"), Arrays.asList("C3", "E1", "F3"),
        Arrays.asList("F3", "F2", "F1"), Arrays.asList("F1", "D3", "C1"),
        // Spokes
        Arrays.asList("A2", "B2", "C2"), Arrays.asList("E3", "E2", "E1"),
        Arrays.asList("H2", "G2", "F2"), Arrays.asList("D1", "D2", "D3")
    );

    public GameBoard() {    //The game board is represented as a graph
        adjacencyList = new HashMap<>();
        initializeBoard();
    }

    /**
     *
     * @return returns the board graph adjacency list
     */
    public  Map<Position, List<Position>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * returns a new game state typically when a movve is made
     * @param adjacencyList
     */
    public  void setAdjacencyList(Map<Position, List<Position>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    /**
     * Finds a canonical Position on the board by its string name.
     * @param name the position name (e.g. "A1")
     * @return the matching Position object, or null if not found
     */
    public Position getPositionByName(String name) {
        for (Position pos : adjacencyList.keySet()) {
            if (pos.getName().equals(name)) return pos;
        }
        return null;
    }

    /**
     * Finds the neighbours of a node in the adjacency list.
     * Returns canonical Position objects (with correct occupied state),
     * not the stubs stored in the adjacency list values.
     * @param position
     * @return A list of neighbouring positions
     */
    public List<Position> getNeighbors(Position position) {
        List<Position> stubs = adjacencyList.get(position);
        if (stubs == null) return null;
        return stubs.stream()
            .map(s -> getPositionByName(s.getName()))
            .filter(p -> p != null)
            .collect(Collectors.toList());
    }

    /**
     *  checks if a position exists
     * @param position
     * @return returns the position if it does and returns null otherwise
     */
    public Position getPosition(Position position) {
        return adjacencyList.containsKey(position) ? position: null;
    }

    /**
     * Checks if a position is occupied
     * @param position
     * @return true if occupied and false otherwise
     */
    public boolean isPositionOccupied(Position position) {
        return position != null && position.isOccupied();
    }


    /**
     * This method clears the board
     */
    public void resetBoard() {
        for(Map.Entry<Position, List<Position>> position: adjacencyList.entrySet()) {
            position.getKey().release(); // clears a postition marking it as unoccupied
        }
    }

    /**
     * Gets the list of all unoccupied positions
     * @return A list of all positions that are not occupied
     */
    public List<Position> getUnoccupiedPositions() {
        List<Position> unoccupiedPositions = new ArrayList<>();

        for (Map.Entry<Position, List<Position>> entry : adjacencyList.entrySet()) {
            Position position = entry.getKey();
            if (!position.isOccupied()) {
                unoccupiedPositions.add(position);
            }
        }

        return unoccupiedPositions;
    }

    /**
     * Checks whether the given position is part of a completed mill owned by the given player.
     * @param position the position just placed or moved to
     * @param player the player who just moved
     * @return true if a mill was formed, false otherwise
     */
    public boolean checkMill(Position position, Player player) {
        for (List<String> mill : VALID_MILLS) {
            if (!mill.contains(position.getName())) continue;
            boolean allOwned = mill.stream().allMatch(name -> {
                Position p = getPositionByName(name);
                return p != null && p.isOccupied() &&
                    player.getPlacedCows().stream()
                        .anyMatch(c -> c.getPosition() != null &&
                                      c.getPosition().getName().equals(name));
            });
            if (allOwned) return true;
        }
        return false;
    }

    public void endGame() {
        this.gameON = false;
    }

    public boolean gameOver() {
        return gameON == false;
    }


    /**
     * Displays the game state on the terminal
     */
    public void viewBoard() {
        String fileName = "gameboard.txt";
        try {
            List<String> fileContent = Files.readAllLines(Paths.get("src", "resources", fileName));

            for (String line : fileContent) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    private  void initializeBoard() {


        // Set up adjacency list
        adjacencyList.put(new Position("A1"), getNeighborPositions(Arrays.asList("A2", "B1", "D1")));
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
