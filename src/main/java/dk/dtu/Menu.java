package dk.dtu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Menu extends JPanel {

	JButton joinButton = new JButton("JOIN GAME");
	JButton hostButton = new JButton("HOST GAME");
	private JTextField textField;
	ViewManager viewManager;
	Client client;

	public Menu(ViewManager viewManager, Client client) {
		this.viewManager = viewManager;
		this.client = client;
		initMenu();
	}

	void initMenu() {

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Add a JLabel for instruction
		JLabel instructionLabel = new JLabel("Name: " + client.getName());
		// instructionLabel.setBounds(235, 505, 120, 20);
		instructionLabel.setPreferredSize(new Dimension(150, 20));
		instructionLabel.setForeground(Color.white);

		// Add a JTextField for input
		textField = new JTextField();
		// textField.setBounds(viewManager.getWidth() / 2 - 60, 500, 120, 40);
		textField.setPreferredSize(new Dimension(150, 20));
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		textField.setBorder(border);

		// hostButton.setBounds(viewManager.getWidth() / 2 - 60, 250, 120, 50);
		hostButton.setPreferredSize(new Dimension(150, 50));
		hostButton.setForeground(Color.white);
		hostButton.setBackground(new Color(0, 76, 153));
		hostButton.setOpaque(true);
		hostButton.setBorderPainted(false);
		
		
		hostButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewManager.changeView("lobby");
				if (client.server == null) {
					client.CreateLobby(); // This should start the server and initialize the lobby
				}
				viewManager.changeView("lobby");
			}
		});

		// joinButton.setBounds(viewManager.getWidth() / 2 - 60, 330, 120, 50);
		joinButton.setPreferredSize(new Dimension(150, 50));
		joinButton.setForeground(Color.white);
		joinButton.setBackground(new Color(0, 76, 153));
		joinButton.setOpaque(true);
		joinButton.setBorderPainted(false);
		
		
		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Prompt the user to enter the host's IP address
				String inputHostAddress = JOptionPane.showInputDialog(Menu.this, "Enter Host's IP Address:", "Connect to Host", JOptionPane.QUESTION_MESSAGE);
				
				if (inputHostAddress != null && !inputHostAddress.isEmpty()) {
					// Get the actual host address from the client
					String actualHostAddress = client.getHostAddress();
		
					// Check if the input address matches the actual host address
					if (inputHostAddress.equals(actualHostAddress)) {
						// If they match, join the lobby
						try {
							client.joinLobby(inputHostAddress);  
							viewManager.changeView("lobby");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(Menu.this, "Failed to connect. Check the IP address and try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
							viewManager.changeView("menu");
						}
					} else {
						// If they do not match, show an error message
						JOptionPane.showMessageDialog(Menu.this, "Incorrect IP address. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(Menu.this, "Please input an IP address", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("No host address provided");
					viewManager.changeView("menu");
				}
			}
		});
		
		
		
		

		// Add a "Save" JButton
		JButton saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(60, 30));

		// Add an ActionListener to the "Save" button
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the text from the JTextField
				String name = textField.getText();

				client.setName(name);
				instructionLabel.setText("Name: " + client.getName());

				// Clear the JTextField, ready for the next one
				textField.setText("");
				
				repaint();
			}
		});

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.ipady = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.insets = new Insets(0, 0, 20, 0);

		add(hostButton, gbc);
		add(joinButton, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		add(instructionLabel, gbc);
		add(textField, gbc);
		gbc.insets = new Insets(0, 0, 20, 0);
		add(saveButton, gbc);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(new Color(102, 178, 255));
		g.fillRect(0, 0, viewManager.getWidth(), viewManager.getHeight());
		g.setColor(new Color(0, 76, 153));
		g.setFont(new Font("Serif", Font.BOLD, 40));
		FontMetrics metric = getFontMetrics(g.getFont());
		g.drawString("Techno Trails", (viewManager.getWidth() - metric.stringWidth("Techno Trails")) / 2,
				(int) g.getClipBounds().getHeight() / 5);
	}
}
