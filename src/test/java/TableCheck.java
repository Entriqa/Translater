

import java.sql.*;

public class TableCheck {

    private static final String URL = "jdbc:postgresql://localhost:5432/yourdatabase";
    private static final String USER = "yourusername";
    private static final String PASSWORD = "yourpassword";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (checkTableExists(connection, "'translationrequests'")) {
                System.out.println("Table 'translationrequests' exists.");
            } else {
                System.out.println("Table 'translationrequests' does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkTableExists(Connection connection, String tableName) throws SQLException {
        String query = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tableName);  // Уберите кавычки вокруг tableName
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        }
        return false;
    }
}