package com.bigtable;

import com.obj.DemoUser;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

        System.out.println("Create 3 users");
        bt.createUserDemo(new DemoUser("anthony413", "orange", timestamp()));
        bt.createUserDemo(new DemoUser("rohith413", "blue", timestamp()));
        bt.createUserDemo(new DemoUser("alexei413", "red", timestamp()));

        System.out.println("Alexei's Color: " + bt.getUserColorDemo("alexei413"));

        System.out.println("\nChange Alexei's Color");
        bt.updateColorDemo("alexei413", "yellow");
        bt.updateColorDemo("alexei539", "yellow");
        System.out.println("Alexei's Color: " + bt.getUserColorDemo("alexei413"));

        System.out.println("alexei413 color: " + bt.getUserColorDemo("alexei413"));
        System.out.println("alexei539 color: " + bt.getUserColorDemo("alexei539"));

        bt.createUserDemo(new DemoUser("alexei539", "red", timestamp()));
        System.out.println("\nCurrent 'User' column family (table)");
        for (DemoUser u: bt.getUsersDemo()) {
            System.out.println(u);
        }

        System.out.println("\nDeleting alexei413:");
        bt.deleteUserDemo("alexei413");
        for (DemoUser u: bt.getUsersDemo()) {
            System.out.println(u);
        }

        System.out.println("\nDeleting all users now:");
        for (DemoUser u: bt.getUsersDemo()) {
            System.out.println("Deleting " + u.username());
            bt.deleteUserDemo(u.username());
        }

        System.out.println("\nAnything left in the table?");
        for (DemoUser u: bt.getUsersDemo()) {
            System.out.println(u);
        }
        System.out.println("\nNope!");

        bt.close();
    }

    /**
     * Make UTC timestamp string for the current moment.
     * @return  A UTC timestamp string.
     */
    private static String timestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(TSFORMATTER);
    }
}
