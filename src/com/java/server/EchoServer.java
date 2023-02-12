package com.java.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    public static List<User> serverList = new ArrayList<>();

    private EchoServer(int port){
        this.port = port;
    }

    public static EchoServer bindToPort(int port){
        return new EchoServer(port);
    }

    public void run(){
        try(var server = new ServerSocket(port)){
            while(!server.isClosed()){
                Socket clientSocket = server.accept();
                User user = new User(clientSocket, makeName());
                pool.submit(() -> ServerWork.handle(user));
                serverList.add(user);
            }
        }catch (IOException e){
            System.out.printf("Connection is failed, port %s is busy", port);
            e.printStackTrace();
        }
    }
    private static String makeName(){
        return "User-" + (serverList.size() + 1);
    }
}
