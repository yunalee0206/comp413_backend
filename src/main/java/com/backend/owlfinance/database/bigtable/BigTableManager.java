package com.backend.owlfinance.database.bigtable;

import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.backend.owlfinance.database.obj.DemoUser;
import com.backend.owlfinance.database.obj.StockPrice;
import com.backend.owlfinance.database.obj.Transaction;
import com.backend.owlfinance.database.obj.User;
import com.backend.owlfinance.database.obj.Portfolio;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// Imports for cell versioning
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.GCRules;
import com.google.cloud.bigtable.admin.v2.models.ModifyColumnFamiliesRequest;


public class BigTableManager {

    // Table IDs
    private final String bigtableDemoID = "bigtableDemo";
    private static final String userTableID = "users";
    private final String transactionsTableID = "transactions";
    private static final String stockPriceTableID = "stock-prices";
    private final String portfolioTableID = "portfolios";

    private static BigtableDataClient client = null;
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
    public static boolean createUser(User user) {  // Changed return type to boolean
        String username = user.username();
        Row existingUser = client.readRow(userTableID, username);
        if (existingUser != null) {
            System.out.println("User \"" + username + "\" already exists in table");
            return false;  // User already exists
        }

        String initialToken = user.token();

        RowMutation mutation = RowMutation.create(userTableID, username)
                .setCell("user_info", "username", username)
                .setCell("user_info", "password", user.password())
                .setCell("user_info", "token", initialToken);

        client.mutateRow(mutation);
        System.out.println("Successfully created user: " + username);
        return true;  // User created successfully
    }

    // Get complete user
    public static User getUser(String username) {  // Remove static
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return null;
        }

        String password = row.getCells("user_info", "password").get(0).getValue().toStringUtf8();
        String token = row.getCells("user_info", "token").get(0).getValue().toStringUtf8();

        return new User(username, password, token);
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
    public static String getUserToken(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return null;
        }
        return row.getCells("user_info", "token").get(0).getValue().toStringUtf8();
    }

    // Delete user.
    public static void deleteUser(String username) {
        Row row = client.readRow(userTableID, username);
        if (row == null) {
            System.out.println("User \"" + username + "\" not found");
            return;
        }

        client.mutateRow(RowMutation.create(userTableID, username).deleteRow());
        System.out.println("Successfully deleted user: " + username);
    }

    // A getallusers method, for debugging purposes.
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Query query = Query.create(userTableID);

        for (Row row : client.readRows(query)) {
            try {
                // Check if all required cells exist first
                if (!row.getCells("user_info", "password").isEmpty() &&
                        !row.getCells("user_info", "token").isEmpty()) {

                    String username = row.getKey().toStringUtf8();
                    String password = row.getCells("user_info", "password").get(0).getValue().toStringUtf8();
                    String token = row.getCells("user_info", "token").get(0).getValue().toStringUtf8();

                    users.add(new User(username, password, token));
                }
            } catch (Exception e) {
                System.out.println("Error processing user row: " + e.getMessage());
                // Continue processing other rows even if one fails
                continue;
            }
        }

        return users;
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
        String rowKey = username + "#" + transaction.uuid();

        RowMutation newTransaction = RowMutation.create(transactionsTableID, rowKey)
            .setCell("user_info", "username", username)
            .setCell("transaction_info", "type", transaction.transactionType())
            .setCell("transaction_info", "stock_symbol", transaction.stockSymbol())
            .setCell("transaction_info", "num_shares", Integer.toString(transaction.numShares()))
            .setCell("transaction_info", "share_price", Double.toString(transaction.sharePrice()))
            .setCell("uuid", "uuid", transaction.uuid());
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
        String uuid = row.getCells("uuid", "uuid").get(0).getValue().toStringUtf8();

        return new Transaction(username, transactionType, stockSymbol, numShares, sharePrice, uuid);
    }

    public Transaction getTransaction(String rowKey) {
        Row row = client.readRow(transactionsTableID, rowKey);

        if (row == null) {
            System.out.println("Transaction at: \"" + rowKey + "\" not found");
            return null;
        }

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

        // Note: we do not need to check if this row exists. Even if it does, this code will update the row (which is what we want)
        RowMutation newStockPrice = RowMutation.create(stockPriceTableID, rowKey)
                .setCell("external_stocks", "stock_key", stockPrice.stockSymbol() + "#" + stockPrice.dateTime())
                .setCell("external_stocks", "stock_symbol", stockPrice.stockSymbol())
                .setCell("external_stocks", "date/time", stockPrice.dateTime())
                .setCell("external_stocks", "low", Double.toString(stockPrice.low()))
                .setCell("external_stocks", "high", Double.toString(stockPrice.high()))
                .setCell("external_stocks", "volume", Integer.toString(stockPrice.volume()))
                .setCell("external_stocks", "open", Double.toString(stockPrice.open()))
                .setCell("external_stocks", "close", Double.toString(stockPrice.close()));
        client.mutateRow(newStockPrice);
        System.out.println("Successfully wrote new stock price \"" + rowKey + "\" to DB.");
        return rowKey;
    }

    public static StockPrice getStockPrice(String rowKey) {
        Row row = client.readRow(stockPriceTableID, rowKey);
        if (row == null) {
            System.out.println("StockPrice at: \"" + rowKey + "\" not found");
            return null;
        }

        // Fix later: change UTF8 to toString
        //String found_key = row.getCells("external_stocks", "stock_key").get(0).getValue().toStringUtf8();
        String stockSymbol = row.getCells("external_stocks", "stock_symbol").get(0).getValue().toStringUtf8();
        String dateTime = row.getCells("external_stocks", "date/time").get(0).getValue().toStringUtf8();
        String lowTemp = row.getCells("external_stocks", "low").get(0).getValue().toStringUtf8();
        double low = Double.parseDouble(lowTemp);
        String highTemp = row.getCells("external_stocks", "high").get(0).getValue().toStringUtf8();
        double high = Double.parseDouble(highTemp);
        String volumeTemp = row.getCells("external_stocks", "volume").get(0).getValue().toStringUtf8();
        int volume = Integer.parseInt(volumeTemp);
        String openTemp = row.getCells("external_stocks", "open").get(0).getValue().toStringUtf8();
        double open = Double.parseDouble(openTemp);
        String closeTemp = row.getCells("external_stocks", "close").get(0).getValue().toStringUtf8();
        double close = Double.parseDouble(closeTemp);

        return new StockPrice(stockSymbol,dateTime, low, high, volume, open, close);
    }

    // Row key = stock symbol + "#"
    public void deleteStockPrice(String rowKey) {
        Row row = client.readRow(stockPriceTableID, rowKey);
        if (row == null) return;
        client.mutateRow(RowMutation.create(stockPriceTableID, rowKey).deleteRow());
    }

    public static String getAllStockPrices() {
        ArrayList<StockPrice> allPrices = new ArrayList<StockPrice>();
        Query query = Query.create(stockPriceTableID);
        for (Row row : client.readRows(query)) {
            String stockSymbol = row.getCells("external_stocks", "stock_symbol").get(0).getValue().toStringUtf8();
            String dateTime = row.getCells("external_stocks", "date/time").get(0).getValue().toStringUtf8();
            String lowTemp = row.getCells("external_stocks", "low").get(0).getValue().toStringUtf8();
            double low = Double.parseDouble(lowTemp);
            String highTemp = row.getCells("external_stocks", "high").get(0).getValue().toStringUtf8();
            double high = Double.parseDouble(highTemp);
            String volumeTemp = row.getCells("external_stocks", "volume").get(0).getValue().toStringUtf8();
            int volume = Integer.parseInt(volumeTemp);
            String openTemp = row.getCells("external_stocks", "open").get(0).getValue().toStringUtf8();
            double open = Double.parseDouble(openTemp);
            String closeTemp = row.getCells("external_stocks", "close").get(0).getValue().toStringUtf8();
            double close = Double.parseDouble(closeTemp);
            StockPrice res = new StockPrice(stockSymbol,dateTime, low, high, volume, open, close);
            allPrices.add(res);
        }

        Gson gson = new Gson();
        return gson.toJson(allPrices);
    }

    /* Portfolios */
    private Portfolio createPortfolioRecord(Row row) {
        String username =  row.getCells("user", "username").get(0).getValue().toStringUtf8();
        String stockSymbol = row.getCells("portfolio", "stock_symbol").get(0).getValue().toStringUtf8();
        int numShares = Integer.parseInt(row.getCells("portfolio", "num_shares").get(0).getValue().toStringUtf8());
        double sharePrice = Double.parseDouble(row.getCells("portfolio", "share_price").get(0).getValue().toStringUtf8());
        String dateTime = row.getCells("portfolio", "timestamp").get(0).getValue().toStringUtf8();

        return new Portfolio(username, stockSymbol, numShares, sharePrice, dateTime);
    }

    public Portfolio getPortfolioRow(String rowKey) {
        Row row = client.readRow(portfolioTableID, rowKey);

        if (row == null) {
            System.out.println("Portfolio at: \"" + rowKey + "\" not found");
            return null;
        }

        return createPortfolioRecord(row);
    }

    public List<Portfolio> getPortfolioRowsByUser(String username) {
        String rowKeyPrefix = username + "#";
        Query query = Query.create(portfolioTableID).prefix(rowKeyPrefix);
        List<Portfolio> portfolioRows = new ArrayList<>();

        client.readRows(query).forEach(row -> {
            portfolioRows.add(createPortfolioRecord(row));
        });

        return portfolioRows;
    }

    public List<Portfolio> getPortfolioRowsByUserAndStock(String username, String stockSymbol) {
        String rowKeyPrefix = username + "#" + stockSymbol + "#";
        Query query = Query.create(portfolioTableID).prefix(rowKeyPrefix);
        List<Portfolio> portfolioRows = new ArrayList<>();

        client.readRows(query).forEach(row -> {
            portfolioRows.add(createPortfolioRecord(row));
        });

        return portfolioRows;
    }

    public void sellShares(String username, String stockSymbol, int numShares) throws Exception {
        List<Portfolio> portfolioRows = getPortfolioRowsByUserAndStock(username, stockSymbol);

        int totalNumShares = 0;
        for (Portfolio row : portfolioRows) {
            totalNumShares += row.numShares();
        }
        if (totalNumShares < numShares) {
            throw new Exception("selling more shares than the user owns");
        }

        for (Portfolio row : portfolioRows) {
            int numSharesToSell = Math.min(numShares, row.numShares());

            String timestamp = row.dateTime();
            String rowKey = username + "#" + stockSymbol + "#" + timestamp;
            RowMutation mutation = RowMutation.create(portfolioTableID, rowKey)
                    .setCell("portfolio", "num_shares", Integer.toString(row.numShares() - numSharesToSell));
            client.mutateRow(mutation);

            numShares -= numSharesToSell;
            if (numShares == 0) {
                break;
            }
        }
    }

    public double getUserCashBalance(String username) {
        Row row = client.readRow(portfolioTableID, username);
        if (row == null) {
            System.out.println("Cash balance for user \"" + username + "\" not found");
            return -1;
        }
        return Double.parseDouble(row.getCells("user", "cash_balance").get(0).getValue().toStringUtf8());
    }

    public void createUserCashBalance(String username, double balance) {
        RowMutation mutation = RowMutation.create(portfolioTableID, username)
                .setCell("user", "username", username)
                .setCell("user", "cash_balance", Double.toString(balance));
        client.mutateRow(mutation);
        System.out.println("Successfully created cash balance for user: " + username);
    }

    public void updateUserCashBalance(String username, double balanceDelta) {
        double currBalance = getUserCashBalance(username);
        double newBalance = currBalance + balanceDelta;
        String transactionId = Long.toString(System.currentTimeMillis()); // Use current timestamp as ID

        if (currBalance == -1) {
            createUserCashBalance(username, balanceDelta);
            return;
        }

        RowMutation mutation = RowMutation.create(portfolioTableID, username)
                .setCell("user", "username", username)
                .setCell("user", "cash_balance", Double.toString(newBalance))
                .setCell("user", "last_transaction", transactionId);

        client.mutateRow(mutation);
        System.out.println("Successfully updated cash balance for user: " + username + " to " + newBalance);
    }

    public void deletePortfolioRow(String rowKey) {
        Row row = client.readRow(portfolioTableID, rowKey);
        if (row == null) return;
        client.mutateRow(RowMutation.create(portfolioTableID, rowKey).deleteRow());
    }

    public void deleteAllPortfolioRows() {
        Iterator<Row> rows = client.readRows(Query.create(portfolioTableID)).stream().iterator();
        Row row;
        while (rows.hasNext()) {
            row = rows.next();
            client.mutateRow(RowMutation.create(portfolioTableID, row.getKey()).deleteRow());
        }
    }

    public String createPortfolioRow(Portfolio portfolio) {
        String username = portfolio.username();
        String stockSymbol = portfolio.stockSymbol();
        String timestamp = portfolio.dateTime();
        String rowKey = username + "#" + stockSymbol + "#" + timestamp;

        RowMutation newPortfolioMutation = RowMutation.create(portfolioTableID, rowKey)
                .setCell("user", "username", username)
                .setCell("portfolio", "stock_symbol", portfolio.stockSymbol())
                .setCell("portfolio", "num_shares", Integer.toString(portfolio.numShares()))
                .setCell("portfolio", "share_price", Double.toString(portfolio.sharePrice()))
                .setCell("portfolio", "timestamp", portfolio.dateTime());
        client.mutateRow(newPortfolioMutation);
        System.out.println("Successfully wrote new portfolio \"" + rowKey + "\" to DB.");

        return rowKey;
    }

    public void updateVolume(String rowKey, int numShares) {
        Row row = client.readRow(stockPriceTableID, rowKey);
        if (row == null) {
            System.out.println("StockPrice row \"" + rowKey + "\" not found while updating volume in a transaction.");
            return;
        }

        String volumeTemp = row.getCells("external_stocks", "volume").get(0).getValue().toStringUtf8();
        int newVolume = Integer.parseInt(volumeTemp) + numShares;


        RowMutation volumeMutation = RowMutation.create(stockPriceTableID, rowKey)
                .setCell("external_stocks", "volume", Integer.toString(newVolume));
        client.mutateRow(volumeMutation);
        System.out.println("Successfully updated volume in row \"" + rowKey + "\" after transaction.");
    }

    // Cell versioning tweaks live down here!

    public void setupCashBalanceVersioning(String projectId, String instanceId) throws IOException {
        BigtableTableAdminSettings adminSettings =
                BigtableTableAdminSettings.newBuilder()
                        .setProjectId(projectId)
                        .setInstanceId(instanceId)
                        .build();

        try (BigtableTableAdminClient adminClient = BigtableTableAdminClient.create(adminSettings)) {
            // Set the number of versions to keep
            GCRules.GCRule gcRule = GCRules.GCRULES.maxVersions(1000000);

            // Modify the existing column family to enable versioning
            ModifyColumnFamiliesRequest modifyRequest = ModifyColumnFamiliesRequest.of(portfolioTableID)
                    .updateFamily("user", gcRule);

            adminClient.modifyFamilies(modifyRequest);
            System.out.println("Updated column family to keep version history");
        }
    }

//    public List<CashBalanceEntry> getCashBalanceHistory(String username) {
//        List<CashBalanceEntry> history = new ArrayList<>();
//
//        // Create a filter
//        Filters.Filter filter = Filters.FILTERS.chain()
//                .filter(Filters.FILTERS.family().exactMatch("user"))
//                .filter(Filters.FILTERS.qualifier().exactMatch("cash_balance"))
//                .filter(Filters.FILTERS.limit().cellsPerColumn(100));
//
//        // Query with filter
//        for (Row row : client.readRows(Query.create(portfolioTableID)
//                .prefix(username)
//                .filter(filter))) {
//
//            row.getCells("user", "cash_balance").forEach(cell -> {
//                double balance = Double.parseDouble(cell.getValue().toStringUtf8());
//                long timestamp = cell.getTimestamp();
//                history.add(new CashBalanceEntry(balance, timestamp));
//            });
//        }
//
//        return history;
//    }
//
//    public static class CashBalanceEntry {
//        private final double balance;
//        private final long timestamp;
//
//        public CashBalanceEntry(double balance, long timestamp) {
//            this.balance = balance;
//            this.timestamp = timestamp;
//        }
//
//        public double getBalance() { return balance; }
//        public long getTimestamp() { return timestamp; }
//
//        @Override
//        public String toString() {
//            return String.format("Balance: $%.2f at %d", balance, timestamp);
//        }
//    }

    public void displayCashBalanceHistory(String username) {
        List<BalanceEntry> history = getBalanceHistory(username);
        System.out.println("Cash Balance History for " + username + ":");
        for (BalanceEntry entry : history) {
            System.out.printf("Balance: $%.2f at %s%n",
                    entry.getBalance(),
                    entry.getTimestamp());
        }
    }

    public List<BalanceEntry> getBalanceHistory(String username) {
        List<BalanceEntry> history = new ArrayList<>();

        Filters.Filter filter = Filters.FILTERS.chain()
                .filter(Filters.FILTERS.family().exactMatch("user"))
                .filter(Filters.FILTERS.qualifier().exactMatch("cash_balance"))
                .filter(Filters.FILTERS.limit().cellsPerColumn(100));

        for (Row row : client.readRows(Query.create(portfolioTableID)
                .prefix(username)
                .filter(filter))) {

            row.getCells("user", "cash_balance").forEach(cell -> {
                double balance = Double.parseDouble(cell.getValue().toStringUtf8());
                String timestamp = Instant.ofEpochMilli(cell.getTimestamp() / 1000)
                        .toString(); // Note to backend: ISO-8601 format
                history.add(new BalanceEntry(balance, timestamp));
            });
        }

        // Sort by timestamp, string descending. I could change this, I guess
        history.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return history;
    }

    public static class BalanceEntry {
        private final double balance;
        private final String timestamp;

        public BalanceEntry(double balance, String timestamp) {
            this.balance = balance;
            this.timestamp = timestamp;
        }

        public double getBalance() { return balance; }
        public String getTimestamp() { return timestamp; }
    }
}
