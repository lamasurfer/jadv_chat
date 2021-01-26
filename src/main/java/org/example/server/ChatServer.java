package org.example.server;


import org.example.common.ISettings;
import org.example.server.handlers.IUserHandler;
import org.example.server.users.ChatUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private final Logger logger;
    private final ISettings settings;
    private final IUserHandler userHandler;

    private ServerSocket serverSocket;
    private ExecutorService service;

    public ChatServer(IUserHandler userHandler, ISettings settings) {
        this.userHandler = userHandler;
        this.settings = settings;
        this.logger = LoggerFactory.getLogger("server logger");
    }

    public ChatServer(IUserHandler userHandler, ISettings settings, Logger logger) {
        this.userHandler = userHandler;
        this.settings = settings;
        this.logger = logger;
    }

    public void start() throws IOException {
        final int PORT = settings.getPort();
        final int MAX_CONNECTIONS = settings.getConnections();

        serverSocket = createServerSocket(PORT);
        logger.info("Server started");

        service = createExecutorService(MAX_CONNECTIONS);
        logger.info("Executor started");

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Connection accepted");
                service.execute(new ChatUser(clientSocket, userHandler, logger));
            }

        } catch (IOException e) {
            logger.warn("ChatServer exception in start() - " + e.getMessage());
        }
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    public ExecutorService createExecutorService(int connections) {
        return Executors.newFixedThreadPool(connections);
    }

    public void stop() {
        try {
            if (userHandler != null && service != null && serverSocket != null) {
                userHandler.stopAll();
                service.shutdown();
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.warn("ChatServer exception in stop() - " + e.getMessage());
        }
        logger.info("Server stopped");
    }
}
