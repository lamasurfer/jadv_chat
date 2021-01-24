package org.example.server.users;

import org.example.server.handlers.IUserHandler;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

import static org.example.common.Codes.EXIT_CODE;

public class ChatUser implements IChatUser {

    private final String id = UUID.randomUUID().toString();

    private final Socket socket;
    private final IUserHandler userHandler;
    private final Logger logger;

    private String name = null;
    private PrintWriter writer;
    private BufferedReader reader;

    public ChatUser(Socket socket, IUserHandler userHandler, Logger logger) {
        this.socket = socket;
        this.userHandler = userHandler;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
            String message;
            while ((message = readMessage()) != null) {
                userHandler.process(this, message);
            }
        } catch (IOException e) {
            logger.error(this.name + " exception in run() - " + e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) {
            writer.println(message);
            logger.info("Sent " + message);
    }

    @Override
    public String readMessage() {
        String message = null;
        try {
            message = reader.readLine();
            logger.info("Read " + message);
        } catch (IOException e) {
            logger.error(this.name + " exception in readMessage() - " + e.getMessage());
            userHandler.process(this, EXIT_CODE.get());
        }
        return message;
    }

    @Override
    public void stop() {
        try {
            if (socket != null && writer != null && reader != null) {
                socket.close();
                writer.close();
                reader.close();
            }
        } catch (IOException e) {
            logger.error(this.name + " " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatUser chatUser = (ChatUser) o;
        return Objects.equals(id, chatUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User " + name;
    }
}
