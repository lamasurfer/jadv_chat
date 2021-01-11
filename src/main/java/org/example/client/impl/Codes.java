package org.example.client.impl;

public enum Codes {

    NAME_CODE("/name"),
    CONTACTS_CODE("/cont"),
    EXIT_CODE("/exit");

    private final String name;

    Codes(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
