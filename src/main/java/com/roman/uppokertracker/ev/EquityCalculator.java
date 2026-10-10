package com.roman.uppokertracker.ev;

import com.roman.uppokertracker.domain.Card;
import com.roman.uppokertracker.domain.Rank;
import com.roman.uppokertracker.domain.Suit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EquityCalculator {

    private final HandEvaluator evaluator = new HandEvaluator();

    public EquityResult calculate(List<Card> hero, List<List<Card>> opponents, List<Card> board) {
        List<List<Card>> holes = new ArrayList<>();
        holes.add(hero);
        holes.addAll(opponents);
        return calculate(holes, board);
    }

    public EquityResult calculate(List<List<Card>> holeCards, List<Card> board) {
        if (holeCards == null || holeCards.size() < 2) {
            throw new IllegalArgumentException("Need at least two hands");
        }
        List<Card> knownBoard = board == null ? List.of() : board;
        if (knownBoard.size() > 5) {
            throw new IllegalArgumentException("Board cannot have more than 5 cards");
        }

        Set<Card> used = new HashSet<>();
        for (List<Card> hole : holeCards) {
            if (hole.size() != 2) {
                throw new IllegalArgumentException("Each player needs exactly two hole cards");
            }
            addUnique(used, hole);
        }
        addUnique(used, knownBoard);

        List<Card> deck = remainingDeck(used);
        int need = 5 - knownBoard.size();
        int players = holeCards.size();
        double[] shares = new double[players];

        Card[] completeBoard = new Card[5];
        for (int i = 0; i < knownBoard.size(); i++) {
            completeBoard[i] = knownBoard.get(i);
        }

        long boards = enumerate(deck, need, 0, completeBoard, knownBoard.size(), holeCards, shares);
        if (boards == 0) {
            throw new IllegalStateException("No boards to evaluate");
        }
        for (int i = 0; i < players; i++) {
            shares[i] /= boards;
        }
        return new EquityResult(shares, boards);
    }

    public double expectedValue(double equity, double pot) {
        return equity * pot;
    }

    private long enumerate(
            List<Card> deck,
            int need,
            int start,
            Card[] board,
            int filled,
            List<List<Card>> holeCards,
            double[] shares
    ) {
        if (need == 0) {
            award(board, holeCards, shares);
            return 1;
        }

        long count = 0;
        int last = deck.size() - need;
        for (int i = start; i <= last; i++) {
            board[filled] = deck.get(i);
            count += enumerate(deck, need - 1, i + 1, board, filled + 1, holeCards, shares);
        }
        return count;
    }

    private void award(Card[] board, List<List<Card>> holeCards, double[] shares) {
        HandValue best = null;
        int winners = 0;
        HandValue[] values = new HandValue[holeCards.size()];

        for (int i = 0; i < holeCards.size(); i++) {
            List<Card> seven = new ArrayList<>(7);
            seven.addAll(holeCards.get(i));
            seven.add(board[0]);
            seven.add(board[1]);
            seven.add(board[2]);
            seven.add(board[3]);
            seven.add(board[4]);
            HandValue value = evaluator.evaluate(seven);
            values[i] = value;
            if (best == null || value.compareTo(best) > 0) {
                best = value;
                winners = 1;
            } else if (value.equals(best)) {
                winners++;
            }
        }

        double share = 1.0 / winners;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(best)) {
                shares[i] += share;
            }
        }
    }

    private static void addUnique(Set<Card> used, List<Card> cards) {
        for (Card card : cards) {
            if (!used.add(card)) {
                throw new IllegalArgumentException("Duplicate card: " + card);
            }
        }
    }

    private static List<Card> remainingDeck(Set<Card> used) {
        List<Card> deck = new ArrayList<>(52 - used.size());
        for (Rank rank : Rank.values()) {
            for (Suit suit : Suit.values()) {
                Card card = Card.of(rank, suit);
                if (!used.contains(card)) {
                    deck.add(card);
                }
            }
        }
        return deck;
    }
}
