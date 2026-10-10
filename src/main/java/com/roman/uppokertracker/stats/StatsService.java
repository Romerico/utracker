package com.roman.uppokertracker.stats;

import com.roman.uppokertracker.domain.Action;
import com.roman.uppokertracker.domain.Hand;
import com.roman.uppokertracker.domain.Player;
import com.roman.uppokertracker.domain.Street;
import com.roman.uppokertracker.domain.SummaryEntry;
import com.roman.uppokertracker.ev.AllInEv;
import com.roman.uppokertracker.ev.AllInEvService;

import java.util.List;

public class StatsService {

    public Stats calculate(List<Hand> hands) {
        Stats stats = new Stats();

        int vpip = 0;
        int pfr = 0;
        int threeBet = 0;
        int aggressionActions = 0;
        int calls = 0;
        int cbetFlop = 0;
        int foldToCbetFlop = 0;
        double totalProfit = 0;

        for (Hand hand : hands) {
            Player hero = hand.hero();
            boolean heroWasPfa = false;
            boolean preflopRaiseSeen = false;

            for (Action action : hand.actions()) {
                if (action.street() == Street.PREFLOP && action.type().isRaise()) {
                    if (action.player().equals(hero) && preflopRaiseSeen) {
                        threeBet++;
                    }
                    if (action.player().equals(hero)) {
                        heroWasPfa = true;
                    }
                    preflopRaiseSeen = true;
                }

                if (!action.player().equals(hero)) {
                    continue;
                }

                if (action.street() == Street.PREFLOP && action.type().isVoluntary()) {
                    vpip++;
                }
                if (action.street() == Street.PREFLOP && action.type().isRaise()) {
                    pfr++;
                }
                if (action.type().isAggressive()) {
                    aggressionActions++;
                }
                if (action.type().isCall()) {
                    calls++;
                }
            }

            if (heroWasPfa && didHeroCbetFlop(hand, hero)) {
                cbetFlop++;
            }
            if (didHeroFoldToCbetFlop(hand, hero)) {
                foldToCbetFlop++;
            }

            totalProfit += hand.summary().entries().stream()
                    .filter(entry -> entry.player().equals(hero))
                    .mapToDouble(SummaryEntry::net)
                    .sum();
        }

        int handsCount = hands.size();
        stats.vpip = percent(vpip, handsCount);
        stats.pfr = percent(pfr, handsCount);
        stats.threeBet = percent(threeBet, handsCount);
        stats.aggression = ratio(aggressionActions, calls);
        stats.afq = percent(aggressionActions, aggressionActions + calls);
        stats.cbetFlop = percent(cbetFlop, handsCount);
        stats.foldToCbetFlop = percent(foldToCbetFlop, handsCount);
        stats.winrate = handsCount == 0 ? 0 : totalProfit / (handsCount / 100.0);
        return stats;
    }

    private boolean didHeroCbetFlop(Hand hand, Player hero) {
        for (Action action : hand.actions()) {
            if (action.street() == Street.FLOP
                    && action.player().equals(hero)
                    && action.type().isBet()) {
                return true;
            }
        }
        return false;
    }

    private boolean didHeroFoldToCbetFlop(Hand hand, Player hero) {
        boolean sawCbet = false;
        for (Action action : hand.actions()) {
            if (action.street() != Street.FLOP) {
                continue;
            }
            if (action.type().isBet() && !action.player().equals(hero)) {
                sawCbet = true;
            }
            if (sawCbet && action.player().equals(hero) && action.type().isFold()) {
                return true;
            }
        }
        return false;
    }

    private double percent(double value, double total) {
        return total == 0 ? 0 : (value / total) * 100.0;
    }

    private double ratio(double a, double b) {
        return b == 0 ? a : a / b;
    }
}
