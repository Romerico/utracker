package com.roman.uppokertracker.domain;

import java.util.List;

public class Board {
    private final List<Card> flop; // 3 карты
    private final Card turn;       // 1 карта
    private final Card river;      // 1 карта

    public Board(List<Card> flop, Card turn, Card river) {
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    public List<Card> flop() { return flop; }
    public Card turn() { return turn; }
    public Card river() { return river; }
}