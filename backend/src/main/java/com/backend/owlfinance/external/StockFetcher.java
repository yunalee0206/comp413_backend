package org.example;

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


    public static void getMinuteByMinuteHistoricalPrices(String stockCode, String targetMonth) throws Exception {
        String function = "TIME_SERIES_INTRADAY";
        String interval = "1min"; // Fetching minute-by-minute data
        String urlString = Alpha_URL + "?function=" + function + "&symbol=" + stockCode
                + "&interval=" + interval + "&month=" + targetMonth + "&outputsize=full" + "&apikey=" + Alpha_API;

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



//        JSONObject timeSeries = jsonResponse.getJSONObject("Time Series (" + interval + ")");
//
//        List<String> timeKeys = new ArrayList<>(timeSeries.keySet());
//        Collections.sort(timeKeys);

        saveJSONToFile(jsonResponse, stockCode + "_historical_data_" + targetMonth + ".json");




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
            String targetDate = "2024-08";

            getMinuteByMinuteHistoricalPrices(stockCode, targetDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
