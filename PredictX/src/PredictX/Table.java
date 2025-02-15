package PredictX;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Table {
    public static void main(String[] args) {
        final String url = "jdbc:mysql://localhost:3306/sales_db"; // Local MySQL
        final String username = "root";
        final String password = "123akbar#";
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connection to MySQL successful!");

            // Query to fetch data from the Sales table
            String query = "SELECT * FROM Sales LIMIT 3";
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                String date = rs.getString("order_date");
                String storeCode = rs.getString("store_code");
                String itemCode = rs.getString("item_code");
                String sales = rs.getString("sales");
                String district = rs.getString("district");
                String state = rs.getString("state");

                System.out.println("\nDate: " + date + "\nStore Code: " + storeCode +
                        "\nItem Code: " + itemCode + "\nSales: " + sales +
                        "\nDistrict: " + district + "\nState: " + state);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ An error occurred while fetching table data.");
        }
    }
}
