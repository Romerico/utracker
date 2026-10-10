package com.roman.uppokertracker.domain;

public enum Rank {

    TWO('2', 2),
    THREE('3', 3),
    FOUR('4', 4),
    FIVE('5', 5),
    SIX('6', 6),
    SEVEN('7', 7),
    EIGHT('8', 8),
    NINE('9', 9),
    TEN('T', 10),
    JACK('J', 11),
    QUEEN('Q', 12),
    KING('K', 13),
    ACE('A', 14);

    private final char symbol;
    private final int value;

    Rank(char symbol, int value) {
        this.symbol = symbol;
        this.value = value;
    }

    public char symbol() {
        return symbol;
    }

    public int value() {
        return value;
    }

    // 🔥 fromChar — как ты просил
    public static Rank fromChar(char c) {
        c = Character.toUpperCase(c);

        for (Rank r : values()) {
            if (r.symbol == c) {
                return r;
            }
        }

        throw new IllegalArgumentException("Unknown rank symbol: " + c);
    }
}
