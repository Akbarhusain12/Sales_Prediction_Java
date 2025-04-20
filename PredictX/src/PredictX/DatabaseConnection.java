package PredictX;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        final String url = "jdbc:mysql://localhost:3306/sales_db"; // Local DB
        final String username = "root";
        final String password = "123akbar#";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connection to MySQL successful!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Connection failed! Check credentials.");
        }
    }
}