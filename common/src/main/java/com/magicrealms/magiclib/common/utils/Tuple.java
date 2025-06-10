/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.magicrealms.magiclib.common.utils;

import java.util.Objects;

/**
 * @author Ryan-0916
 * @date 2025-06-10
 */
public record Tuple<A, B, C>(A first, B second, C third) {

    public static <A, B, C> Tuple<A, B, C> of(final A first, final B second, final C third) {
        return new Tuple<>(first, second, third);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) object;
        return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second) && Objects.equals(third, tuple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}