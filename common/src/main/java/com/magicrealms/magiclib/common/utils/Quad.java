package com.magicrealms.magiclib.common.utils;

import java.util.Objects;

/**
 * @author Ryan-0916
 * @date 2025-06-10
 */
public record Quad<A, B, C, D>(A first, B second, C third, D fourth) {

    public static <A, B, C, D> Quad<A, B, C, D> of(final A first, final B second, final C third, final D fourth) {
        return new Quad<>(first, second, third, fourth);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Quad<?, ?, ?, ?> tuple = (Quad<?, ?, ?, ?>) object;
        return Objects.equals(first, tuple.first)
                && Objects.equals(second, tuple.second)
                && Objects.equals(third, tuple.third)
                && Objects.equals(fourth, tuple.fourth);
    }


    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third, this.fourth);
    }

}
