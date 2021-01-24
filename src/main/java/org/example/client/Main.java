package org.example.client;


import org.example.common.ISettings;
import org.example.common.Settings;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        final ISettings settings = new Settings("src/main/resources/settings.txt");
        final ChatClient chatClient = new ChatClient(settings);

        final Scanner scanner = new Scanner(System.in);

        final ChatUI chatUi = new ChatUI(chatClient, scanner);
        chatUi.startUserInteraction();

    }
}
