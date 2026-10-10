package com.roman.uppokertracker.domain;

public enum Suit {

    CLUBS('c'),
    DIAMONDS('d'),
    HEARTS('h'),
    SPADES('s');

    private final char symbol;

    Suit(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }


    public static Suit fromChar(char c) {
        c = Character.toLowerCase(c);

        for (Suit s : values()) {
            if (s.symbol == c) {
                return s;
            }
        }

        throw new IllegalArgumentException("Unknown suit symbol: " + c);
    }
}
