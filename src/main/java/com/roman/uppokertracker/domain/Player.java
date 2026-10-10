package com.roman.uppokertracker.domain;

import java.util.Objects;

public class Player {

    private final String name;
    private final double stack;

    public Player(String name) {
        this(name, 0);
    }

    public Player(String name, double stack) {
        this.name = name;
        this.stack = stack;
    }

    public String name() {
        return name;
    }

    public double stack() {
        return stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
