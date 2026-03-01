package com.morabaraba.gui;

import com.morabaraba.GameBoard;
import com.morabaraba.Player;
import com.morabaraba.controller.GameController;
import com.morabaraba.controller.GameListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Main application window.
 * Wires together GameController (logic) and BoardPanel (view).
 * Implements GameListener to update the UI when game state changes.
 */
public class GameApp extends JFrame implements GameListener {

    private GameController controller;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel p1CowsLabel;
    private JLabel p2CowsLabel;
    private JLabel p1TurnIndicator;
    private JLabel p2TurnIndicator;

    public GameApp() {
        super("Morabaraba");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(480, 420));

        buildMenu();
        initGame();
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    // ── Game lifecycle ────────────────────────────────────────────────────────

    private void initGame() {
        GameBoard board = new GameBoard();
        Player p1 = new Player("Player 1", board);
        Player p2 = new Player("Player 2", board);
        controller = new GameController(board, p1, p2);
        controller.addListener(this);
    }

    private void startNewGame() {
        initGame();
        boardPanel.setController(controller);
        controller.startGame();
        updateSidePanel();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.setBackground(new Color(30, 60, 30));

        boardPanel = new BoardPanel(controller);

        root.add(buildSidePanel(), BorderLayout.WEST);
        root.add(boardPanel, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        controller.startGame();
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(30, 60, 30));
        side.setPreferredSize(new Dimension(150, 0));

        side.add(buildPlayerCard("Player 1", BoardPanel.P1_COLOR, true));
        side.add(Box.createVerticalStrut(20));
        side.add(buildPlayerCard("Player 2", BoardPanel.P2_COLOR, false));
        side.add(Box.createVerticalGlue());

        return side;
    }

    private JPanel buildPlayerCard(String name, Color color, boolean isP1) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 75, 40));
        card.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(color, 2), name,
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12), color));

        JLabel cowsLabel = new JLabel("Cows: 12", SwingConstants.CENTER);
        cowsLabel.setForeground(Color.WHITE);
        cowsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cowsLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel indicator = new JLabel("◀ YOUR TURN", SwingConstants.CENTER);
        indicator.setForeground(Color.YELLOW);
        indicator.setFont(new Font("SansSerif", Font.BOLD, 11));
        indicator.setAlignmentX(CENTER_ALIGNMENT);
        indicator.setVisible(isP1); // player 1 goes first

        // Colour swatch
        JPanel swatch = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(10, 5, 30, 30);
                g.setColor(Color.BLACK);
                g.drawOval(10, 5, 30, 30);
            }
        };
        swatch.setPreferredSize(new Dimension(50, 40));
        swatch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        swatch.setBackground(new Color(40, 75, 40));
        swatch.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(6));
        card.add(swatch);
        card.add(Box.createVerticalStrut(4));
        card.add(cowsLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(indicator);
        card.add(Box.createVerticalStrut(6));

        if (isP1) {
            p1CowsLabel = cowsLabel;
            p1TurnIndicator = indicator;
        } else {
            p2CowsLabel = cowsLabel;
            p2TurnIndicator = indicator;
        }

        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(20, 40, 20));
        bar.setBorder(new EmptyBorder(6, 10, 6, 10));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bar.add(statusLabel, BorderLayout.CENTER);
        return bar;
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Start a new game?", "New Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) startNewGame();
        });

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.addSeparator();
        gameMenu.add(quit);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    // ── Side panel update ─────────────────────────────────────────────────────

    private void updateSidePanel() {
        Player p1 = controller.getPlayer1();
        Player p2 = controller.getPlayer2();
        Player cur = controller.getCurrentPlayer();

        int p1Total = p1.getPlacedCows().size() + p1.getUnplacedCows().size();
        int p2Total = p2.getPlacedCows().size() + p2.getUnplacedCows().size();
        p1CowsLabel.setText("Cows: " + p1Total);
        p2CowsLabel.setText("Cows: " + p2Total);

        p1TurnIndicator.setVisible(cur == p1);
        p2TurnIndicator.setVisible(cur == p2);
    }

    // ── GameListener implementation ───────────────────────────────────────────

    @Override
    public void onCowMoved(String from, String to, boolean isPlayer1) {
        Color c = isPlayer1 ? BoardPanel.P1_COLOR : BoardPanel.P2_COLOR;
        SwingUtilities.invokeLater(() -> boardPanel.animateMove(from, to, c));
    }

    @Override
    public void onStateChanged(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            boardPanel.repaint();
            updateSidePanel();
        });
    }

    @Override
    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }

    @Override
    public void onGameOver(Player winner) {
        SwingUtilities.invokeLater(() -> {
            boardPanel.repaint();
            updateSidePanel();
            int choice = JOptionPane.showOptionDialog(
                this,
                winner.getName() + " wins! Play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"New Game", "Quit"},
                "New Game"
            );
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
            } else {
                System.exit(0);
            }
        });
    }
}
