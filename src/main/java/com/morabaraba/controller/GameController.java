package com.morabaraba.controller;

import com.morabaraba.Cow;
import com.morabaraba.GameBoard;
import com.morabaraba.Player;
import com.morabaraba.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure game logic controller with no I/O dependencies.
 * Accepts moves via handleInput() and notifies registered GameListeners.
 *
 * Websocket multiplayer: wrap this with a WebSocketAdapter that implements
 * GameListener (to send events to clients) and calls handleInput() when
 * it receives moves from remote clients. GameController stays unchanged.
 */
public class GameController {

    public enum Phase { PLACEMENT, SELECT_COW, SELECT_DEST, SHOOT }

    private final GameBoard board;
    private final Player player1;
    private final Player player2;
    private Player current;
    private Player opponent;
    private Phase phase;
    private Cow selectedCow;
    private List<Cow> shootCandidates;
    private final List<GameListener> listeners = new ArrayList<>();

    public GameController(GameBoard board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.current = player1;
        this.opponent = player2;
        updatePhase();
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /** Fire the initial state notification to prime the UI. */
    public void startGame() {
        notifyStateChanged(turnMessage());
    }

    /**
     * Single entry point for all user input.
     * @param positionName e.g. "A1", "B3"
     */
    public void handleInput(String positionName) {
        Position pos = board.getPositionByName(positionName);
        if (pos == null) {
            notifyMessage("Unknown position: " + positionName);
            return;
        }
        switch (phase) {
            case PLACEMENT:   handlePlacement(pos);  break;
            case SELECT_COW:  handleSelectCow(pos);  break;
            case SELECT_DEST: handleSelectDest(pos); break;
            case SHOOT:       handleShoot(pos);      break;
        }
    }

    // ── Phase handlers ───────────────────────────────────────────────────────

    private void handlePlacement(Position pos) {
        if (pos.isOccupied()) {
            notifyMessage("Position " + pos.getName() + " is already occupied.");
            return;
        }
        Cow cow = current.getUnplacedCows().get(0);
        current.moveCow(cow, pos);
        notifyCowMoved(null, pos.getName(), current == player1);

        if (board.checkMill(pos, current)) {
            computeShootCandidates();
            phase = Phase.SHOOT;
            notifyStateChanged("MILL! " + current.getName() + " — click an opponent cow to shoot.");
        } else {
            nextTurn();
        }
    }

    private void handleSelectCow(Position pos) {
        Cow cow = findCow(current, pos);
        if (cow == null) {
            notifyMessage("No cow of yours at " + pos.getName() + ". Pick one of your cows.");
            return;
        }
        selectedCow = cow;
        phase = Phase.SELECT_DEST;
        String extra = current.getPlacedCows().size() == 3 ? " (flying — any empty spot)" : " (adjacent empty spot)";
        notifyStateChanged("Cow at " + pos.getName() + " selected" + extra + ". Click destination.");
    }

    private void handleSelectDest(Position pos) {
        // Allow re-selecting a different cow
        Cow ownCow = findCow(current, pos);
        if (ownCow != null && ownCow != selectedCow) {
            selectedCow = ownCow;
            String extra = current.getPlacedCows().size() == 3 ? " (flying)" : "";
            notifyStateChanged("Now selected cow at " + pos.getName() + extra + ". Click destination.");
            return;
        }

        if (pos.isOccupied()) {
            notifyMessage("Position " + pos.getName() + " is occupied. Choose an empty destination.");
            return;
        }

        String before = selectedCow.getPosition().getName();
        current.moveCow(selectedCow, pos);

        if (!selectedCow.getPosition().getName().equals(before)) {
            // Move succeeded
            notifyCowMoved(before, pos.getName(), current == player1);
            if (board.checkMill(pos, current)) {
                computeShootCandidates();
                phase = Phase.SHOOT;
                notifyStateChanged("MILL! " + current.getName() + " — click an opponent cow to shoot.");
            } else {
                selectedCow = null;
                nextTurn();
            }
        } else {
            notifyMessage("Invalid move to " + pos.getName() + ". Must be adjacent (or any empty if flying).");
        }
    }

    private void handleShoot(Position pos) {
        Cow toRemove = null;
        for (Cow c : shootCandidates) {
            if (c.getPosition().getName().equals(pos.getName())) {
                toRemove = c;
                break;
            }
        }
        if (toRemove == null) {
            notifyMessage("Pick a highlighted opponent cow to shoot.");
            return;
        }
        opponent.removeCow(toRemove);
        selectedCow = null;
        shootCandidates = null;

        if (isGameOver(opponent)) {
            board.endGame();
            notifyStateChanged(current.getName() + " wins!");
            notifyGameOver(current);
        } else {
            nextTurn();
        }
    }

    // ── Turn management ──────────────────────────────────────────────────────

    private void nextTurn() {
        Player tmp = current;
        current = opponent;
        opponent = tmp;
        updatePhase();
        notifyStateChanged(turnMessage());
    }

    private void updatePhase() {
        phase = current.getUnplacedCows().isEmpty() ? Phase.SELECT_COW : Phase.PLACEMENT;
    }

    private String turnMessage() {
        int unplaced = current.getUnplacedCows().size();
        if (phase == Phase.PLACEMENT) {
            return current.getName() + "'s turn — click an empty position to place a cow (" + unplaced + " remaining).";
        }
        boolean flying = current.getPlacedCows().size() == 3;
        return current.getName() + "'s turn — click one of your cows to move" + (flying ? " (flying mode!)" : "") + ".";
    }

    // ── Win condition ────────────────────────────────────────────────────────

    private boolean isGameOver(Player p) {
        boolean allPlaced = p.getUnplacedCows().isEmpty();
        if (!allPlaced) return false;
        if (p.getPlacedCows().size() < 3) return true;
        // Flying players can always move if any empty spot exists
        if (p.getPlacedCows().size() == 3) {
            return board.getUnoccupiedPositions().isEmpty();
        }
        // Check if any cow has a valid move
        for (Cow c : p.getPlacedCows()) {
            List<Position> neighbours = board.getNeighbors(c.getPosition());
            if (neighbours != null) {
                for (Position n : neighbours) {
                    if (!n.isOccupied()) return false;
                }
            }
        }
        return true;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void computeShootCandidates() {
        List<Cow> all = opponent.getPlacedCows();
        List<Cow> notInMill = all.stream()
            .filter(c -> !board.checkMill(c.getPosition(), opponent))
            .collect(Collectors.toList());
        shootCandidates = notInMill.isEmpty() ? new ArrayList<>(all) : notInMill;
    }

    private Cow findCow(Player player, Position pos) {
        for (Cow c : player.getPlacedCows()) {
            if (c.getPosition().getName().equals(pos.getName())) return c;
        }
        return null;
    }

    private void notifyStateChanged(String message) {
        for (GameListener l : listeners) l.onStateChanged(message);
    }

    private void notifyMessage(String message) {
        for (GameListener l : listeners) l.onMessage(message);
    }

    private void notifyGameOver(Player winner) {
        for (GameListener l : listeners) l.onGameOver(winner);
    }

    private void notifyCowMoved(String from, String to, boolean isPlayer1) {
        for (GameListener l : listeners) l.onCowMoved(from, to, isPlayer1);
    }

    // ── Getters for GUI ──────────────────────────────────────────────────────

    public Phase getPhase() { return phase; }
    public Player getCurrentPlayer() { return current; }
    public Player getOpponentPlayer() { return opponent; }
    public Cow getSelectedCow() { return selectedCow; }
    public List<Cow> getShootCandidates() { return shootCandidates; }
    public GameBoard getBoard() { return board; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
}
