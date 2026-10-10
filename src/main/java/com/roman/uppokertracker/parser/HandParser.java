package com.roman.uppokertracker.parser;

import com.roman.uppokertracker.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandParser {

    public Hand parse(String raw) {
        String[] lines = raw.split("\\R");

        String handId = parseHandId(lines);
        String tableName = parseTable(lines);
        String limit = parseLimit(lines);

        List<Player> players = parsePlayers(lines);
        Player hero = parseHero(lines, players);
        List<Card> heroCards = parseHeroCards(lines);
        Board board = parseBoard(lines);
        List<Action> actions = parseActions(lines, players);
        Summary summary = parseSummary(lines, players);
        Map<String, List<Card>> opponentHands = parseOpponentHands(lines, players);

        return new Hand(
                handId,
                tableName,
                limit,
                hero,
                players,
                heroCards,
                board,
                actions,
                summary,
                opponentHands
        );
    }

    private Player parseHero(String[] lines, List<Player> players) {
        for (String line : lines) {
            if (line.startsWith("Dealt to ")) {
                String heroName = line.substring("Dealt to ".length()).split(" ")[0];
                return findPlayer(players, heroName);
            }
        }
        throw new IllegalStateException("Hero not found in hand history");
    }

    private List<Card> parseHeroCards(String[] lines) {
        for (String line : lines) {
            if (line.startsWith("Dealt to ")) {
                return parseCards(extractFirstCards(line));
            }
        }
        return List.of();
    }

    private List<Player> parsePlayers(String[] lines) {
        List<Player> players = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("***")) {
                break;
            }
            if (!line.startsWith("Seat ")) {
                continue;
            }

            String[] parts = line.split(":", 2);
            if (parts.length < 2) {
                continue;
            }

            String nameAndStack = parts[1].trim();
            String name = nameAndStack.split(" ")[0];
            double stack = parseMoney(nameAndStack);
            players.add(new Player(name, stack));
        }

        return players;
    }

    private Board parseBoard(String[] lines) {
        List<Card> flop = List.of();
        Card turn = null;
        Card river = null;

        for (String line : lines) {
            if (line.startsWith("*** FLOP ***")) {
                flop = parseCards(extractFirstCards(line));
            } else if (line.startsWith("*** TURN ***")) {
                List<Card> cards = parseCards(extractLastCards(line));
                turn = cards.isEmpty() ? null : cards.get(0);
            } else if (line.startsWith("*** RIVER ***")) {
                List<Card> cards = parseCards(extractLastCards(line));
                river = cards.isEmpty() ? null : cards.get(0);
            }
        }

        return new Board(flop, turn, river);
    }

    private List<Action> parseActions(String[] lines, List<Player> players) {
        List<Action> actions = new ArrayList<>();
        Street street = Street.PREFLOP;
        boolean inSummary = false;

        for (String line : lines) {
            if (line.startsWith("*** SUMMARY ***")) {
                inSummary = true;
                continue;
            }
            if (inSummary) {
                continue;
            }

            if (line.startsWith("*** FLOP ***")) {
                street = Street.FLOP;
                continue;
            }
            if (line.startsWith("*** TURN ***")) {
                street = Street.TURN;
                continue;
            }
            if (line.startsWith("*** RIVER ***")) {
                street = Street.RIVER;
                continue;
            }
            if (line.startsWith("***") || line.startsWith("Dealt to ") || line.startsWith("Seat ")) {
                continue;
            }

            Player player = playerStarting(line, players);
            if (player == null) {
                continue;
            }

            boolean allIn = isAllIn(line);
            ActionType type = parseActionType(line);
            if (type == null && allIn) {
                type = ActionType.ALL_IN;
            }
            if (type == null) {
                continue;
            }

            actions.add(new Action(player, street, type, parseMoney(line), allIn));
        }

        return actions;
    }

    private ActionType parseActionType(String line) {
        String lower = line.toLowerCase();
        if (lower.contains("folds")) return ActionType.FOLD;
        if (lower.contains("calls")) return ActionType.CALL;
        if (lower.contains("raises")) return ActionType.RAISE;
        if (lower.contains("bets")) return ActionType.BET;
        if (lower.contains("checks")) return ActionType.CHECK;
        return null;
    }

    private boolean isAllIn(String line) {
        String lower = line.toLowerCase();
        return lower.contains("all-in") || lower.contains("all in");
    }

    private Summary parseSummary(String[] lines, List<Player> players) {
        List<SummaryEntry> entries = new ArrayList<>();
        double pot = 0;
        double rake = 0;

        for (String line : lines) {
            String lower = line.toLowerCase();
            if (lower.contains("total pot")) {
                pot = parseMoney(line);
                int rakeAt = lower.indexOf("rake");
                if (rakeAt >= 0) {
                    rake = parseMoney(line.substring(rakeAt));
                }
            }

            Player player = playerInLine(line, players);
            if (player == null || !isWonLine(line)) {
                continue;
            }
            entries.add(new SummaryEntry(player, parseMoney(line)));
        }

        return new Summary(pot, rake, entries);
    }

    private boolean isWonLine(String line) {
        String lower = line.toLowerCase();
        return lower.contains("collected") || lower.contains("and won") || lower.contains(" won (");
    }

    private Map<String, List<Card>> parseOpponentHands(String[] lines, List<Player> players) {
        Map<String, List<Card>> map = new HashMap<>();

        for (String line : lines) {
            String lower = line.toLowerCase();
            if (!lower.contains("shows") && !lower.contains("showed")) {
                continue;
            }
            Player player = playerInLine(line, players);
            if (player == null) {
                continue;
            }
            map.put(player.name(), parseCards(extractFirstCards(line)));
        }

        return map;
    }

    private Player playerInLine(String line, List<Player> players) {
        Player match = null;
        for (Player player : players) {
            if (line.contains(player.name()) && (match == null || player.name().length() > match.name().length())) {
                match = player;
            }
        }
        return match;
    }

    private String parseHandId(String[] lines) {
        return lines.length == 0 ? "" : lines[0].trim();
    }

    private String parseTable(String[] lines) {
        return "UnknownTable";
    }

    private String parseLimit(String[] lines) {
        return "UnknownLimit";
    }

    private Player findPlayer(List<Player> players, String name) {
        return players.stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Player not found: " + name));
    }

    private Player playerStarting(String line, List<Player> players) {
        Player match = null;
        for (Player player : players) {
            if (line.startsWith(player.name()) && (match == null || player.name().length() > match.name().length())) {
                match = player;
            }
        }
        return match;
    }

    private String extractFirstCards(String line) {
        int start = line.indexOf('[');
        int end = line.indexOf(']');
        if (start < 0 || end < 0 || end <= start) {
            return "";
        }
        return line.substring(start + 1, end);
    }

    private String extractLastCards(String line) {
        int start = line.lastIndexOf('[');
        int end = line.lastIndexOf(']');
        if (start < 0 || end < 0 || end <= start) {
            return "";
        }
        return line.substring(start + 1, end);
    }

    private List<Card> parseCards(String raw) {
        raw = raw.replace("[", "")
                .replace("]", "")
                .replace(",", "")
                .replace("|", "")
                .replace(" ", "")
                .trim();

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i + 1 < raw.length(); i += 2) {
            cards.add(Card.of(Rank.fromChar(raw.charAt(i)), Suit.fromChar(raw.charAt(i + 1))));
        }
        return cards;
    }

    private double parseMoney(String text) {
        int dollar = text.indexOf('$');
        if (dollar < 0) {
            return 0;
        }
        int end = dollar + 1;
        while (end < text.length()) {
            char c = text.charAt(end);
            if (Character.isDigit(c) || c == '.') {
                end++;
            } else {
                break;
            }
        }
        String number = text.substring(dollar + 1, end);
        if (number.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(number);
    }
}
