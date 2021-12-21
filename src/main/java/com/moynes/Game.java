package com.moynes;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class Game {

    JFrame frame;
    GameCanvas canvas;
    GameState gameState;
    KeyState keyState;

    public Game(JFrame frame, GameCanvas canvas, GameState gameState, KeyState keyState) {
        this.frame = frame;
        this.keyState = keyState;
        this.canvas = canvas;
        this.gameState = gameState;

        frame.setVisible(true);
        frame.add(canvas);
        frame.pack();
        frame.addKeyListener(new KeyListener(keyState));
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }


    private static boolean update(GameState gameState, KeyState keyState, long dt) {
        return gameState.update(keyState, dt);

    }

    public static JFrame frame(int width, int height) {
        JFrame frame = new JFrame("Demo");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();

        return frame;
    }

    public void run() {
        long lastTime = getCurrentTime();

        while (true) {
            long current = getCurrentTime();
            long elapsed = current - lastTime;
            if (elapsed > 0) {
                if (update(gameState, keyState, elapsed))
                    canvas.render(elapsed);
                lastTime = current;
            }
        }

    }
}
