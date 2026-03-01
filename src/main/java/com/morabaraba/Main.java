package com.morabaraba;

import com.morabaraba.gui.GameApp;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameApp().setVisible(true));
    }
}
