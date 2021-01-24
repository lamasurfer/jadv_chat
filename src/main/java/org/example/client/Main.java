package org.example.client;


import org.example.client.impl.ChatClient;
import org.example.client.impl.ChatUI;
import org.example.client.interfaces.IChatClient;
import org.example.client.interfaces.IChatUI;
import org.example.common.ISettings;
import org.example.common.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger("Client logger");

        final ISettings settings = new Settings("src/main/resources/settings.txt");
        final IChatClient chatClient = new ChatClient(settings, logger);


        final Scanner scanner = new Scanner(System.in);
        final Logger historyLogger = LoggerFactory.getLogger("History logger");

        IChatUI chatUi = new ChatUI(chatClient, scanner, historyLogger);

        chatUi.startUserInteraction();

    }
}
