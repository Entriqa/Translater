package ru.kors.Translator.repository;

import java.sql.*;


public class TranslationRequestRepository {

    private static final String URL = "jdbc:postgresql://localhost:5432/yourdatabase";
    private static final String USER = "yourusername";
    private static final String PASSWORD = "yourpassword";


    private static final String CREATE_TABLE_SQL = "CREATE TABLE public.translationrequests (" +
            "id SERIAL PRIMARY KEY, " +
            "ip_address VARCHAR(255) NOT NULL, " +
            "input_text TEXT NOT NULL, " +
            "translated_text TEXT NOT NULL" +
            ");";


    private void checkAndCreateTable() {
        String checkTableSQL = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'translationrequests');";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement checkStatement = connection.prepareStatement(checkTableSQL)) {

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && !resultSet.getBoolean(1)) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(CREATE_TABLE_SQL);
                        System.out.println("Table 'translationrequests' created successfully.");
                    }
                } else {
                    System.out.println("Table 'translationrequests' already exists.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveTranslationRequest(String ipAddress, String inputText, String translatedText) {
        String insertSql = "INSERT INTO translationrequests (ip_address, input_text, translated_text) VALUES (?, ?, ?)";
        String selectSql = "SELECT * FROM translationrequests";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {

            insertStatement.setString(1, ipAddress);
            insertStatement.setString(2, inputText);
            insertStatement.setString(3, translatedText);
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Request saved successfully.");
            } else {
                System.out.println("Failed to save the request.");
            }


            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String ip = resultSet.getString("ip_address");
                    String input = resultSet.getString("input_text");
                    String translated = resultSet.getString("translated_text");
                    System.out.printf("id: %d, ip_address: %s, input_text: %s, translated_text: %s%n", id, ip, input, translated);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}