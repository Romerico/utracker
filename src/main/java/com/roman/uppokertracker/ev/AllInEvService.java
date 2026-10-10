package com.roman.uppokertracker.ev;

import com.roman.uppokertracker.domain.Action;
import com.roman.uppokertracker.domain.Board;
import com.roman.uppokertracker.domain.Card;
import com.roman.uppokertracker.domain.Hand;
import com.roman.uppokertracker.domain.Player;
import com.roman.uppokertracker.domain.Street;
import com.roman.uppokertracker.domain.SummaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AllInEvService {

    private final EquityCalculator equityCalculator = new EquityCalculator();

    public Optional<AllInEv> calculate(Hand hand) {
        if (hand.heroCards() == null || hand.heroCards().size() != 2) {
            return Optional.empty();
        }

        Street allInStreet = lastAllInStreet(hand);
        if (allInStreet == null || allInStreet == Street.RIVER) {
            return Optional.empty();
        }

        List<Card> boardAtAllIn = boardUpTo(hand.board(), allInStreet);
        if (allInStreet == Street.FLOP && boardAtAllIn.size() < 3) {
            return Optional.empty();
        }
        if (allInStreet == Street.TURN && (hand.board().turn() == null || boardAtAllIn.size() < 4)) {
            return Optional.empty();
        }

        List<List<Card>> opponents = shownOpponents(hand);
        if (opponents.isEmpty()) {
            return Optional.empty();
        }

        double pot = potSize(hand);
        if (pot <= 0) {
            return Optional.empty();
        }

        EquityResult equity = equityCalculator.calculate(hand.heroCards(), opponents, boardAtAllIn);
        double expected = equity.expectedValue(pot);
        double realized = heroCollected(hand);
        return Optional.of(new AllInEv(allInStreet, equity.heroEquity(), pot, expected, realized));
    }

    private Street lastAllInStreet(Hand hand) {
        Street last = null;
        for (Action action : hand.actions()) {
            if (action.allIn() || action.type().isAllIn()) {
                last = action.street();
            }
        }
        return last;
    }

    private List<List<Card>> shownOpponents(Hand hand) {
        List<List<Card>> opponents = new ArrayList<>();
        Player hero = hand.hero();
        for (Map.Entry<String, List<Card>> entry : hand.opponentHands().entrySet()) {
            if (hero != null && entry.getKey().equals(hero.name())) {
                continue;
            }
            if (entry.getValue() != null && entry.getValue().size() == 2) {
                opponents.add(entry.getValue());
            }
        }
        return opponents;
    }

    private List<Card> boardUpTo(Board board, Street street) {
        List<Card> cards = new ArrayList<>();
        if (street == Street.PREFLOP) {
            return cards;
        }
        if (board.flop() != null) {
            cards.addAll(board.flop());
        }
        if (street == Street.FLOP) {
            return cards;
        }
        if (board.turn() != null) {
            cards.add(board.turn());
        }
        if (street == Street.TURN) {
            return cards;
        }
        if (board.river() != null) {
            cards.add(board.river());
        }
        return cards;
    }

    private double potSize(Hand hand) {
        if (hand.summary().pot() > 0) {
            return hand.summary().pot();
        }
        return hand.summary().entries().stream()
                .mapToDouble(SummaryEntry::collected)
                .sum();
    }

    private double heroCollected(Hand hand) {
        Player hero = hand.hero();
        return hand.summary().entries().stream()
                .filter(entry -> entry.player().equals(hero))
                .mapToDouble(SummaryEntry::collected)
                .sum();
    }
}
