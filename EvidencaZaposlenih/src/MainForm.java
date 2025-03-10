import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.*;

public class MainForm {

    public static void main(String[] args) {
        String query = "SELECT * FROM kraji";
        try(Connection connection = DatabaseConnection.getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("ime");
                    int posta = resultSet.getInt("posta");
                    System.out.println("ID: " + id + ", Ime: " + name + ", Posta: " + posta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}