package com.morabaraba.gameHelpers;
import com.morabaraba.Player;
import com.morabaraba.Position;
import com.morabaraba.Cow;
import com.morabaraba.GameBoard;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GamePlay {
    Player player1;
    Player player2;
    
    public GamePlay(Player player1, Player player2, GameBoard board) {

        int turnCount = 1;
        // System.out.println("Starting turns");
        while (!board.gameOver()) {
            Player player = playerTurn(turnCount, player1, player2); // returns the player that has to play the turn
            List<Position> freePosition = board.getUnoccupiedPositions();
            Scanner sc = new Scanner(System.in);
            System.out.println("\nPlease Enter a postion name from the ones below:\n");
            freePosition.stream().map( pos -> pos.getName()).forEach(name -> System.out.print(name + " "));
            break;
        }
    }

    public static void placeCow(Player player, Position position ,GameBoard board) {
        Position p = position;
        Cow cow = player.getUnplacedCows().get(0);

        
        Position newCowPostition = player.moveCow(cow, p).getPosition();
        Map<Position, List<Position>> updatedAdjecencyList = board.getAdjacencyList();

        // List<Position> pNeighbours = board.getAdjacencyList().get(p); 
        // updatedAdjecencyList.put(newCowPostition, pNeighbours);
        
        // for (Position pos: pNeighbours) {   
        //     updatedAdjecencyList.put(pos, pNeighbours.stream().map(neigbour -> neigbour.getName() == newCowPostition.getName() ? newCowPostition : neigbour).toList());
        // }
        board.setAdjacencyList(updatedAdjecencyList);
    }

    /**
     * 
     * @param turnCount an odd or even number if odd its player1 turn and player2 turn if even
     * @param player1 
     * @param player2
     * @return The player who has to play the next turn
     */
    private Player playerTurn(int turnCount,Player player1,Player player2) {
        boolean even = (turnCount/2) == 0;
        return even  ?  player2: player1;
    }
}
