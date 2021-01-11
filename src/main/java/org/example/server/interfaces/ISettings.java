package org.example.server.interfaces;

public interface ISettings {

    void load(String file);

    int getPort();

    String getHost();

    int getConnections();

}
