package org.example.server.handlers;

import org.example.server.users.IChatUser;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static org.example.common.Codes.*;

public class UserHandler implements IUserHandler {

    private final Logger logger;
    private final Set<IChatUser> users = new HashSet<>();

    public UserHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public synchronized void process(IChatUser sender, String message) {
        logger.info("Processing - " + sender.getName() + " - " + message);
        if (message.startsWith(NAME_CODE.get())) {
            processNameCode(sender, message);
        } else if (message.startsWith(CONTACTS_CODE.get())) {
            sender.sendMessage(getUsersAsString());
        } else if (message.startsWith(EXIT_CODE.get())) {
             processExitCode(sender);
        } else {
            sender.sendMessage("You said: " + message);
            sendOther(sender, sender.getName() + " said: " + message);
        }
        logger.info("Processed - " + sender.getName() + " - " + message);
    }

    void processNameCode(IChatUser sender, String message) {
        if (sender.getName() == null) {
            // если новый
            sender.setName(message.substring(6).trim()); // задает ник
            users.add(sender); // добавляет в список
            sender.sendMessage("Registered as " + sender.getName()); // уведомляет отправителя
            sendOther(sender, sender.getName() + " enters chat"); // уведомляет остальных
        } else {
            // если смена ника
            String oldName = sender.getName(); // старый ник
            sender.setName(message.substring(6)); // задает новый ник
            sender.sendMessage("Nickname changed to " + sender.getName()); // уведомляет отправителя
            sendOther(sender, oldName + " changed nickname to " + sender.getName()); // уведомляет остальных
        }
    }

    void processExitCode(IChatUser sender) {
        users.remove(sender); // удаляется из списка контактов
        sender.stop(); // останавливается чтение тут
        sendOther(sender, sender.getName() + " leaves chat"); // уведомляет остальных
    }

    // отправить всем, кроме отправителя
    void sendOther(IChatUser sender, String message) {
        for (IChatUser receiver : users) {
            if (!receiver.equals(sender)) {
                receiver.sendMessage(message);
            }
        }
    }

    // получить список доступных пользователей строкой
    String getUsersAsString() {
        StringBuilder sb = new StringBuilder("Now in chat:\n");
        for (IChatUser user : users) {
            sb.append(user).append("\n");
        }
        return sb.toString();
    }

    // отправить всем
    void sendAll(String message) {
        for (IChatUser receiver : users) {
            receiver.sendMessage(message);
        }
    }

    // останавливает всех когда сервер выключается из консоли
    @Override
    public void stopAll() {
        sendAll("Server stopped, please exit");
        for (IChatUser user : users) {
            user.stop();
        }
    }
}
