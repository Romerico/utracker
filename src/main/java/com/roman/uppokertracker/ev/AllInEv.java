package com.roman.uppokertracker.ev;

import com.roman.uppokertracker.domain.Street;

public final class AllInEv {

    private final Street street;
    private final double equity;
    private final double pot;
    private final double expected;
    private final double realized;
    private final double luck;

    public AllInEv(Street street, double equity, double pot, double expected, double realized) {
        this.street = street;
        this.equity = equity;
        this.pot = pot;
        this.expected = expected;
        this.realized = realized;
        this.luck = realized - expected;
    }

    public Street street() {
        return street;
    }

    public double equity() {
        return equity;
    }

    public double pot() {
        return pot;
    }

    public double expected() {
        return expected;
    }

    public double realized() {
        return realized;
    }

    public double luck() {
        return luck;
    }
}
