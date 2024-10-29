package com.morabaraba;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.morabaraba.gameHelpers.GamePlay;

public class Main {

    public static void main(String[] args) {
        GameBoard board = new GameBoard();
        Player player1 = new Player("first",  board);
        
        Player player2 = new Player("second",  board);
        new GamePlay(player1, player2, board);
        
        // board.viewBoard();
    }
}
