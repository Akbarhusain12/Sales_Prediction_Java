package PredictX;

import org.apache.commons.math4.legacy.stat.regression.OLSMultipleLinearRegression;
import java.sql.*;

public class predictor {
    public static void main(String[] args) {
        try (

                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sales_db", "username", "password");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT store_code, item_code, sales FROM Sales")
        ) {

            double[][] features = new double[100][2];
            double[] target = new double[100];
            int index = 0;

            while (rs.next() && index < 100) {

                String storeCodeStr = rs.getString("store_code").replaceAll("[^0-9]", "");
                features[index][0] = Double.parseDouble(storeCodeStr);


                String itemCodeStr = rs.getString("item_code").replaceAll("[^0-9]", "");
                features[index][1] = Double.parseDouble(itemCodeStr);

                target[index] = rs.getDouble("sales");
                index++;
            }


            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            regression.setNoIntercept(false);
            regression.newSampleData(target, features);
            double[] coefficients = regression.estimateRegressionParameters();

            System.out.println("✅ Model Trained Successfully!");


            int testStoreCode = 3;
            int testItemCode = 102;
            double predictedSales = coefficients[0] + (coefficients[1] * testStoreCode) + (coefficients[2] * testItemCode);

            System.out.println("🔮 Predicted Sales: " + predictedSales);

        } catch (Exception e) {
            System.err.println("❌ Error in Sales Prediction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
