package com.roman.uppokertracker.domain;

import java.util.List;

public class SummaryEntry {

    private final Player player;
    private final double balance;
    private final double bet;
    private final double collected;
    private final double lost;
    private final double net;
    private final boolean folded;
    private final boolean showedCards;
    private final List<Card> shownCards;

    public SummaryEntry(Player player, double net) {
        this(player, 0, 0, Math.max(net, 0), Math.max(-net, 0), net, false, false, List.of());
    }

    public SummaryEntry(
            Player player,
            double balance,
            double bet,
            double collected,
            double lost,
            double net,
            boolean folded,
            boolean showedCards,
            List<Card> shownCards
    ) {
        this.player = player;
        this.balance = balance;
        this.bet = bet;
        this.collected = collected;
        this.lost = lost;
        this.net = net;
        this.folded = folded;
        this.showedCards = showedCards;
        this.shownCards = shownCards;
    }

    public Player player() {
        return player;
    }

    public double balance() {
        return balance;
    }

    public double bet() {
        return bet;
    }

    public double collected() {
        return collected;
    }

    public double lost() {
        return lost;
    }

    public double net() {
        return net;
    }

    public boolean folded() {
        return folded;
    }

    public boolean showedCards() {
        return showedCards;
    }

    public List<Card> shownCards() {
        return shownCards;
    }
}
