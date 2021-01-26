package org.example.server.users;

import java.io.IOException;

public interface IChatUser extends Runnable {

    void start() throws IOException;

    void sendMessage(String message);

    String readMessage();

    void stop();

    String getName();

    void setName(String name);

}
