package org.example.common;

public enum Codes {

    NAME_CODE("/name"),
    CONTACTS_CODE("/cont"),
    HELP_CODE("/help"),
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
