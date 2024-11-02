package com.obj;

public record DemoUser(String username, String color, String timestamp) {

    @Override
    public String toString() {
        return this.username + " " + this.color + " " + this.timestamp;
    }
}
