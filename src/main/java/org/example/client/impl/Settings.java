package org.example.client.impl;


import org.example.client.interfaces.ISettings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings implements ISettings {

    private final Properties properties = new Properties();

    public Settings(String file) {
        load(file);
    }

    @Override
    public void load(String file) {
        try {
            properties.loadFromXML(new FileInputStream(file));
        } catch (IOException e) {
            System.out.println("File not found! Default settings loaded!");
        }
    }

    @Override
    public int getPort() {
        return Integer.parseInt(properties.getProperty("port", "8080"));
    }

    @Override
    public String getHost() {
        return properties.getProperty("host", "127.0.0.1");
    }

    @Override
    public int getConnections() {
        return Integer.parseInt(properties.getProperty("maxConnections", "10"));
    }
}
