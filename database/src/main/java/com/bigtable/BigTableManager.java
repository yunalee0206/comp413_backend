package com.bigtable;

import com.obj.DemoUser;
import com.obj.StockPrice;
import com.obj.Transaction;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
// Needed to filter multiple conditions for column values
import com.google.cloud.bigtable.data.v2.models.Filters;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BigTableManager {

    // Table IDs
    private final String bigtableDemoID = "bigtableDemo";
    private final String userTableID = "users";
    private final String transactionsTableID = "transactions";
    private final String stockPriceTableID = "stock-prices";

    private final BigtableDataClient client;
    private final Random RANDOM = new Random(0xC413 + Instant.now().getEpochSecond());

    public BigTableManager(String projectId, String instanceId) throws IOException {

//        BigtableTableAdminClient admin = BigtableTableAdminClient.create(projectId, instanceId);
//        admin.dropAllRows(usersTableID);
//        admin.close();

        BigtableDataSettings settings =
                BigtableDataSettings.newBuilder()
                        .setProjectId(projectId)
                        .setInstanceId(instanceId)
                        .build();

        this.client = BigtableDataClient.create(settings);
    }

    void close() {
        this.client.close();
    }

    List<DemoUser> getUsersDemo() {
        List<DemoUser> users = new ArrayList<>();
//        Stream<Row> rows = client.readRows(Query.create(usersTableID)).stream();
        Iterator<Row> rows = client.readRows(Query.create(bigtableDemoID)).stream().iterator();
        Row row;
        while (rows.hasNext()) {
            row = rows.next();
            String username =  row.getCells("User", "username").get(0).getValue().toStringUtf8();
            String color = row.getCells("User", "color").get(0).getValue().toStringUtf8();
            String registrationTimestamp =  row.getCells("User", "timestamp").get(0).getValue().toStringUtf8();
            users.add(new DemoUser(username, color, registrationTimestamp));
        }
        return users;
    }

    String getUserColorDemo(String username) {
        Row row = client.readRow(bigtableDemoID, username);
        if (row == null) return "";
        return row.getCells("User", "color").get(0).getValue().toStringUtf8();
    }

    public void createUserDemo(DemoUser user) {
        String username = user.username();
        if (client.readRow(bigtableDemoID, username) != null) {
            System.out.println("User \"" + username + "\" already exists in table");
            return;
        }

        RowMutation mutation = RowMutation.create(
                        bigtableDemoID,
                        username)
                .setCell("User", "username", username)
                .setCell("User", "color", user.color())
                .setCell("User", "timestamp", user.timestamp());
        client.mutateRow(mutation);
        System.out.println("Successfully wrote user \"" + username + "\" to DB.");
    }

    public void updateColorDemo(String username, String value) {
        Row row = client.readRow(bigtableDemoID, username);
        if (row == null) return;
        String oldColor = row.getCells("User", "color").get(0).getValue().toStringUtf8();
        if (!value.equals(oldColor)) {
            client.mutateRow(
                    RowMutation.create(bigtableDemoID, username)
                            .setCell("User", "color", String.valueOf(value))
            );
        }
    }

    public void deleteUserDemo(String username) {

        Row row = client.readRow(bigtableDemoID, username);
        if (row == null) return;
        client.mutateRow(
                RowMutation.create(bigtableDemoID, username)
                        .deleteRow());
    }

    /* Transactions Table Methods */
    public String createTransaction(Transaction transaction) {
        String username = transaction.username();
        String uuid = UUID.randomUUID().toString();
        String rowKey = username + "#" + uuid;

        RowMutation newTransaction = RowMutation.create(transactionsTableID, rowKey)
            .setCell("user_info", "username", username)
            .setCell("transaction_info", "type", transaction.transactionType())
            .setCell("transaction_info", "stock_symbol", transaction.stockSymbol())
            .setCell("transaction_info", "num_shares", transaction.numShares())
            .setCell("transaction_info", "share_price", Double.toString(transaction.sharePrice()));
        client.mutateRow(newTransaction);
        System.out.println("Successfully wrote new transaction \"" + rowKey + "\" to DB.");

        return rowKey;
    }

    public void deleteTransaction(String rowKey) {
        Row row = client.readRow(transactionsTableID, rowKey);
        if (row == null) return;
        client.mutateRow(RowMutation.create(transactionsTableID, rowKey).deleteRow());
    }

    /* Stock Prices Table Methods */
    public String createStockPrice(StockPrice stockPrice) {
        // Construct the row key from stockSymbol and dateTime
        String rowKey = stockPrice.stockSymbol() + "#" + stockPrice.dateTime();

        // Check if the stock price already exists
        Row row = client.readRow(stockPriceTableID, rowKey);

        // Note: we do not need to check if this row exists. Even if it does, this code will update the row (which is what we want)
        RowMutation newStockPrice = RowMutation.create(stockPriceTableID, rowKey)
                .setCell("external_stocks", "stock_key", stockPrice.stockSymbol() + "#" + stockPrice.dateTime())
                .setCell("external_stocks", "stock_symbol", stockPrice.stockSymbol())
                .setCell("external_stocks", "date/time", stockPrice.dateTime())
                .setCell("external_stocks", "low", Double.toString(stockPrice.low()))
                .setCell("external_stocks", "high", Double.toString(stockPrice.high()))
                .setCell("external_stocks", "volume", stockPrice.volume())
                .setCell("external_stocks", "close", Double.toString(stockPrice.close()));
        client.mutateRow(newStockPrice);
        System.out.println("Successfully wrote new stock price \"" + rowKey + "\" to DB.");
        return rowKey;
    }

    // Row key = stock symbol + "#"
    public void deleteStockPrice(String rowKey) {
        Row row = client.readRow(stockPriceTableID, rowKey);
        if (row == null) return;
        client.mutateRow(RowMutation.create(stockPriceTableID, rowKey).deleteRow());
    }


}
