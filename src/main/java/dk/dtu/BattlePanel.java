package dk.dtu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class BattlePanel extends JPanel {
	private CustomDrawingPanel drawingPanel;
	private Client client;
	private Timer winnerTimer;
	private int winnerTimeLeft = -10;
	Timer drawGameTimer;
	String winnerString = "unset";
	// static final int SCREEN_HEIGHT = 720; //There shouldn't be standard values
	// like this
	// static final int SCREEN_WIDTH = 1280;
	static final int fps = 60;
	static final Color playerColors[] = new Color[] { Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA, Color.ORANGE,
			Color.BLUE, Color.GRAY, Color.PINK, Color.WHITE };

	public BattlePanel(Client client) {
		this.client = client;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		drawingPanel = new CustomDrawingPanel();
		drawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		drawingPanel.setPreferredSize(new Dimension(1280, 720)); // ideally the preffered size should be the level size
		drawingPanel.setBackground(Color.black);
		add(drawingPanel, gbc);
		drawGameTimer = new Timer(1000 / fps, e -> repaint());
	}

	public int getWinnerTimeLeft() {
		return winnerTimeLeft;
	}

	public void setWinnerTimeLeft(int winnerTimeLeft) {
		this.winnerTimeLeft = winnerTimeLeft;
	}

	private class CustomDrawingPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			drawGame(g2d, client.getGameState());
		}

		public void drawGame(Graphics2D g2d, GameState gameState) {
			Stroke old = g2d.getStroke();
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(GamePlay.LEVEL_BORDER / 2, GamePlay.LEVEL_BORDER / 2,
					gameState.levelX - (GamePlay.LEVEL_BORDER), gameState.levelY - (GamePlay.LEVEL_BORDER));

			// Checks if we have a winner. If true, it shows the winners name and changes
			// view to lobby view after a small delay
			CheckForWinner(g2d, gameState);

			// For each player choose a color and then draw the player and their trail in
			// that color, cycles through the playerColors array and loops
			int c = 0;
			for (PlayerInfo p : gameState.players.values()) {
				g2d.setColor(playerColors[c]);
				drawingPanel.drawTrail(g2d, p); // Draw the trail
				if (p.alive)
					drawingPanel.drawPlayer(g2d, p); // Draw the player
				c++;
				if (c == playerColors.length)
					c = 0;
			}
		}

		public void drawPlayer(Graphics2D g2d, PlayerInfo p) {
			g2d.setStroke(new BasicStroke(1));
			Path2D.Float triangle = new Path2D.Float();
			float size = GamePlay.PLAYER_SIZE;
			float x = p.x;
			float y = p.y;
			float rotation = p.rotation;

			// Move to the first vertex
			triangle.moveTo(x + size * Math.cos(rotation), y + size * Math.sin(rotation));

			// Line to the second vertex
			triangle.lineTo(x + size * Math.cos(rotation + 2 * Math.PI / 3),
					y + size * Math.sin(rotation + 2 * Math.PI / 3));

			// Line to the third vertex and close the path
			triangle.lineTo(x + size * Math.cos(rotation - 2 * Math.PI / 3),
					y + size * Math.sin(rotation - 2 * Math.PI / 3));
			triangle.closePath();

			g2d.fill(triangle);
		}

		public void drawTrail(Graphics2D g2d, PlayerInfo p) {
			g2d.setStroke(new BasicStroke(GamePlay.TRAIL_WIDTH));
			ArrayList<ImmutablePair<Float, Float>> trail = p.trail; // Assuming this is how you get the trail

			for (int i = 0; i < trail.size() - 1; i++) {

				ImmutablePair<Float, Float> point1 = trail.get(i);
				ImmutablePair<Float, Float> point2 = trail.get(i + 1);

				// Extracting the x and y coordinates of the points
				float x1 = point1.getLeft();
				float y1 = point1.getRight();
				float x2 = point2.getLeft();
				float y2 = point2.getRight();

				// Drawing a line between the points
				// Convert float coordinates to integers
				g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

				// Alternatively, if you want to draw with float precision, you can use:
				// g2d.draw(new Line2D.Float(x1, y1, x2, y2));
			}
		}

		private void CheckForWinner(Graphics2D g2d, GameState gameState) {
			if (gameState.winner != -1) {
				//Draw the winners name
				g2d.setColor(new Color(50, 100, 180));
				g2d.setFont(new Font("Serif", Font.BOLD, 40));
				FontMetrics metric = getFontMetrics(g2d.getFont());
				g2d.drawString(winnerString, (gameState.levelX - metric.stringWidth(winnerString)) / 2,
						 (gameState.levelY - metric.getHeight()) / 2);
				// countdownLabel.setFont(new Font("Serif", Font.BOLD, 48)); // Set font size
				// and style
				// countdownLabel.setForeground(Color.RED); // Set text color
				
				if (getWinnerTimeLeft() == -10) {
					winnerString = "Winner: " + client.serverInfo.playerList.get(gameState.winner).name;
					setWinnerTimeLeft(GamePlay.WINNER_DELAY);

					winnerTimer = new Timer(1000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							setWinnerTimeLeft(getWinnerTimeLeft() - 1);
							if (getWinnerTimeLeft() <= 0) {
								winnerTimer.stop();
								client.BackToLobby();
							}
						}
					});
					winnerTimer.start();
				}

			}
		}
	}
}
