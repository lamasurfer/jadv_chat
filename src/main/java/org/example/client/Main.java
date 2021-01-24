package org.example.client;


import org.example.common.ISettings;
import org.example.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger("Client logger");

        final ISettings settings = new Settings("src/main/resources/settings.txt");
        final ChatClient chatClient = new ChatClient(settings, logger);


        final Scanner scanner = new Scanner(System.in);
        final Logger historyLogger = LoggerFactory.getLogger("History logger");

        ChatUI chatUi = new ChatUI(chatClient, scanner, historyLogger);

        chatUi.startUserInteraction();

    }
}
