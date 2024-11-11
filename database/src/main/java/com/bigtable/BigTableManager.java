package com.bigtable;

import com.obj.DemoUser;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BigTableManager {

    private final String bigtableDemoID = "bigtableDemo";
    private final String userTableID = "users";
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

}
