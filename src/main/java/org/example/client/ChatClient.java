package org.example.client;


import org.example.common.ISettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ChatClient(ISettings settings) {
        this.settings = settings;
        this.logger = LoggerFactory.getLogger("client logger");
    }

    public ChatClient(ISettings settings, Logger logger) {
        this.settings = settings;
        this.logger = logger;
    }

    public void start() throws IOException {
        final String HOST = settings.getHost();
        final int PORT = settings.getPort();

        socket = createSocket(HOST, PORT);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Connected to server");
    }

    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    public void sendMessage(String message) {
        writer.println(message);
        logger.info("Sent " + message);
    }

    public String readMessage() {
        String message = null;
        try {
            message = reader.readLine();
            logger.info("Read " + message);
        } catch (IOException e) {
            logger.warn("ChatClient exception in read() - " + e.getMessage());
            System.out.println("Disconnected");
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
            logger.warn("ChatClient exception in stop() - " + e.getMessage());
        }
        logger.info("Client stopped");
    }
}
