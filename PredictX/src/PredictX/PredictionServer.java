package PredictX;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.math4.legacy.stat.regression.OLSMultipleLinearRegression;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class PredictionServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/upload", PredictionServer::handleFileUpload);  // File upload-based prediction
        server.createContext("/predict", PredictionServer::handlePredictRequest);  // Manual input-based prediction
        server.createContext("/options", PredictionServer::handleOptionsRequest);  // Preflight for CORS
        server.setExecutor(null);
        server.start();
        System.out.println("🚀 Server started at http://localhost:" + PORT);
    }

    // 📂 File Upload Prediction
    private static void handleFileUpload(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleOptionsRequest(exchange);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Read uploaded file
        InputStream inputStream = exchange.getRequestBody();
        File tempFile = File.createTempFile("uploaded_", ".csv");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            inputStream.transferTo(fos);
        }

        // Process CSV file
        List<double[]> features = new ArrayList<>();
        List<Double> sales = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            String line;
            boolean firstLine = true; // Skip header
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length < 3) continue;

                try {
                    double storeCode = Double.parseDouble(values[0].trim());
                    double itemCode = Double.parseDouble(values[1].trim());
                    double sale = Double.parseDouble(values[2].trim());

                    features.add(new double[]{storeCode, itemCode});
                    sales.add(sale);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid row: " + Arrays.toString(values));
                }
            }
        }

        // Convert lists to arrays
        double[][] featureArray = features.toArray(new double[0][0]);
        double[] salesArray = sales.stream().mapToDouble(Double::doubleValue).toArray();

        // Train model and predict
        double prediction = trainAndPredict(featureArray, salesArray);

        // Send response
        Map<String, Object> response = new HashMap<>();
        response.put("predictedSales", prediction);
        response.put("message", "📊 Estimated sales from uploaded data: " + prediction + " units.");
        sendJsonResponse(exchange, new Gson().toJson(response));

        tempFile.delete();
    }

    // 📌 Manual Prediction (storeCode & itemCode)
    private static void handlePredictRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleOptionsRequest(exchange);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Map<String, Object> input = gson.fromJson(requestBody, new TypeToken<Map<String, Object>>() {}.getType());

        if (!input.containsKey("storeCode") || !input.containsKey("itemCode")) {
            sendErrorResponse(exchange, "Missing storeCode or itemCode in request.");
            return;
        }

        int storeCode = Integer.parseInt(input.get("storeCode").toString().replaceAll("[^0-9]", ""));
        int itemCode = Integer.parseInt(input.get("itemCode").toString().replaceAll("[^0-9]", ""));

        try {
            double predictedSales = predictSalesFromDB(storeCode, itemCode);
            Map<String, Object> response = new HashMap<>();
            response.put("predictedSales", predictedSales);
            response.put("message", "📈 Estimated sales for Store " + storeCode + " and Item " + itemCode + " is " + predictedSales + " units.");
            sendJsonResponse(exchange, gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(exchange, "Internal Server Error: " + e.getMessage());
        }
    }

    // 🔢 Predict using database
    private static double predictSalesFromDB(int storeCode, int itemCode) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/sales_db";
        String username = "username";
        String password = "password"; 

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT store_code, item_code, sales FROM Sales")) {

            double[][] features = new double[100][2];
            double[] target = new double[100];
            int index = 0;

            while (rs.next() && index < 100) {
                // Convert store_code and item_code to numbers
                String storeCodeStr = rs.getString("store_code").replaceAll("[^0-9]", "");
                String itemCodeStr = rs.getString("item_code").replaceAll("[^0-9]", "");

                features[index][0] = Double.parseDouble(storeCodeStr);
                features[index][1] = Double.parseDouble(itemCodeStr);
                target[index] = rs.getDouble("sales");
                index++;
            }

            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            regression.setNoIntercept(false);
            regression.newSampleData(target, features);
            double[] coefficients = regression.estimateRegressionParameters();

            return coefficients[0] + (coefficients[1] * storeCode) + (coefficients[2] * itemCode);
        }
    }


    // 📊 Train model for file-based prediction
    private static double trainAndPredict(double[][] features, double[] sales) {
        if (features.length == 0) return -1;

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(false);
        regression.newSampleData(sales, features);
        double[] coefficients = regression.estimateRegressionParameters();

        double avgStore = Arrays.stream(features).mapToDouble(row -> row[0]).average().orElse(0);
        double avgItem = Arrays.stream(features).mapToDouble(row -> row[1]).average().orElse(0);

        return coefficients[0] + (coefficients[1] * avgStore) + (coefficients[2] * avgItem);
    }

    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        sendJsonResponse(exchange, new Gson().toJson(Map.of("error", errorMessage)));
    }

    private static void handleOptionsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }
}
