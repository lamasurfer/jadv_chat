package org.example.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings implements ISettings {

    public static final String PORT_KEY = "port";
    public static final String HOST_KEY = "host";
    public static final String CONNECTIONS_KEY = "maxConnections";

    private final Properties properties = new Properties();

    public Settings(String file) {
        load(file);
    }

    private void load(String file) {
        try {
            properties.loadFromXML(new FileInputStream(file));
        } catch (IOException e) {
            System.out.println("File not found! Default settings loaded!");
        }
    }

    @Override
    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT_KEY, "8080"));
    }

    @Override
    public String getHost() {
        return properties.getProperty(HOST_KEY, "127.0.0.1");
    }

    @Override
    public int getConnections() {
        return Integer.parseInt(properties.getProperty(CONNECTIONS_KEY, "1"));
    }
}
