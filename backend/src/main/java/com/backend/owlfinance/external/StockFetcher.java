package com.backend.owlfinance.external;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

public class StockFetcher {

    private static final String Alpha_API = ""; //replace with your api key
    private static final String Alpha_URL = "https://www.alphavantage.co/query";


    public static void getMinuteByMinuteHistoricalPrices(String stockCode, String targetDate) throws Exception {
        String function = "TIME_SERIES_INTRADAY";
        String interval = "1min"; // Fetching minute-by-minute data
        String urlString = Alpha_URL + "?function=" + function + "&symbol=" + stockCode
                + "&interval=" + interval + "&outputsize=full" + "&apikey=" + Alpha_API;

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        saveJSONToFile(jsonResponse, stockCode + "_historical_data_" + targetDate + ".json");

        JSONObject timeSeries = jsonResponse.getJSONObject("Time Series (" + interval + ")");

        List<String> timeKeys = new ArrayList<>(timeSeries.keySet());
        Collections.sort(timeKeys);

        System.out.println("Minute-by-Minute Historical Prices for " + stockCode + " on " + targetDate + ":");
        boolean dataFound = false;
        for (String time : timeKeys) {
            if (time.startsWith(targetDate)) {
                dataFound = true;
                JSONObject minuteData = timeSeries.getJSONObject(time);
                System.out.println("Time: " + time + ", Close Price: " + minuteData.getDouble("4. close"));
            }
        }

        if (!dataFound) {
            System.out.println("No data found for the specified date: " + targetDate);
        }
    }

    public static void saveJSONToFile(JSONObject jsonObject, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonObject.toString(4)); // Pretty-print the JSON data with an indent of 4 spaces
            System.out.println("Successfully saved JSON data to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String stockCode = "AAPL";
            String targetDate = "2024-09-27";

            getMinuteByMinuteHistoricalPrices(stockCode, targetDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
