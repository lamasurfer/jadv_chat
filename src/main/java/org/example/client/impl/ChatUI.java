package org.example.client.impl;


import org.example.client.interfaces.IChatClient;
import org.example.client.interfaces.IChatUI;
import org.slf4j.Logger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.example.common.Codes.EXIT_CODE;
import static org.example.common.Codes.NAME_CODE;

public class ChatUI implements IChatUI {

    private static final String START_MESSAGE = "Enter your nickname and press enter to start chatting or 0 to exit";
    private static final String COMMANDS =
            "\n- type /name and your new nickname to change nickname" +
                    "\n- type /contacts to see all chat users" +
                    "\n- type /exit to leave chat" +
                    "\n- or type your message";

    private final IChatClient chatClient;
    private final Scanner scanner;
    private final Logger logger;

    public ChatUI(IChatClient chatClient, Scanner scanner, Logger logger) {
        this.chatClient = chatClient;
        this.scanner = scanner;
        this.logger = logger;
    }

    @Override
    public void startUserInteraction() {

        String name;
        do {
            System.out.println(START_MESSAGE);
            name = scanner.nextLine();
            if ("0".equals(name)) {
                return;
            }
        } while (name.isBlank());

        chatClient.start();
        if (!chatClient.isConnected()) {
            System.out.println("Connection problems!");
            return;
        }

        final Consumer<String> messageConsumer = System.out::println;

        System.out.println(COMMANDS);

        final ExecutorService readerExecutor = Executors.newSingleThreadExecutor();
        readerExecutor.execute(() -> listen(messageConsumer));
        readerExecutor.shutdown();

        chatClient.send(NAME_CODE.get() + " " + name);

        while (true) {
            String message = scanner.nextLine();
            if (EXIT_CODE.get().equals(message)) {
                chatClient.send(message);
                break;
            }
            chatClient.send(message);
            logger.info("sent - " + message);
        }
        chatClient.stop();
    }

    void listen(Consumer<String> messageConsumer) {
        String message;
        while ((message = chatClient.read()) != null) {
            messageConsumer.accept(message);
            logger.info("received - " + message);
        }
    }
}
