package org.example.server.handlers;

import org.example.server.users.IChatUser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.example.common.Codes.*;

public class UserHandler implements IUserHandler {

    public static final String STOP_MESSAGE = "Server stopped, please exit!";
    public static final String REG_CONFIRMATION = "Registered as ";
    public static final String REG_NOTIFICATION = " enters chat";
    public static final String MSG_CONFIRMATION = "You said: ";
    public static final String MSG_NOTIFICATION = " said: ";
    public static final String NAME_CONFIRMATION = "Nickname changed to ";
    public static final String NAME_NOTIFICATION = " changed nickname to ";
    public static final String EXIT_MESSAGE = " leaves chat";
    public static final String NAME_WARNING = "Name is to short!";
    public static final String HELP_MESSAGE =
            "/name - change name, /cont - get contacts, /help - get help, /exit - exit";

    private final Set<IChatUser> users = new HashSet<>();

    @Override
    public synchronized void process(IChatUser sender, String message) {
        if (message.startsWith(NAME_CODE.get())) {
            processNameCode(sender, message);
        } else if (message.startsWith(CONTACTS_CODE.get())) {
            processContactsCode(sender);
        } else if (message.startsWith(HELP_CODE.get())) {
            processHelpCode(sender);
        } else if (message.startsWith(EXIT_CODE.get())) {
            processExitCode(sender);
        } else {
            processMessage(sender, message);
        }
    }

    void processNameCode(IChatUser sender, String message) {
        final String name = message.replace(NAME_CODE.get(), "").trim();
        if (name.isBlank()) {
            sender.sendMessage(NAME_WARNING);
            return;
        }
        if (sender.getName() == null) {
            sender.setName(name);
            addUser(sender);
            sender.sendMessage(REG_CONFIRMATION + sender.getName());
            sendOther(sender, sender.getName() + REG_NOTIFICATION);
        } else {
            String oldName = sender.getName();
            sender.setName(name);
            sender.sendMessage(NAME_CONFIRMATION + sender.getName());
            sendOther(sender, oldName + NAME_NOTIFICATION + sender.getName());
        }
    }

    void processContactsCode(IChatUser sender) {
        sender.sendMessage(getUsersAsString());
    }

    void processHelpCode(IChatUser sender) {
        sender.sendMessage(HELP_MESSAGE);
    }

    void processExitCode(IChatUser sender) {
        if (users.contains(sender)) {
            sendOther(sender, sender.getName() + EXIT_MESSAGE);
            removeUser(sender);
        }
        sender.stop();
    }

    void processMessage(IChatUser sender, String message) {
        sender.sendMessage(MSG_CONFIRMATION + message);
        sendOther(sender, sender.getName() + MSG_NOTIFICATION + message);
    }

    void sendOther(IChatUser sender, String message) {
        for (IChatUser receiver : users) {
            if (!receiver.equals(sender)) {
                receiver.sendMessage(message);
            }
        }
    }

    void sendAll(String message) {
        for (IChatUser receiver : users) {
            receiver.sendMessage(message);
        }
    }

    void addUser(IChatUser chatUser) {
        users.add(chatUser);
    }

    void removeUser(IChatUser chatUser) {
        users.remove(chatUser);
    }

    Set<IChatUser> getUsers() {
        return new HashSet<>(users);
    }

    String getUsersAsString() {
        final StringBuilder sb = new StringBuilder("Now in chat:\n");
        for (IChatUser user : users) {
            sb.append(user).append("\n");
        }
        return sb.toString();
    }

    @Override
    public synchronized void stopAll() {
        sendAll(STOP_MESSAGE);
        Iterator<IChatUser> it = users.iterator();
        while (it.hasNext()) {
            IChatUser user = it.next();
            user.stop();
            it.remove();
        }
    }
}
