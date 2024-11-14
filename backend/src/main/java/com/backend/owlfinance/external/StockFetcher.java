package org.example;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class StockFetcher {

    private static final String Alpha_API = ""; // replace with your actual API key
    private static final String Alpha_URL = "https://www.alphavantage.co/query";

    public static void getPriceMonthly(String stockCode, String targetMonth) throws Exception {
        String function = "TIME_SERIES_INTRADAY";
        String interval = "1min"; // Fetching minute-by-minute data
        String urlString = Alpha_URL + "?function=" + function + "&symbol=" + stockCode
                + "&interval=" + interval + "&month=" + targetMonth + "&outputsize=full" + "&apikey=" + Alpha_API;

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        // Read response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        saveJSONToFile(jsonResponse, stockCode + "_" + targetMonth + ".json");
        System.out.println("Successfully saved monthly data to file: " + stockCode + "_" + targetMonth + ".json");
    }

    public static void getPriceDaily(String stockCode, String targetDate) throws Exception {
        // Extract the month from targetDate (format: YYYY-MM-DD)
        String targetMonth = targetDate.substring(0, 7); // Extracts YYYY-MM

        String function = "TIME_SERIES_INTRADAY";
        String interval = "1min"; // Fetching minute-by-minute data
        String urlString = Alpha_URL + "?function=" + function + "&symbol=" + stockCode
                + "&interval=" + interval + "&month=" + targetMonth + "&outputsize=full" + "&apikey=" + Alpha_API;

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        // Read response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        // The time series data is under the key "Time Series (1min)"
        String timeSeriesKey = "Time Series (" + interval + ")";
        if (!jsonResponse.has(timeSeriesKey)) {
            System.out.println("No data found for the given date.");
            return;
        }

        JSONObject timeSeries = jsonResponse.getJSONObject(timeSeriesKey);

        // Extract data for the target date
        JSONObject targetDateData = new JSONObject();

        for (String timestamp : timeSeries.keySet()) {
            if (timestamp.startsWith(targetDate)) {
                targetDateData.put(timestamp, timeSeries.getJSONObject(timestamp));
            }
        }

        if (targetDateData.length() == 0) {
            System.out.println("No data found for the target date: " + targetDate);
            return;
        }

        // Save the data to a file
        saveJSONToFile(targetDateData, stockCode + "_" + targetDate + ".json");
        System.out.println("Successfully saved daily data to file: " + stockCode + "_" + targetDate + ".json");
    }

    public static void saveJSONToFile(JSONObject jsonObject, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonObject.toString(4)); // Pretty-print the JSON data with an indent of 4 spaces
            System.out.println("Data saved to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String stockCode = "AAPL";

            // Fetch and save minute-by-minute data for a specific date
            String targetDate = "2023-10-31"; // Replace with your target date in YYYY-MM-DD format
            getPriceDaily(stockCode, targetDate);

            // Fetch and save minute-by-minute data for a specific month
            String targetMonth = "2023-10"; // Replace with your target month in YYYY-MM format
            getPriceMonthly(stockCode, targetMonth);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
