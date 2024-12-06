package com.backend.owlfinance.database.bigtable;

import com.backend.owlfinance.database.obj.StockPrice;
import com.backend.owlfinance.database.obj.Transaction;
import com.backend.owlfinance.database.obj.User;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class emulates the client interacting with the DB.
 */
public class BigTableDriver {

    //Timestamp formatter
    private static final DateTimeFormatter TSFORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {

        // Establish and maintain connection with our Bigtable instance
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "comp-539-bigtable";

        BigTableManager bt = new BigTableManager(projectId, instanceId);

        testUserMethods(bt);

        // TESTING TRANSACTIONS TABLE
        // TODO: eventually want to make these actual test cases
        System.out.println("\nAdding new transaction");
        String transactionRowKey1 = bt.createTransaction(new Transaction(
               "anthony413", "BUY", "NVDA", 1, 100.00, UUID.randomUUID().toString()
        ));

        Transaction transaction = bt.getTransaction(transactionRowKey1);
        System.out.println(transaction);

        System.out.println("\nAdding new transaction");
        String transactionRowKey2 = bt.createTransaction(new Transaction(
                "anthony413", "BUY", "NVDA", 3, 100.01, UUID.randomUUID().toString()
        ));

        Transaction transaction2 = bt.getTransaction(transactionRowKey2);
        System.out.println(transaction2);

        System.out.println("\nAdding new transaction");
        String transactionRowKey3 = bt.createTransaction(new Transaction(
                "brian123", "BUY", "NVDA", 3, 100.01, UUID.randomUUID().toString()
        ));

        Transaction transaction3 = bt.getTransaction(transactionRowKey3);
        System.out.println(transaction3);

        System.out.println("\nGetting all transactions from anthony413");
        List<Transaction> transactions = bt.getTransactionsByUser("anthony413");
        int cnt = 0;
        for (Transaction txn : transactions) {
            System.out.println(txn);
            cnt += 1;
        }
        System.out.println("\nThere were " + cnt + " transactions");

        for (Transaction txn : transactions) {
            bt.deleteTransaction(txn.username() + "#" + txn.uuid());
        }
        System.out.println("\nDeleted transactions");

        // TESTING STOCK PRICE DATA
        System.out.println("\nAdding new stock price");
        String stockPrice1 = bt.createStockPrice(new StockPrice("AAPL", "2024-11-11T15:30:00Z", 149.25, 153.50, 12000000, 150.00, 152.75));
        System.out.println("Stock price added:" + stockPrice1);

        String stockPrice2 = bt.createStockPrice(new StockPrice("AAPL", "2024-11-11T18:30:00Z", 149.25, 200.50, 12000000, 150.00, 152.75));
        System.out.println("Stock price added (duplicate for different time):" + stockPrice2);

        String stockPrice3 = bt.createStockPrice(new StockPrice("AMZN", "2024-12-11T15:30:00Z", 149.25, 200.50, 12000000, 150.00, 152.75));
        System.out.println("Stock price added:" + stockPrice3);

        String stockPrice4 = bt.createStockPrice(new StockPrice("F", "2024-12-11T15:30:00Z", 149.25, 200.50, 1000000, 150.00, 152.75));
        System.out.println("Stock price added:" + stockPrice4);

        System.out.println("\n" + "Getting stock price with row key: " + stockPrice1);
        StockPrice found = bt.getStockPrice(stockPrice1);
        System.out.println("Stock found: " + found);

        System.out.println("Getting all stock prices");
        String allPrices = bt.getAllStockPrices();

        System.out.println(allPrices);

        System.out.println("\nTesting updating stock volume:");
        StockPrice found1 = bt.getStockPrice("F#2024-12-11T15:30:00Z");
        System.out.println("Before adding 1000 shares, volume of F is " + found1.volume());
        bt.updateVolume("F#2024-12-11T15:30:00Z", 1000);
        StockPrice found2 = bt.getStockPrice("F#2024-12-11T15:30:00Z");
        System.out.println("After adding 1000 shares, volume of F is " + found2.volume());

        bt.deleteStockPrice(stockPrice1);
        bt.deleteStockPrice(stockPrice2);
        bt.deleteStockPrice(stockPrice3);
        bt.deleteStockPrice(stockPrice4);
        System.out.println("\nDeleting stock prices");

        bt.close();
    }

    /**
     * Make UTC timestamp string for the current moment.
     * @return  A UTC timestamp string.
     */
    private static String timestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(TSFORMATTER);
    }

    /**
     * Tests all of the user methods available. Made it it's own method for clarity.
     */
    public static void testUserMethods(BigTableManager bt) {
        System.out.println("\nTesting user methods:");

        // Create a test user
        System.out.println("\nCreating test user...");
        bt.createUser(new User("testuser", "password123", "initial_token", 413));

        // Get user
        System.out.println("\nGetting user info:");
        User user = bt.getUser("testuser");
        System.out.println(user);

        // Test "authentication" (again, this is not secure at all.)
        System.out.println("\nTesting authentication:");
        System.out.println("Correct password test: " + bt.authenticateUser("testuser", "password123"));
        System.out.println("Wrong password test: " + bt.authenticateUser("testuser", "wrongpass"));

        // Get token
        System.out.println("\nGetting user token:");
        String token = bt.getUserToken("testuser");
        System.out.println("Token: " + token);

        // Get and set balance
        System.out.println("\nTesting balance operations:");
        System.out.println("Initial balance: $" + bt.getUserCashBalance("testuser"));
        bt.setUserCashBalance("testuser", 2000);
        System.out.println("New balance: $" + bt.getUserCashBalance("testuser"));

        // Delete user
        System.out.println("\nDeleting user:");
        bt.deleteUser("testuser");

        // Verify deletion
        System.out.println("\nTrying to get deleted user:");
        User deletedUser = bt.getUser("testuser");
        if (deletedUser == null) {
            System.out.println("User successfully deleted");
        }
    }
}
