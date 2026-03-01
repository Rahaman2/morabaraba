package com.morabaraba.gui;

import com.morabaraba.Cow;
import com.morabaraba.Player;
import com.morabaraba.controller.GameController;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom JPanel that draws the Morabaraba board and handles mouse clicks.
 * All positions are stored as normalised coordinates [0.0–1.0] and scaled
 * to pixel space at paint time, so the board resizes with the window.
 */
public class BoardPanel extends JPanel {

    // Player colours (package-visible for GameApp)
    static final Color P1_COLOR       = new Color(220, 50,  50);
    static final Color P2_COLOR       = new Color(50,  100, 220);
    private static final Color EMPTY_COLOR    = new Color(180, 170, 150);
    private static final Color SELECTED_COLOR = new Color(255, 220, 0);
    private static final Color SHOOT_COLOR    = new Color(255, 140, 0);
    private static final Color BOARD_BG       = new Color(50,  90,  50);
    private static final Color LINE_COLOR     = new Color(210, 185, 140);

    /**
     * Normalised position coordinates in [0.0, 1.0].
     * Derived from original 600×600 layout: norm = px / 600.
     *   Outer square edges at 0.1 / 0.9
     *   Middle square edges at 0.233 / 0.767
     *   Inner square edges at 0.367 / 0.633
     */
    private static final Map<String, double[]> NORM_COORDS = new LinkedHashMap<>();
    static {
        // Outer square
        NORM_COORDS.put("A1", new double[]{0.100, 0.100});
        NORM_COORDS.put("A2", new double[]{0.500, 0.100});
        NORM_COORDS.put("A3", new double[]{0.900, 0.100});
        NORM_COORDS.put("E3", new double[]{0.900, 0.500});
        NORM_COORDS.put("H3", new double[]{0.900, 0.900});
        NORM_COORDS.put("H2", new double[]{0.500, 0.900});
        NORM_COORDS.put("H1", new double[]{0.100, 0.900});
        NORM_COORDS.put("D1", new double[]{0.100, 0.500});
        // Middle square
        NORM_COORDS.put("B1", new double[]{0.233, 0.233});
        NORM_COORDS.put("B2", new double[]{0.500, 0.233});
        NORM_COORDS.put("B3", new double[]{0.767, 0.233});
        NORM_COORDS.put("E2", new double[]{0.767, 0.500});
        NORM_COORDS.put("G3", new double[]{0.767, 0.767});
        NORM_COORDS.put("G2", new double[]{0.500, 0.767});
        NORM_COORDS.put("G1", new double[]{0.233, 0.767});
        NORM_COORDS.put("D2", new double[]{0.233, 0.500});
        // Inner square
        NORM_COORDS.put("C1", new double[]{0.367, 0.367});
        NORM_COORDS.put("C2", new double[]{0.500, 0.367});
        NORM_COORDS.put("C3", new double[]{0.633, 0.367});
        NORM_COORDS.put("E1", new double[]{0.633, 0.500});
        NORM_COORDS.put("F3", new double[]{0.633, 0.633});
        NORM_COORDS.put("F2", new double[]{0.500, 0.633});
        NORM_COORDS.put("F1", new double[]{0.367, 0.633});
        NORM_COORDS.put("D3", new double[]{0.367, 0.500});
    }

    /** Board edges as pairs of position names. */
    private static final String[][] EDGES = {
        // Outer square
        {"A1","A2"}, {"A2","A3"}, {"A3","E3"}, {"E3","H3"},
        {"H3","H2"}, {"H2","H1"}, {"H1","D1"}, {"D1","A1"},
        // Middle square
        {"B1","B2"}, {"B2","B3"}, {"B3","E2"}, {"E2","G3"},
        {"G3","G2"}, {"G2","G1"}, {"G1","D2"}, {"D2","B1"},
        // Inner square
        {"C1","C2"}, {"C2","C3"}, {"C3","E1"}, {"E1","F3"},
        {"F3","F2"}, {"F2","F1"}, {"F1","D3"}, {"D3","C1"},
        // Spokes
        {"A2","B2"}, {"B2","C2"},
        {"E3","E2"}, {"E2","E1"},
        {"H2","G2"}, {"G2","F2"},
        {"D1","D2"}, {"D2","D3"},
        // Corner diagonals
        {"A1","B1"}, {"B1","C1"},
        {"A3","B3"}, {"B3","C3"},
        {"H3","G3"}, {"G3","F3"},
        {"H1","G1"}, {"G1","F1"},
    };

    private GameController controller;
    private MoveAnim activeAnim;
    private Timer animTimer;

    // ── Animation ─────────────────────────────────────────────────────────────

    private static class MoveAnim {
        final double[] fromNorm, toNorm;
        final Color color;
        final String toPos;
        final boolean placement; // true → scale-in at destination; false → slide
        double t; // progress 0.0 → 1.0

        MoveAnim(double[] fromNorm, double[] toNorm, Color color, String toPos, boolean placement) {
            this.fromNorm = fromNorm;
            this.toNorm   = toNorm;
            this.color     = color;
            this.toPos     = toPos;
            this.placement = placement;
        }
    }

    /**
     * Start a cow animation.
     * @param from source position name, or {@code null} for a placement (scale-in)
     * @param to   destination position name
     * @param color cow colour to draw
     */
    public void animateMove(String from, String to, Color color) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        double[] toN = NORM_COORDS.get(to);
        if (toN == null) { repaint(); return; }
        boolean placement = (from == null);
        double[] fromN = placement ? toN : NORM_COORDS.get(from);
        if (fromN == null) fromN = toN;
        activeAnim = new MoveAnim(fromN, toN, color, to, placement);
        animTimer = new Timer(16, e -> {
            activeAnim.t = Math.min(1.0, activeAnim.t + 16.0 / 350.0);
            if (activeAnim.t >= 1.0) {
                ((Timer) e.getSource()).stop();
                activeAnim = null;
            }
            repaint();
        });
        animTimer.start();
        repaint();
    }

    private double easeInOut(double t) {
        return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    }

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setBackground(BOARD_BG);
        setPreferredSize(new Dimension(600, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String pos = nearestPosition(e.getX(), e.getY());
                if (pos != null) controller.handleInput(pos);
            }
        });
    }

    public void setController(GameController controller) {
        this.controller = controller;
        repaint();
    }

    // ── Coordinate helpers ────────────────────────────────────────────────────

    /** The pixel size of the board square drawn inside this panel. */
    private int boardSize() {
        return Math.min(getWidth(), getHeight());
    }

    /** Left edge of the centred board square. */
    private int boardOffsetX() {
        return (getWidth() - boardSize()) / 2;
    }

    /** Top edge of the centred board square. */
    private int boardOffsetY() {
        return (getHeight() - boardSize()) / 2;
    }

    /** Convert a normalised position to pixel [x, y]. */
    private int[] toPixel(double[] norm) {
        int s = boardSize();
        return new int[]{
            boardOffsetX() + (int) (norm[0] * s),
            boardOffsetY() + (int) (norm[1] * s)
        };
    }

    private int nodeRadius() {
        return Math.max(8, (int) (boardSize() * 0.027));
    }

    private int clickRadius() {
        return Math.max(14, (int) (boardSize() * 0.05));
    }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawEdges(g2);
        drawNodes(g2);
    }

    private void drawEdges(Graphics2D g2) {
        float lineWidth = Math.max(1.5f, boardSize() * 0.005f);
        g2.setColor(LINE_COLOR);
        g2.setStroke(new BasicStroke(lineWidth));
        for (String[] edge : EDGES) {
            double[] na = NORM_COORDS.get(edge[0]);
            double[] nb = NORM_COORDS.get(edge[1]);
            if (na == null || nb == null) continue;
            int[] a = toPixel(na);
            int[] b = toPixel(nb);
            g2.drawLine(a[0], a[1], b[0], b[1]);
        }
    }

    private void drawNodes(Graphics2D g2) {
        Player p1 = controller.getPlayer1();
        Player p2 = controller.getPlayer2();
        Cow selected = controller.getSelectedCow();
        List<Cow> shootTargets = controller.getShootCandidates();
        int r = nodeRadius();
        int d = r * 2;
        int fontSize = Math.max(7, (int) (boardSize() * 0.018));
        String skipPos = activeAnim != null ? activeAnim.toPos : null;

        for (Map.Entry<String, double[]> entry : NORM_COORDS.entrySet()) {
            String name = entry.getKey();
            if (name.equals(skipPos)) continue; // drawn by animation overlay
            int[] c = toPixel(entry.getValue());

            Cow cowHere = findCowAt(name, p1, p2);
            Color fill;
            Color border = Color.BLACK;
            float borderWidth = 2f;

            if (cowHere == null) {
                fill = EMPTY_COLOR;
            } else if (isShootTarget(cowHere, shootTargets)) {
                fill = SHOOT_COLOR;
                border = Color.RED;
                borderWidth = 3f;
            } else if (cowHere == selected) {
                fill = SELECTED_COLOR;
                border = Color.DARK_GRAY;
                borderWidth = 3f;
            } else if (cowHere.getOwner() == p1) {
                fill = P1_COLOR;
            } else {
                fill = P2_COLOR;
            }

            g2.setColor(fill);
            g2.fillOval(c[0] - r, c[1] - r, d, d);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.drawOval(c[0] - r, c[1] - r, d, d);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int lw = fm.stringWidth(name);
            g2.drawString(name, c[0] - lw / 2, c[1] + fm.getAscent() / 2 - 1);
        }

        if (activeAnim != null) drawAnimatedCow(g2, r, fontSize);
    }

    private void drawAnimatedCow(Graphics2D g2, int baseRadius, int fontSize) {
        MoveAnim a = activeAnim;
        double ease = easeInOut(a.t);
        int x, y, radius;

        if (a.placement) {
            // Scale from 0 → full size at the destination
            int[] c = toPixel(a.toNorm);
            x = c[0]; y = c[1];
            radius = (int) (baseRadius * ease);
        } else {
            // Slide from source to destination
            double nx = a.fromNorm[0] + (a.toNorm[0] - a.fromNorm[0]) * ease;
            double ny = a.fromNorm[1] + (a.toNorm[1] - a.fromNorm[1]) * ease;
            int[] c = toPixel(new double[]{nx, ny});
            x = c[0]; y = c[1];
            radius = baseRadius;
        }

        if (radius <= 0) return;
        int d = radius * 2;
        g2.setColor(a.color);
        g2.fillOval(x - radius, y - radius, d, d);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x - radius, y - radius, d, d);

        if (radius >= baseRadius / 2) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int lw = fm.stringWidth(a.toPos);
            g2.drawString(a.toPos, x - lw / 2, y + fm.getAscent() / 2 - 1);
        }
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    private Cow findCowAt(String posName, Player p1, Player p2) {
        for (Cow c : p1.getPlacedCows()) {
            if (c.getPosition() != null && c.getPosition().getName().equals(posName)) return c;
        }
        for (Cow c : p2.getPlacedCows()) {
            if (c.getPosition() != null && c.getPosition().getName().equals(posName)) return c;
        }
        return null;
    }

    private boolean isShootTarget(Cow cow, List<Cow> targets) {
        return targets != null && targets.contains(cow);
    }

    private String nearestPosition(int px, int py) {
        String best = null;
        double bestDist = clickRadius() + 1;
        for (Map.Entry<String, double[]> entry : NORM_COORDS.entrySet()) {
            int[] c = toPixel(entry.getValue());
            double dist = Math.hypot(px - c[0], py - c[1]);
            if (dist < bestDist) {
                bestDist = dist;
                best = entry.getKey();
            }
        }
        return best;
    }
}
