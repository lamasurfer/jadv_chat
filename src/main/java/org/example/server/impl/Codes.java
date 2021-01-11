package org.example.server.impl;

public enum Codes {

    NAME_CODE("/name"),
    CONTACTS_CODE("/cont"),
    EXIT_CODE("/exit");

    private final String code;

    Codes(String code) {
        this.code = code;
    }

    public String get() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
