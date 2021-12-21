package com.moynes;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;

@Slf4j
public class KeyListener implements java.awt.event.KeyListener {

    KeyState keyState;
    public KeyListener(KeyState keyState){
        this.keyState = keyState;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    static final int KEY_UP_ARROW = 38;
    static final int KEY_DOWN_ARROW = 40;
    static final int KEY_LEFT_ARROW = 37;
    static final int KEY_RIGHT_ARROW = 39;
    static final int KEY_SPACE = 32;
    static final int KEY_C = 67;

    @Override
    public void keyPressed(KeyEvent e) {
        log.debug("Pressed: {}", e);
        switch(e.getKeyCode()){
            case KEY_UP_ARROW -> keyState.UP_KEY_DOWN = true;
            case KEY_DOWN_ARROW -> keyState.DOWN_KEY_DOWN = true;
            case KEY_LEFT_ARROW -> keyState.LEFT_KEY_DOWN = true;
            case KEY_RIGHT_ARROW -> keyState.RIGHT_KEY_DOWN = true;
            case KEY_SPACE -> keyState.SPACE_KEY_DOWN = true;
            case KEY_C -> keyState.C_KEY_DOWN = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        log.debug("Released: {}", e);
        switch(e.getKeyCode()){
            case KEY_UP_ARROW -> keyState.UP_KEY_DOWN = false;
            case KEY_DOWN_ARROW -> keyState.DOWN_KEY_DOWN = false;
            case KEY_LEFT_ARROW -> keyState.LEFT_KEY_DOWN = false;
            case KEY_RIGHT_ARROW -> keyState.RIGHT_KEY_DOWN = false;
            case KEY_SPACE -> keyState.SPACE_KEY_DOWN = false;
            case KEY_C -> keyState.C_KEY_DOWN = false;
        }
    }
}
