package com.morabaraba;

import java.util.List;
import java.util.Map;

public class Main {



    public static void placeCow(Player player, Position position ,GameBoard board) {
            Position p = position;
            Cow cow = player.getUnplacedCows().get(0);

            
            List<Position> pNeighbours = board.getAdjacencyList().get(p);
            Position newCowPostition = player.moveCow(cow, p).getPosition();
            Map<Position, List<Position>> updatedAdjecencyList = board.getAdjacencyList();
            updatedAdjecencyList.put(newCowPostition, pNeighbours);
            
            for (Position pos: pNeighbours) {   
                updatedAdjecencyList.put(pos, pNeighbours.stream().map(neigbour -> neigbour.getName() == newCowPostition.getName() ? newCowPostition : neigbour).toList());
            }
            board.setAdjacencyList(updatedAdjecencyList);

    }
    public static void placementPhase(Player player1, Player player2, GameBoard board) {
        List<Position> positions = board.getUnoccupiedPositions();
        for (int i = 0; i < 12; i++) {
            placeCow(player1, positions.get(i), board);
        }
        for (int j = 12; j < 24; j++) {
            placeCow(player2, positions.get(j), board);
        }

        
    }


    public static void main(String[] args) {
        GameBoard board = new GameBoard();
        Player player1 = new Player("first",  board);
        
        Player player2 = new Player("second",  board);
        placementPhase(player1, player2, board);
        
        board.viewBoard();
    }
}
