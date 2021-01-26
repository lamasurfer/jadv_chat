package org.example.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.example.common.Codes.EXIT_CODE;
import static org.example.common.Codes.NAME_CODE;

public class ChatUI {

    private static final String START_MESSAGE = "Enter your nickname and press enter to start chatting or 0 to exit";
    private static final String COMMANDS = "\n- type /name and your new nickname to change nickname" +
            "\n- type /contacts to see all chat users" +
            "\n- type /exit to leave chat" +
            "\n- type /help to get chat commands" +
            "\n- or type your message" +
            "\n";

    private final ChatClient chatClient;
    private final Scanner scanner;
    private final Logger logger;

    public ChatUI(ChatClient chatClient, Scanner scanner) {
        this.chatClient = chatClient;
        this.scanner = scanner;
        this.logger = LoggerFactory.getLogger("history logger");
    }

    public ChatUI(ChatClient chatClient, Scanner scanner, Logger logger) {
        this.chatClient = chatClient;
        this.scanner = scanner;
        this.logger = logger;
    }

    public void startChatting() {
        String name;
        do {
            System.out.println(START_MESSAGE);
            name = scanner.nextLine();
            if ("0".equals(name)) {
                return;
            }
        } while (name.isBlank());

        try {
            chatClient.start();
        } catch (IOException e) {
            System.out.println("Problems starting Client!");
            return;
        }

        final Consumer<String> messageConsumer = System.out::println;

        System.out.println(COMMANDS);

        final ExecutorService readerExecutor = Executors.newSingleThreadExecutor();
        readerExecutor.execute(() -> listen(messageConsumer));
        readerExecutor.shutdown();

        chatClient.sendMessage(NAME_CODE.get() + " " + name);

        while (true) {
            String message = scanner.nextLine();
            if (EXIT_CODE.get().equals(message)) {
                chatClient.sendMessage(message);
                break;
            }
            chatClient.sendMessage(message);
            logger.info("sent - " + message);
        }
        chatClient.stop();
    }

    void listen(Consumer<String> messageConsumer) {
        String message;
        while ((message = chatClient.readMessage()) != null) {
            messageConsumer.accept(message);
            logger.info("received - " + message);
        }
    }
}
