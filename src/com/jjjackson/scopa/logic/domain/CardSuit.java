package com.jjjackson.scopa.logic.domain;

public enum CardSuit {
    SPADES("s"),
    HEARTS("h"),
    DIAMONDS("d"),
    CLUBS("c");

    private String prefix;

    CardSuit(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
