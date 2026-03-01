package com.morabaraba.controller;

import com.morabaraba.Player;

/**
 * Observer interface for game state changes.
 * Implement this to receive events from GameController.
 *
 * Future multiplayer: a WebSocketAdapter implementing this interface
 * can serialise events to JSON and broadcast to remote clients,
 * without any changes to GameController.
 */
public interface GameListener {
    /** Board state changed — redraw and update UI. */
    void onStateChanged(String message);

    /** Informational message (e.g. invalid move) — update status label only. */
    void onMessage(String message);

    /** Game has ended. */
    void onGameOver(Player winner);

    /**
     * A cow was placed or moved.  Called BEFORE the corresponding onStateChanged.
     * @param from  source position name, or {@code null} for a placement
     * @param to    destination position name
     * @param isPlayer1 true if the cow belongs to player 1
     */
    default void onCowMoved(String from, String to, boolean isPlayer1) {}
}
