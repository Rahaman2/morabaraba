package com.morabaraba.gameHelpers;

import com.morabaraba.Player;
import com.morabaraba.Position;
import com.morabaraba.Cow;
import com.morabaraba.GameBoard;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GamePlay {
    Player player1;
    Player player2;

    public GamePlay(Player player1, Player player2, GameBoard board) {
        this.player1 = player1;
        this.player2 = player2;

        System.out.println("=== Welcome to Morabaraba! ===");
        System.out.println("Each player has 12 cows. Place them on the board, then move them.");
        System.out.println("Form a mill (3 in a row) to shoot an opponent's cow.");
        System.out.println("Win by reducing your opponent to fewer than 3 cows.\n");

        Scanner sc = new Scanner(System.in);
        int turnCount = 1;

        while (!board.gameOver()) {
            boolean isOdd = (turnCount % 2) == 1;
            Player current  = isOdd ? player1 : player2;
            Player opponent = isOdd ? player2 : player1;

            board.viewBoard();
            System.out.println("\n--- " + current.getName() + "'s turn ---");
            System.out.println("Cows remaining to place: " + current.getUnplacedCows().size());

            boolean placementPhase = !current.getUnplacedCows().isEmpty();

            if (placementPhase) {
                handlePlacement(sc, current, opponent, board);
            } else {
                handleMovement(sc, current, opponent, board);
            }

            if (isGameOver(opponent, board)) {
                board.viewBoard();
                System.out.println("\n=== " + current.getName() + " wins! ===");
                board.endGame();
                break;
            }

            turnCount++;
        }

        sc.close();
    }

    private void handlePlacement(Scanner sc, Player current, Player opponent, GameBoard board) {
        System.out.println("Available positions: ");
        board.getUnoccupiedPositions().stream()
            .map(Position::getName)
            .sorted()
            .forEach(name -> System.out.print(name + " "));
        System.out.println();

        Position target = promptPosition(sc, board, "Enter a position to place your cow: ");
        if (target == null || target.isOccupied()) {
            System.out.println("Invalid or occupied position. Turn skipped.");
            return;
        }

        placeCow(current, target, board);

        if (board.checkMill(target, current)) {
            System.out.println("*** MILL! " + current.getName() + " formed a mill! ***");
            handleShoot(sc, current, opponent, board);
        }
    }

    private void handleMovement(Scanner sc, Player current, Player opponent, GameBoard board) {
        System.out.println("Your cows are at: ");
        current.getPlacedCows().stream()
            .map(c -> c.getPosition().getName())
            .sorted()
            .forEach(name -> System.out.print(name + " "));
        System.out.println();

        Position from = promptPosition(sc, board, "Enter the position of the cow to move: ");
        if (from == null) {
            System.out.println("Unknown position. Turn skipped.");
            return;
        }

        Cow cowToMove = null;
        for (Cow c : current.getPlacedCows()) {
            if (c.getPosition().getName().equals(from.getName())) {
                cowToMove = c;
                break;
            }
        }
        if (cowToMove == null) {
            System.out.println("No cow at " + from.getName() + ". Turn skipped.");
            return;
        }

        boolean canFly = (current.getPlacedCows().size() == 3);
        if (canFly) {
            System.out.println("(Flying mode — you can move to any empty position)");
            System.out.println("Available positions: ");
            board.getUnoccupiedPositions().stream()
                .map(Position::getName)
                .sorted()
                .forEach(name -> System.out.print(name + " "));
            System.out.println();
        } else {
            List<Position> neighbours = board.getNeighbors(from);
            System.out.println("Valid moves from " + from.getName() + ": ");
            if (neighbours != null) {
                neighbours.stream()
                    .filter(n -> !n.isOccupied())
                    .map(Position::getName)
                    .sorted()
                    .forEach(name -> System.out.print(name + " "));
            }
            System.out.println();
        }

        Position to = promptPosition(sc, board, "Enter the destination: ");
        if (to == null) {
            System.out.println("Unknown position. Turn skipped.");
            return;
        }

        String beforeMove = cowToMove.getPosition().getName();
        current.moveCow(cowToMove, to);

        if (!cowToMove.getPosition().getName().equals(beforeMove)) {
            if (board.checkMill(to, current)) {
                System.out.println("*** MILL! " + current.getName() + " formed a mill! ***");
                handleShoot(sc, current, opponent, board);
            }
        } else {
            System.out.println("Invalid move. Turn skipped.");
        }
    }

    private void handleShoot(Scanner sc, Player shooter, Player target, GameBoard board) {
        List<Cow> allCows = target.getPlacedCows();
        List<Cow> notInMill = allCows.stream()
            .filter(c -> !board.checkMill(c.getPosition(), target))
            .collect(Collectors.toList());
        List<Cow> candidates = notInMill.isEmpty() ? allCows : notInMill;

        System.out.println("Opponent cows you can shoot: ");
        candidates.stream()
            .map(c -> c.getPosition().getName())
            .sorted()
            .forEach(name -> System.out.print(name + " "));
        System.out.println();

        Position shootPos = promptPosition(sc, board, "Enter position of cow to shoot: ");
        if (shootPos == null) {
            System.out.println("Unknown position. No cow removed.");
            return;
        }

        Cow toRemove = null;
        for (Cow c : candidates) {
            if (c.getPosition().getName().equals(shootPos.getName())) {
                toRemove = c;
                break;
            }
        }
        if (toRemove != null) {
            target.removeCow(toRemove);
            System.out.println("Cow at " + shootPos.getName() + " has been shot!");
        } else {
            System.out.println("Invalid target. No cow removed.");
        }
    }

    private boolean isGameOver(Player opponent, GameBoard board) {
        boolean allPlaced = opponent.getUnplacedCows().isEmpty();
        if (allPlaced && opponent.getPlacedCows().size() < 3) {
            return true;
        }
        // If all cows are placed and none can move, the opponent loses
        if (allPlaced && opponent.getPlacedCows().size() >= 3) {
            boolean canFly = (opponent.getPlacedCows().size() == 3);
            if (canFly) {
                return board.getUnoccupiedPositions().isEmpty();
            }
            for (Cow c : opponent.getPlacedCows()) {
                List<Position> neighbours = board.getNeighbors(c.getPosition());
                if (neighbours != null) {
                    for (Position n : neighbours) {
                        if (!n.isOccupied()) return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private Position promptPosition(Scanner sc, GameBoard board, String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim().toUpperCase();
        Position pos = board.getPositionByName(input);
        if (pos == null) {
            System.out.println("Unknown position: " + input);
        }
        return pos;
    }

    public static void placeCow(Player player, Position position, GameBoard board) {
        Cow cow = player.getUnplacedCows().get(0);
        player.moveCow(cow, position);
        board.setAdjacencyList(board.getAdjacencyList());
    }

    private Player playerTurn(int turnCount, Player player1, Player player2) {
        boolean isOdd = (turnCount % 2) == 1;
        return isOdd ? player1 : player2;
    }
}
