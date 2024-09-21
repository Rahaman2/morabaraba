package com.morabaraba;


public class Main {
    public static void main(String[] args) {
        GameBoard board = new GameBoard();
        System.out.println( board.getUnoccupiedPositions().size());
    }
}