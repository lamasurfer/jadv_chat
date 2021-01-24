package org.example.server.impl;


import org.example.server.interfaces.IChatServer;
import org.example.common.ISettings;
import org.example.server.interfaces.IUserHandler;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements IChatServer {

    private final Logger logger;
    private final ISettings settings;
    private final IUserHandler userHandler;

    private ServerSocket serverSocket;
    private ExecutorService service;

    public ChatServer(IUserHandler userHandler, ISettings settings, Logger logger) {
        this.userHandler = userHandler;
        this.settings = settings;
        this.logger = logger;
    }

    @Override
    public void start() {
        final int PORT = settings.getPort();
        final int MAX_CONNECTIONS = settings.getConnections();

        try {
            serverSocket = startServerSocket(PORT);
            if (serverSocket == null) {
                logger.error("Error starting ServerSocket!");
                return;
            }

            logger.info("Server started");

            service = Executors.newFixedThreadPool(MAX_CONNECTIONS);
            logger.info("Executor started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Connection accepted");
                service.execute(new ChatUser(clientSocket, userHandler, logger));
            }

        } catch (IOException e) {
            logger.error(this.getClass().getSimpleName() + " exception in start() - " + e.getMessage());
        }
    }

    @Override
    public ServerSocket startServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    // останавливает сервер с этой стороны из консоли
    @Override
    public void stop() {
        userHandler.stopAll();
        try {
            service.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            logger.error(this.getClass().getSimpleName() + " exception in stop() - " + e.getMessage());
        }
        logger.info("Server stopped");
    }
}
