package dk.dtu;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class GameControls extends KeyAdapter {

	public boolean rightClick;
	public boolean leftClick;

	public GameControls() {

		// setFocusable(true);
		// addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			leftClick = true;
			break;

		case KeyEvent.VK_RIGHT:
			rightClick = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			leftClick = false;
			break;

		case KeyEvent.VK_RIGHT:
			rightClick = false;
			break;
		}
	}

	public boolean getLeft() {
		return leftClick;
	}

	public boolean getRight() {
		return rightClick;

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
