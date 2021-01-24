package org.example.server.users;

public interface IChatUser extends Runnable {

    void sendMessage(String message);

    String readMessage();

    void stop();

    String getName();

    void setName(String name);

}
