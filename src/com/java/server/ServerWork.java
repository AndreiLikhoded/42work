package com.java.server;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ServerWork {

    public static Pattern name = Pattern.compile("/name");

    public static void handle(User user) {
        Socket socket = user.getSocket();
        System.out.printf("Client is on: %s%n", socket);

        try (Scanner reader = getReader(socket);
             PrintWriter writer = getWriter(socket);
             socket) {
            sendResponse("Hello " + socket, writer);
            while (true) {
                EchoServer.serverList.removeIf(u -> u.getSocket().isClosed());

                String message = reader.nextLine().strip();
                System.out.printf("Got: <%s>-%s %n", user.getName(), message);

                if (isEmptyMsg(message) || isQuitMsg(message)) {
                    break;
                }

                if (name.matcher(message).find()) {
                    String[] strings = message.split(" ");
                    if (strings.length == 2) {
                        String oldName = user.getName();
                        user.setName(strings[1]);
                        sendResponse("You are known as: " + strings[1], getWriter(user.getSocket()));
                        sendAllUsers(user, "User " + oldName + " is known as " + strings[1]);
//                        return;
                    } else {
                        sendResponse("Something went wrong", getWriter(user.getSocket()));
                    }
                } else {

                    sendAllUsers(user, message);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped the connection!");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.printf("Client is off: %s%n", socket);
        }
    }

    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }

    private static Scanner getReader(Socket socket) throws IOException {
        InputStream stream = socket.getInputStream();
        InputStreamReader input = new InputStreamReader(stream, "UTF-8");
        return new Scanner(input);
    }

    private static boolean isQuitMsg(String message) {
        return "bye".equalsIgnoreCase(message);
    }

    private static boolean isEmptyMsg(String message) {
        return message == null || message.isBlank();
    }

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private static void sendAllUsers(User user, String message) throws IOException {
        for (var r : EchoServer.serverList) {
            if (!user.getName().equals(r.getName())) {
                if(!r.getSocket().isClosed()) {
                    PrintWriter writer = getWriter(r.getSocket());
                    sendResponse(user.getName() + ":" + message, writer);
                }
            }
        }
    }

}
