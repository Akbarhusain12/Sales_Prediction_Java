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
import java.util.HashMap;
import java.util.Map;

public class PredictionServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/predict", PredictionServer::handlePredictRequest);
        server.createContext("/options", PredictionServer::handleOptionsRequest); // Handle preflight
        server.setExecutor(null);
        server.start();
        System.out.println("🚀 Server started at http://localhost:" + PORT);
    }

    private static void handlePredictRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleOptionsRequest(exchange);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // 405: Method Not Allowed
            return;
        }


        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        Gson gson = new Gson();
        Map<String, Object> input = gson.fromJson(requestBody, new TypeToken<Map<String, Object>>() {}.getType());

        if (!input.containsKey("storeCode") || !input.containsKey("itemCode")) {
            sendErrorResponse(exchange, "Missing storeCode or itemCode in request.");
            return;
        }

        String storeCodeStr = input.get("storeCode").toString().replaceAll("[^0-9]", "");
        String itemCodeStr = input.get("itemCode").toString().replaceAll("[^0-9]", "");

        try {
            double predictedSales = predictSales(Integer.parseInt(storeCodeStr), Integer.parseInt(itemCodeStr));
            Map<String, Object> response = new HashMap<>();
            response.put("predictedSales", predictedSales);

            sendJsonResponse(exchange, gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(exchange, "Internal Server Error: " + e.getMessage());
        }
    }

    private static double predictSales(int storeCode, int itemCode) throws SQLException {
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
                String sc = rs.getString("store_code").replaceAll("[^0-9]", "");
                String ic = rs.getString("item_code").replaceAll("[^0-9]", "");

                features[index][0] = Double.parseDouble(sc);
                features[index][1] = Double.parseDouble(ic);
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

    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // CORS FIX
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // CORS FIX
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String jsonResponse = new Gson().toJson(Map.of("error", errorMessage));
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(400, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void handleOptionsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1); // No content for OPTIONS request
    }
}
