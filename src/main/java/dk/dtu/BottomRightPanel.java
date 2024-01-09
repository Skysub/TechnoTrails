package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class BottomRightPanel extends JTable {

    public BottomRightPanel() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(200, 200));

        /*this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1; // Placing it below the TopRightPanel
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;*/
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
