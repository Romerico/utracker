package com.roman.uppokertracker.ev;

import com.roman.uppokertracker.domain.Card;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HandEvaluator {

    public HandValue evaluate(List<Card> cards) {
        if (cards == null || cards.size() < 5 || cards.size() > 7) {
            throw new IllegalArgumentException("Need 5 to 7 cards");
        }

        int[] rankCount = new int[15];
        int[] suitCount = new int[4];
        List<List<Integer>> ranksBySuit = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            ranksBySuit.add(new ArrayList<>());
        }

        for (Card card : cards) {
            int rank = card.rank().value();
            int suit = card.suit().ordinal();
            rankCount[rank]++;
            suitCount[suit]++;
            ranksBySuit.get(suit).add(rank);
        }

        int flushSuit = -1;
        for (int s = 0; s < 4; s++) {
            if (suitCount[s] >= 5) {
                flushSuit = s;
                break;
            }
        }

        if (flushSuit >= 0) {
            List<Integer> flushRanks = ranksBySuit.get(flushSuit);
            flushRanks.sort(Comparator.reverseOrder());
            int straightFlushHigh = straightHigh(toPresent(flushRanks));
            if (straightFlushHigh > 0) {
                return HandValue.of(HandCategory.STRAIGHT_FLUSH, straightFlushHigh);
            }
        }

        int quads = 0;
        int[] trips = new int[2];
        int tripCount = 0;
        int[] pairs = new int[3];
        int pairCount = 0;
        int[] singles = new int[7];
        int singleCount = 0;

        for (int rank = 14; rank >= 2; rank--) {
            int count = rankCount[rank];
            if (count == 4) {
                quads = rank;
            } else if (count == 3) {
                trips[tripCount++] = rank;
            } else if (count == 2) {
                pairs[pairCount++] = rank;
            } else if (count == 1) {
                singles[singleCount++] = rank;
            }
        }

        if (quads != 0) {
            int kicker = highestExcept(quads, trips, tripCount, pairs, pairCount, singles, singleCount);
            return HandValue.of(HandCategory.FOUR_OF_A_KIND, quads, kicker);
        }

        if (tripCount > 0 && (pairCount > 0 || tripCount > 1)) {
            int three = trips[0];
            int pair = tripCount > 1 ? trips[1] : pairs[0];
            return HandValue.of(HandCategory.FULL_HOUSE, three, pair);
        }

        if (flushSuit >= 0) {
            List<Integer> flushRanks = ranksBySuit.get(flushSuit);
            flushRanks.sort(Comparator.reverseOrder());
            return HandValue.of(
                    HandCategory.FLUSH,
                    flushRanks.get(0),
                    flushRanks.get(1),
                    flushRanks.get(2),
                    flushRanks.get(3),
                    flushRanks.get(4)
            );
        }

        int straight = straightHigh(rankCount);
        if (straight > 0) {
            return HandValue.of(HandCategory.STRAIGHT, straight);
        }

        if (tripCount > 0) {
            List<Integer> rest = remainingRanks(pairs, pairCount, singles, singleCount);
            return HandValue.of(HandCategory.THREE_OF_A_KIND, trips[0], rest.get(0), rest.get(1));
        }

        if (pairCount >= 2) {
            int kicker = pairCount >= 3 ? pairs[2] : 0;
            if (singleCount > 0) {
                kicker = Math.max(kicker, singles[0]);
            }
            return HandValue.of(HandCategory.TWO_PAIR, pairs[0], pairs[1], kicker);
        }

        if (pairCount == 1) {
            return HandValue.of(HandCategory.PAIR, pairs[0], singles[0], singles[1], singles[2]);
        }

        return HandValue.of(
                HandCategory.HIGH_CARD,
                singles[0],
                singles[1],
                singles[2],
                singles[3],
                singles[4]
        );
    }

    private static boolean[] toPresent(List<Integer> ranks) {
        boolean[] present = new boolean[15];
        for (int rank : ranks) {
            present[rank] = true;
        }
        return present;
    }

    private static int straightHigh(int[] rankCount) {
        boolean[] present = new boolean[15];
        for (int rank = 2; rank <= 14; rank++) {
            present[rank] = rankCount[rank] > 0;
        }
        return straightHigh(present);
    }

    private static int straightHigh(boolean[] present) {
        present[1] = present[14];
        for (int high = 14; high >= 5; high--) {
            if (present[high] && present[high - 1] && present[high - 2] && present[high - 3] && present[high - 4]) {
                return high;
            }
        }
        return 0;
    }

    private static int highestExcept(
            int excluded,
            int[] trips,
            int tripCount,
            int[] pairs,
            int pairCount,
            int[] singles,
            int singleCount
    ) {
        for (int i = 0; i < tripCount; i++) {
            if (trips[i] != excluded) {
                return trips[i];
            }
        }
        for (int i = 0; i < pairCount; i++) {
            if (pairs[i] != excluded) {
                return pairs[i];
            }
        }
        for (int i = 0; i < singleCount; i++) {
            if (singles[i] != excluded) {
                return singles[i];
            }
        }
        return 0;
    }

    private static List<Integer> remainingRanks(int[] pairs, int pairCount, int[] singles, int singleCount) {
        List<Integer> rest = new ArrayList<>();
        for (int i = 0; i < pairCount; i++) {
            rest.add(pairs[i]);
        }
        for (int i = 0; i < singleCount; i++) {
            rest.add(singles[i]);
        }
        rest.sort(Comparator.reverseOrder());
        return rest;
    }
}
