package dk.dtu;

import javax.swing.*;
import java.awt.*;

public class TopRightPanel extends JPanel {

    public TopRightPanel() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(200, 200));

        /*this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;*/
    }
}
