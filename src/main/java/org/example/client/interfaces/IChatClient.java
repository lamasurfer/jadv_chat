package org.example.client.interfaces;

import java.io.IOException;
import java.net.Socket;

public interface IChatClient {

    void start();

    Socket startSocket(String host, int port) throws IOException;

    boolean isConnected();

    void send(String message);

    String read();

    void stop();
}
