package com.roman.uppokertracker.domain;

public enum ActionType {
    FOLD, CALL, RAISE, CHECK, BET, ALL_IN;

    public boolean isFold() {
        return this == FOLD;
    }

    public boolean isCall() {
        return this == CALL;
    }

    public boolean isBet() {
        return this == BET;
    }

    public boolean isRaise() {
        return this == RAISE;
    }

    public boolean isCheck() {
        return this == CHECK;
    }

    public boolean isAllIn() {
        return this == ALL_IN;
    }

    public boolean isVoluntary() {
        return this == CALL || this == RAISE || this == BET || this == ALL_IN;
    }

    public boolean isAggressive() {
        return this == BET || this == RAISE || this == ALL_IN;
    }
}
