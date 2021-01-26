package org.example.server;

import org.example.common.ISettings;
import org.example.common.Settings;
import org.example.server.handlers.UserHandler;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        final ISettings settings = new Settings("src/main/resources/settings.txt");
        final ChatServer chatServer = new ChatServer(new UserHandler(), settings);

        final Scanner scanner = new Scanner(System.in);

        Thread serverThread = new Thread(() -> {
            try {
                chatServer.start();
            } catch (Exception e) {
                System.out.println("Problems starting server!");
            }
        });

        serverThread.start();

        while (true) {

            System.out.println("Type 'end' and press enter to stop server");
            String command = scanner.nextLine();

            if ("end".equals(command)) {
                chatServer.stop();
                serverThread.interrupt();
                break;
            }
        }
    }
}
