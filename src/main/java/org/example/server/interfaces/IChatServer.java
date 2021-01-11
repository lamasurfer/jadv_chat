package org.example.server.interfaces;

import java.io.IOException;
import java.net.ServerSocket;

public interface IChatServer {

    void start();

    ServerSocket startServerSocket(int port) throws IOException;

    void stop();
}
