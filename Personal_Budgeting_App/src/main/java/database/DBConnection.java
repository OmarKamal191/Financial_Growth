package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=PersonalBudget;"
            + "user=BudgetUser;"
            + "password=123456;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";


    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                System.out.println("Connection to SQL Server successful!");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to SQL Server: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}