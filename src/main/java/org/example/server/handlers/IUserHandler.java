package org.example.server.handlers;

import org.example.server.users.IChatUser;

public interface IUserHandler {

    void process(IChatUser sender, String message);

    void stopAll();

}
