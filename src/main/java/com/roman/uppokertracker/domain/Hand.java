package com.roman.uppokertracker.domain;

import java.util.List;
import java.util.Map;

public class Hand {

    private final String id;
    private final String table;
    private final String limit;
    private final Player hero;
    private final List<Player> players;
    private final List<Card> heroCards;
    private final Board board;
    private final List<Action> actions;
    private final Summary summary;
    private final Map<String, List<Card>> opponentHands;
    private HandTimestamp timestamp;

    public Hand(
            String id,
            String table,
            String limit,
            Player hero,
            List<Player> players,
            List<Card> heroCards,
            Board board,
            List<Action> actions,
            Summary summary,
            Map<String, List<Card>> opponentHands
    ) {
        this.id = id;
        this.table = table;
        this.limit = limit;
        this.hero = hero;
        this.players = players;
        this.heroCards = heroCards;
        this.board = board;
        this.actions = actions;
        this.summary = summary;
        this.opponentHands = opponentHands;
    }

    public String id() {
        return id;
    }

    public String table() {
        return table;
    }

    public String limit() {
        return limit;
    }

    public Player hero() {
        return hero;
    }

    public List<Player> players() {
        return players;
    }

    public List<Card> heroCards() {
        return heroCards;
    }

    public Board board() {
        return board;
    }

    public List<Action> actions() {
        return actions;
    }

    public Summary summary() {
        return summary;
    }

    public Map<String, List<Card>> opponentHands() {
        return opponentHands;
    }

    public HandTimestamp timestamp() {
        return timestamp;
    }

    public void setTimestamp(HandTimestamp timestamp) {
        this.timestamp = timestamp;
    }
}
