package org.example.integration;

import org.example.client.ChatClient;
import org.example.common.Settings;
import org.example.server.ChatServer;
import org.example.server.handlers.UserHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.example.common.Codes.*;
import static org.example.server.handlers.UserHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegrationTest {

    private static final Settings settings = new Settings("src/main/resources/settings.txt");

    private static ChatServer chatServer;
    private static Thread thread;

    private final ChatClient user1 = new ChatClient(settings);
    private final ChatClient user2 = new ChatClient(settings);
    private final ChatClient user3 = new ChatClient(settings);

    @BeforeAll
    static void init() {

        chatServer = new ChatServer(new UserHandler(), settings);
        thread = new Thread(() -> {
            try {
                chatServer.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        thread.start();
    }

    @Test
    void test() throws InterruptedException, IOException {

        TimeUnit.SECONDS.sleep(1);

        user1.start();
        user1.sendMessage(NAME_CODE.get() + " " + "Joe");
        assertEquals(REG_CONFIRMATION + "Joe", user1.readMessage());
        user1.sendMessage(HELP_CODE.get());
        assertEquals(HELP_MESSAGE, user1.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user2.start();
        user2.sendMessage(NAME_CODE.get() + " " + "Scott");
        assertEquals(REG_CONFIRMATION + "Scott", user2.readMessage());
        assertEquals("Scott" + REG_NOTIFICATION, user1.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user3.start();
        user3.sendMessage(NAME_CODE.get() + " " + "Jeff");
        assertEquals(REG_CONFIRMATION + "Jeff", user3.readMessage());
        assertEquals("Jeff" + REG_NOTIFICATION, user2.readMessage());
        assertEquals("Jeff" + REG_NOTIFICATION, user1.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user1.sendMessage("Hello!");
        assertEquals(MSG_CONFIRMATION + "Hello!", user1.readMessage());
        assertEquals("Joe" + MSG_NOTIFICATION + "Hello!", user2.readMessage());
        assertEquals("Joe" + MSG_NOTIFICATION + "Hello!", user3.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user1.sendMessage(EXIT_CODE.get());
        user1.stop();
        assertEquals("Joe" + EXIT_MESSAGE, user2.readMessage());
        assertEquals("Joe" + EXIT_MESSAGE, user3.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user2.sendMessage(NAME_CODE.get() + " " + "Lara");
        assertEquals(NAME_CONFIRMATION + "Lara", user2.readMessage());
        assertEquals("Scott" + NAME_NOTIFICATION + "Lara", user3.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user2.sendMessage(EXIT_CODE.get());
        assertEquals("Lara" + EXIT_MESSAGE, user3.readMessage());

        TimeUnit.SECONDS.sleep(1);

        user3.sendMessage(EXIT_CODE.get());

        user2.stop();
        user3.stop();

        TimeUnit.SECONDS.sleep(1);
    }

    @AfterAll
    static void close() {
        chatServer.stop();
        thread.interrupt();
    }
}