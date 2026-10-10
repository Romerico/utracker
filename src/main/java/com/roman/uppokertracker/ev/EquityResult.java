package com.roman.uppokertracker.ev;

import java.util.Arrays;

public final class EquityResult {

    private final double[] shares;
    private final long boards;

    public EquityResult(double[] shares, long boards) {
        this.shares = shares;
        this.boards = boards;
    }

    public double heroEquity() {
        return shares.length == 0 ? 0 : shares[0];
    }

    public double equity(int playerIndex) {
        return shares[playerIndex];
    }

    public double[] shares() {
        return Arrays.copyOf(shares, shares.length);
    }

    public long boards() {
        return boards;
    }

    public double expectedValue(double pot) {
        return heroEquity() * pot;
    }
}
