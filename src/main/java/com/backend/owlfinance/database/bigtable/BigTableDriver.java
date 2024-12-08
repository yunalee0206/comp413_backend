package com.backend.owlfinance.database.bigtable;

import com.backend.owlfinance.database.obj.Portfolio;
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

    public static void main(String[] args) throws Exception {

        // Establish and maintain connection with our Bigtable instance
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "comp-539-bigtable";

        BigTableManager bt = new BigTableManager(projectId, instanceId);

        testUserMethods(bt);
        testPortfolioMethods(bt);

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
//        String allPrices = bt.getAllStockPrices();
//
//        System.out.println(allPrices);

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

    public static void testPortfolioMethods(BigTableManager bt) {
        System.out.println("\nTesting portfolio methods");

        System.out.println("\nCreate portfolio row");
        String rowKey = bt.createPortfolioRow(new Portfolio("username", "NVDA", 5, 100.00, timestamp()));

        System.out.println("\nGet portfolio row");
        Portfolio portfolioObject = bt.getPortfolioRow(rowKey);
        System.out.println(portfolioObject);

        System.out.println("\nAdding more rows");
        bt.createPortfolioRow(new Portfolio("username", "NVDA", 10, 150.00, timestamp()));
        bt.createPortfolioRow(new Portfolio("username", "AAPL", 10, 114.03, timestamp()));

        System.out.println("\nGet all rows for a user");
        List<Portfolio> userPortfolio = bt.getPortfolioRowsByUser("username");
        for (Portfolio portfolio : userPortfolio) {
            System.out.println(portfolio);
        }

        System.out.println("\nGet all rows for a user filtered by a specific stock");
        List<Portfolio> userPortfolioNVDA = bt.getPortfolioRowsByUserAndStock("username", "NVDA");
        for (Portfolio portfolio : userPortfolioNVDA) {
            System.out.println(portfolio);
        }

        System.out.println("\nTesting portfolio cash balance methods");
        System.out.println("\nCreate cash balance row");
        bt.updateUserCashBalance("username", 50.0);

        System.out.println("\nGet cash balance row");
        Double cashBalance = bt.getUserCashBalance("username");
        System.out.println(cashBalance);

        System.out.println("\nCreate cash balance row");
        bt.updateUserCashBalance("username", 25.2);

        System.out.println("\nGet updated cash balance row");
        Double updatedCashBalance = bt.getUserCashBalance("username");
        System.out.println(updatedCashBalance);

        bt.deleteAllPortfolioRows();
    }

    /**
     * Tests all of the user methods available. Made it it's own method for clarity.
     */
    public static void testUserMethods(BigTableManager bt) {
        System.out.println("\n=== Starting User Tests ===");

        // First check if there are any existing users
        System.out.println("\nChecking existing users before test:");
        List<User> initialUsers = bt.getAllUsers();
        System.out.println("Found " + initialUsers.size() + " users in database");

        // Create test user with ID
        System.out.println("\nCreating test user...");
        User testUser = new User("testuser", "password123", "initial_token");
        bt.createUser(testUser);

        // Verify user was created correctly by direct lookup
        System.out.println("\nVerifying user creation by username lookup:");
        User retrievedUser = bt.getUser("testuser");
        if (retrievedUser != null) {
            System.out.println("Retrieved user data:");
            System.out.println("Username: " + retrievedUser.username());
            System.out.println("Password: " + retrievedUser.password());
            System.out.println("Token: " + retrievedUser.token());

            // Verify all fields match
            boolean fieldsMatch =
                    testUser.username().equals(retrievedUser.username()) &&
                            testUser.password().equals(retrievedUser.password()) &&
                            testUser.token().equals(retrievedUser.token());

            System.out.println("All fields match original data: " + fieldsMatch);
        } else {
            System.out.println("ERROR: Failed to retrieve created user!");
        }


        // Test authentication that does not work and that you should not use.
        System.out.println("\nTesting authentication:");
        System.out.println("Correct password test: " + bt.authenticateUser("testuser", "password123"));
        System.out.println("Wrong password test: " + bt.authenticateUser("testuser", "wrongpass"));

        // Get all users again to verify the user appears in the list
        System.out.println("\nVerifying user appears in full user list:");
        List<User> allUsers = bt.getAllUsers();
        System.out.println("Total users in database: " + allUsers.size());
        boolean userFound = false;
        for (User user : allUsers) {
            if (user.username().equals("testuser")) {
                userFound = true;
                System.out.println("Found test user in full list: " + user);
                break;
            }
        }
        System.out.println("Test user found in full list: " + userFound);

        // Clean up
        System.out.println("\nCleaning up - deleting test user:");
        bt.deleteUser("testuser");

        // Verify deletion
        System.out.println("\nVerifying deletion:");
        User deletedUser = bt.getUser("testuser");
        if (deletedUser == null) {
            System.out.println("User successfully deleted");
        } else {
            System.out.println("ERROR: User still exists after deletion!");
        }

        // Final user count
        List<User> finalUsers = bt.getAllUsers();
        System.out.println("\nFinal user count: " + finalUsers.size());

        System.out.println("\n=== User Tests Complete ===");
    }
}
