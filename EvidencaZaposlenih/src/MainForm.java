import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainForm extends JFrame {

    private JComboBox<String> tableComboBox;
    private JButton outputButton;
    private JTable dataTable;
    private JPanel mainPanel;
    private JButton settingsButton;
    private JButton editButton;
    private JButton exitButton;

    public MainForm() {
        setTitle("Database Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setContentPane(mainPanel);
        setLocationRelativeTo(null); // Center window

        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData((String) tableComboBox.getSelectedItem());
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseEditForm editDatabaseForm = new DatabaseEditForm(MainForm.this);
                editDatabaseForm.setVisible(true);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));
    }

    public void loadTableData(String tableName) {
        DefaultTableModel model = new DefaultTableModel();
        dataTable.setModel(model);

        String query = "SELECT * FROM " + tableName;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Get column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Add rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainForm().setVisible(true));
    }
}