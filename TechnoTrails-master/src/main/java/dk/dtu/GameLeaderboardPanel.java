package dk.dtu;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class GameLeaderboardPanel extends JPanel {

    private JTable leaderboardTable;

    public GameLeaderboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Data to be displayed in the JTable
        String[][] data = { { "Marcus", "4" }, { "Seb", "2" }, { "Martin", "1" }, { "Hannah", "0" }, { "Frederik", "0" }
        };
        // Column Names
        String[] columnNames = { "Name", "Score" };

        // Create a DefaultTableModel with data and column names
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        // Create a JTable with the DefaultTableModel
        leaderboardTable = new JTable(model);
        leaderboardTable.setFocusable(false);
        // Add the JTable to the JPanel
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Set the preferred size of the JScrollPane
        scrollPane.setPreferredSize(new Dimension(200, 200)); // Adjust the preferred size of the JScrollPane

        // Add the JScrollPane to the JPanel
        add(scrollPane, BorderLayout.CENTER);
    }
}
