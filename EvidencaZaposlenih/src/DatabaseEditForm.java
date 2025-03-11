import javax.swing.*;
import java.sql.*;

public class DatabaseEditForm extends JFrame {

    private JTextField firstNameTextField;
    private JButton addEmployeeButton;
    private JPanel editPanel;
    private JTextField lastNameTextField;
    private JTextField emailTextField;
    private JTextField phoneTextField;
    private JTextField locationTextField;
    private JTextField jobTitleTextField;

    private MainForm mainForm;

    public DatabaseEditForm(MainForm mainForm) {
        this.mainForm = mainForm;

        setTitle("Edit database");
        setContentPane(editPanel);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addEmployeeButton.addActionListener(e -> {
            addEmployee();
        });
    }

    private void addEmployee() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String phone = phoneTextField.getText();
        String location = locationTextField.getText();
        String jobTitle = jobTitleTextField.getText();

        String query = "SELECT dodaj_zaposlenega(" + firstName + ", " + lastName + ", " + email + ", " + phone + ", " + location + ", " + jobTitle + ");";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();

            mainForm.loadTableData("zaposleni");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }

    }
}
