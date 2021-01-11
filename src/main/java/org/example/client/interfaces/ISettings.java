package org.example.client.interfaces;

public interface ISettings {

    void load(String file);

    int getPort();

    String getHost();

    int getConnections();

}
