import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DatabaseEditForm extends JFrame {

    private JTextField firstNameTextField;
    private JButton addEmployeeButton;
    private JPanel editPanel;
    private JTextField lastNameTextField;
    private JTextField emailTextField;
    private JTextField phoneTextField;
    private JTextField locationTextField;
    private JTextField jobTitleTextField;
    private JTextField oddelekTextField;
    private JButton deleteEmployeeButton;
    private JButton updateJobTitleButton;
    private JButton addDepartmentButton;

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

        deleteEmployeeButton.addActionListener(e -> {
            deleteEmployee();
        });

        updateJobTitleButton.addActionListener(e -> {
            updateJobTitle();
        });

        addDepartmentButton.addActionListener(e -> {
            addDepartment();
        });
    }

    private void addEmployee() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String location = locationTextField.getText();
        String jobTitle = jobTitleTextField.getText();
        String oddelek = oddelekTextField.getText();

        int phone = Integer.parseInt(phoneTextField.getText());

        String query = "SELECT dodaj_zaposlenega(?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setInt(4, phone);
            statement.setString(5, location);
            statement.setString(6, jobTitle);
            statement.setString(7, oddelek);

            statement.executeQuery();

            mainForm.loadTableData("zaposleni");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        String email = emailTextField.getText();
        int phone = Integer.parseInt(phoneTextField.getText());

        String query = "SELECT izbrisi_zaposlenega(?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setInt(2, phone);

            statement.executeQuery();

            mainForm.loadTableData("zaposleni");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void updateJobTitle() {
        String jobTitle = jobTitleTextField.getText();
        String email = emailTextField.getText();
        int phone = Integer.parseInt(phoneTextField.getText());

        String query = "SELECT posodobi_naziv(?, ?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, jobTitle);
            statement.setString(2, email);
            statement.setInt(3, phone);

            statement.executeQuery();

            mainForm.loadTableData("zaposleni");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }

    private void addDepartment() {
        String department = oddelekTextField.getText();

        String query = "SELECT dodaj_oddelek(?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, department);

            statement.executeQuery();

            mainForm.loadTableData("oddelek");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }
}
