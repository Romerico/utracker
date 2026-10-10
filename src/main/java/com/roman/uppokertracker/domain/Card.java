package com.roman.uppokertracker.domain;


    public class Card {
        private final Rank rank;
        private final Suit suit;

        private static final Card[][] CACHE =
                new Card[Rank.values().length][Suit.values().length];

        static {
            for (Rank r : Rank.values()) {
                for (Suit s : Suit.values()) {
                    CACHE[r.ordinal()][s.ordinal()] = new Card(r, s);
                }
            }
        }
        public static Card fromString(String s) {
            if (s == null || s.length() != 2)
                throw new IllegalArgumentException("Invalid card: " + s);

            Rank rank = Rank.fromChar(s.charAt(0));
            Suit suit = Suit.fromChar(s.charAt(1));

            return of(rank, suit);
        }

        private Card(Rank rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }

        public static Card of(Rank rank, Suit suit) {
            return CACHE[rank.ordinal()][suit.ordinal()];
        }

        public Rank rank() { return rank; }
        public Suit suit() { return suit; }

        @Override
        public String toString() {
            return rank.name() + suit.name();
        }
    }



