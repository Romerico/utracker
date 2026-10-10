package com.roman.uppokertracker.domain;

public class Action {

    private final Player player;
    private final Street street;
    private final ActionType type;
    private final double amount;
    private final boolean allIn;

    public Action(Player player, ActionType type, Street street) {
        this(player, street, type, 0, false);
    }

    public Action(Player player, Street street, ActionType type, double amount) {
        this(player, street, type, amount, false);
    }

    public Action(Player player, Street street, ActionType type, double amount, boolean allIn) {
        this.player = player;
        this.street = street;
        this.type = type;
        this.amount = amount;
        this.allIn = allIn;
    }

    public Player player() {
        return player;
    }

    public Street street() {
        return street;
    }

    public ActionType type() {
        return type;
    }

    public double amount() {
        return amount;
    }

    public boolean allIn() {
        return allIn;
    }
}
