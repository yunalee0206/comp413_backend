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
import com.obj.User;

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

    /* User methods—derived in large part from the demo. */

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

    /* From the demo. Should be deprecated, but keeping for now...
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
     */
    public void createUser(User user) {
        String username = user.username();
        // First check if user exists
        // NOTE: this assumes the username is our row key—this might not be the case?
        Row existingUser = client.readRow(userTableID, username);
        if (existingUser != null) {
            System.out.println("User \"" + username + "\" already exists in table");
            return;
        }

        // Generate initial token for the user
        // NOTE: this assumes that the user object already created has a token.
        // can easily generate one if that's what we need, however.
        String initialToken = user.token();

        // Create the user entry - username is both row key and a column value. Hopefully that's not a bad idea.
        RowMutation mutation = RowMutation.create(userTableID, username)  // Again, username as row key
                .setCell("user_info", "username", username)
                .setCell("user_info", "password", user.password())
                .setCell("user_info", "token", initialToken)
                .setCell("cash_balance", "cash_balance", Integer.toString(user.cash_balance()));

        client.mutateRow(mutation);
        System.out.println("Successfully created user: " + username);
    }

    // Get complete user
    public User getUser(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return null;
        }

        String password = row.getCells("user_info", "password").get(0).getValue().toStringUtf8();
        String token = row.getCells("user_info", "token").get(0).getValue().toStringUtf8();
        int cashBalance = Integer.parseInt(row.getCells("cash_balance", "cash_balance").get(0).getValue().toStringUtf8());

        return new User(username, password, token, cashBalance);
    }

    // Authenticate a user with a user/pass combo.
    public boolean authenticateUser(String username, String providedPassword) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return false;
        }

        String storedPassword = row.getCells("user_info", "password").get(0).getValue().toStringUtf8();
        return storedPassword.equals(providedPassword); // To be clear, this is not secure at all.
    }

    // Get user token
    public String getUserToken(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return null;
        }
        return row.getCells("user_info", "token").get(0).getValue().toStringUtf8();
    }

    // Get cash balance
    public int getUserCashBalance(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return -1;
        }
        return Integer.parseInt(row.getCells("cash_balance", "cash_balance").get(0).getValue().toStringUtf8());
    }

    // Set cash balance
    // I don't think you should directly set the cash balance like this. But in case
    // we need to produce hacky code... I'd rather be ready.
    public void setUserCashBalance(String username, int newBalance) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return;
        }

        RowMutation mutation = RowMutation.create(userTableID, username)
                .setCell("cash_balance", "cash_balance", Integer.toString(newBalance));
        client.mutateRow(mutation);
        System.out.println("Successfully updated balance for user: " + username);
    }

    // Delete user.
    public void deleteUser(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return;
        }

        client.mutateRow(RowMutation.create(userTableID, username).deleteRow());
        System.out.println("Successfully deleted user: " + username);
    }

    /* OLD DEMO CODE—will be thrown out in refactoring soon
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
    */

    /* Transactions Table Methods */
    public String createTransaction(Transaction transaction) {
        String username = transaction.username();
        String uuid = UUID.randomUUID().toString();
        String rowKey = username + "#" + uuid;

        RowMutation newTransaction = RowMutation.create(transactionsTableID, rowKey)
            .setCell("user_info", "username", username)
            .setCell("transaction_info", "type", transaction.transactionType())
            .setCell("transaction_info", "stock_symbol", transaction.stockSymbol())
            .setCell("transaction_info", "num_shares", Integer.toString(transaction.numShares()))
            .setCell("transaction_info", "share_price", Double.toString(transaction.sharePrice()));
        client.mutateRow(newTransaction);
        System.out.println("Successfully wrote new transaction \"" + rowKey + "\" to DB.");

        return rowKey;
    }

    private Transaction createTransactionRecord(Row row) {
        String username =  row.getCells("user_info", "username").get(0).getValue().toStringUtf8();
        String transactionType = row.getCells("transaction_info", "type").get(0).getValue().toStringUtf8();
        String stockSymbol =  row.getCells("transaction_info", "stock_symbol").get(0).getValue().toStringUtf8();
        int numShares = Integer.parseInt(row.getCells("transaction_info", "num_shares").get(0).getValue().toStringUtf8());
        double sharePrice = Double.parseDouble(row.getCells("transaction_info", "share_price").get(0).getValue().toStringUtf8());

        return new Transaction(username, transactionType, stockSymbol, numShares, sharePrice);
    }

    public Transaction getTransaction(String rowKey) {
        Row row = client.readRow(transactionsTableID, rowKey);

        return createTransactionRecord(row);
    }

    public List<Transaction> getTransactionsByUser(String username) {
        String rowKeyPrefix = username + "#";
        Query query = Query.create(transactionsTableID).prefix(rowKeyPrefix);
        List<Transaction> transactions = new ArrayList<>();

        client.readRows(query).forEach(row -> {
            transactions.add(createTransactionRecord(row));
        });

        return transactions;
    }

    public void deleteTransaction(String rowKey) {
        Row row = client.readRow(transactionsTableID, rowKey);
        if (row == null) return;
        client.mutateRow(RowMutation.create(transactionsTableID, rowKey).deleteRow());
    }

    public void deleteAllTransactions() {
        Iterator<Row> rows = client.readRows(Query.create(transactionsTableID)).stream().iterator();
        Row row;
        while (rows.hasNext()) {
            row = rows.next();
            client.mutateRow(RowMutation.create(transactionsTableID, row.getKey()).deleteRow());
        }
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
