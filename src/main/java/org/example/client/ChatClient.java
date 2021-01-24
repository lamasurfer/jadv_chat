package org.example.client;


import org.example.common.ISettings;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private final Logger logger;
    private final ISettings settings;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ChatClient(ISettings settings, Logger logger) {
        this.settings = settings;
        this.logger = logger;
    }

    public void start() {
        final String HOST = settings.getHost();
        final int PORT = settings.getPort();

        try {
            socket = startSocket(HOST, PORT);

            logger.info("Connected to server");

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            logger.error(this.getClass().getSimpleName() + " exception in start() - " + e.getMessage());
        }
    }

    public Socket startSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void send(String message) {
        writer.println(message);
        logger.info("Sent " + message);
    }

    public String read() {
        String message = null;
        try {
            message = reader.readLine();
            logger.info("Read " + message);
        } catch (IOException e) {
            logger.error(this.getClass().getSimpleName() + " exception in read() - " + e.getMessage());
            System.out.println("No connection");
            stop();
        }
        return message;
    }

    public void stop() {
        try {
            if (socket != null && writer != null && reader != null) {
                writer.close();
                reader.close();
                socket.close();
            }
        } catch (IOException e) {
            logger.error(this.getClass().getSimpleName() + " exception in stop() - " + e.getMessage());
        }
        logger.info("Client stopped");
    }
}