package com.java.server;

import java.net.Socket;

public class User {
    private final Socket socket;
    private String name;

    public User(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
