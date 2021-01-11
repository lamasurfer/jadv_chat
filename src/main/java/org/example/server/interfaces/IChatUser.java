package org.example.server.interfaces;

public interface IChatUser extends Runnable {

    void sendMessage(String message);

    String readMessage();

    void stop();

    String getName();

    void setName(String name);

}
