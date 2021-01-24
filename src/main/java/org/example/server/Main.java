package org.example.server;

import org.example.server.impl.ChatServer;
import org.example.common.Settings;
import org.example.server.impl.UserHandler;
import org.example.server.interfaces.IChatServer;
import org.example.common.ISettings;
import org.example.server.interfaces.IUserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        final ISettings settings = new Settings("src/main/resources/settings.txt");
        final Logger logger = LoggerFactory.getLogger("Server logger");

        final IUserHandler userHandler = new UserHandler(logger);
        final IChatServer chatServer = new ChatServer(userHandler, settings, logger);

        final Scanner scanner = new Scanner(System.in);
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        while (true) {

            System.out.println("Type 'start' and press enter to start server or '0' to exit");
            String command = scanner.nextLine();

            if ("start".equals(command)) {
                executor.execute(chatServer::start);
                executor.shutdown();
                break;
            }

            if ("0".equals(command))  {
                return;
            }
        }

        while (true) {

            System.out.println("Type 'end' and press enter to stop server");
            String command = scanner.nextLine();

            if ("end".equals(command)) {
                chatServer.stop();
                break;
            }
        }
    }
}
