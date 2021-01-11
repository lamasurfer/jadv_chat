package org.example.server.interfaces;

public interface IUserHandler {

    void process(IChatUser sender, String message);

    void stopAll();

}
