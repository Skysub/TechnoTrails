package dk.dtu;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class GameControls extends JFrame implements KeyListener {

    private int directionx = 0;
    private int directiony = 0;

    public GameControls() {
        setFocusable(true);
        addKeyListener(this);
    }

    public int getDirectionX() {
        return directionx;
    }

    public int getDirectionY() {
        return directiony;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                directionx = 0;
                directiony = -1;
                break;
            case KeyEvent.VK_DOWN:
                directionx = 0;
                directiony = 1;
                break;
            case KeyEvent.VK_LEFT:
                directionx = -1;
                directiony = 0;
                break;
            case KeyEvent.VK_RIGHT:
                directionx = 1;
                directiony = 0;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this example
    }

}
