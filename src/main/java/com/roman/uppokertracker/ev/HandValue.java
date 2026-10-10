package com.roman.uppokertracker.ev;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record HandValue(int encoded, HandCategory category) implements Comparable<HandValue> {

    public static HandValue of(HandCategory category, int... ranks) {

        if (ranks == null || ranks.length == 0 || ranks.length > 5) {
            throw new IllegalArgumentException("HandValue.of() requires 1–5 ranks");
        }

        // Копируем массив, чтобы не портить входные данные
        int[] r = Arrays.copyOf(ranks, ranks.length);

        // Нормализация A-low для стрита A2345
        // Если логика стрита у тебя в другом месте — можно убрать
        if (category == HandCategory.STRAIGHT && r.length == 5) {
            // A2345 → 14,5,4,3,2
            Arrays.sort(r);
            if (r[4] == 14 && r[3] == 5) {
                r[4] = 1; // Ace low
            }
        }

        // Сортировка рангов по убыванию
        Arrays.sort(r);
        for (int i = 0; i < r.length / 2; i++) {
            int tmp = r[i];
            r[i] = r[r.length - 1 - i];
            r[r.length - 1 - i] = tmp;
        }

        // Битовая упаковка
        int value = category.ordinal() << 20;
        int shift = 16;

        for (int i = 0; i < r.length; i++) {
            value |= (r[i] & 0xF) << shift;
            shift -= 4;
        }

        return new HandValue(value, category);
    }

    @Override
    public int compareTo(HandValue other) {
        return Integer.compare(this.encoded, other.encoded);
    }

    @Override
    @NotNull
    public String toString() {
        return category + "(" + encoded + ")";
    }
}
