package org.example.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static org.example.common.Settings.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SettingsTest {

    private static final String SETTINGS_FILE = "src/test/resources/settings.txt";
    private static final int PORT = 9999;
    private static final String HOST = "127.0.0.1";
    private static final int CONNECTIONS = 1;

    private static Settings settings;

    @BeforeAll
    public static void writeSettings() {

        final Properties properties = new Properties();
        properties.setProperty(PORT_KEY, String.valueOf(PORT));
        properties.setProperty(HOST_KEY, HOST);
        properties.setProperty(CONNECTIONS_KEY, String.valueOf(CONNECTIONS));

        try (FileOutputStream fileOutputStream = new FileOutputStream(SETTINGS_FILE)) {
            properties.storeToXML(fileOutputStream, "test");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        settings = new Settings(SETTINGS_FILE);
    }

    @Test
    void getPort_expectedBehaviour() {
        assertEquals(PORT, settings.getPort());
    }

    @Test
    void getHost_expectedBehaviour() {
        assertEquals(HOST, settings.getHost());
    }

    @Test
    void getConnections_expectedBehaviour() {
        assertEquals(CONNECTIONS, settings.getConnections());
    }

    @AfterAll
    static void deleteSettings() {
        try (PrintWriter writer = new PrintWriter(SETTINGS_FILE)) {
            writer.print("");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}