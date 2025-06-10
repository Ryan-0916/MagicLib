package com.magicrealms.magiclib.common.utils;

import java.util.Objects;

/**
 * @author Ryan-0916
 * @date 2025-06-10
 */
@SuppressWarnings("unused")
public record Pair<A, B>(A first, B second) {

    public static <A, B> Pair<A, B> of(final A first, final B second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
