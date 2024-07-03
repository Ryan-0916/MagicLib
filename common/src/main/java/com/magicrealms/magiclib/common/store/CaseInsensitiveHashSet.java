package com.magicrealms.magiclib.common.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Ryan-0916
 * @Desc 不区分大小写的 Set
 * @date 2024-05-30
 */
@SuppressWarnings("unused")
public class CaseInsensitiveHashSet {
    private final HashMap<String, String> MAP;

    public CaseInsensitiveHashSet() {
        MAP = new HashMap<>();
    }

    public CaseInsensitiveHashSet(@NotNull Collection<? extends String> values) {
        MAP = new HashMap<>(Math.max((int) (values.size()/.75f) + 1, 16));
        addAll(values);
    }

    public void add(@Nullable String value) {
        if (value == null) return;
        MAP.put(value.toLowerCase(), value);
    }

    public boolean contains(@Nullable String value) {
        if (value == null) return false;
        return MAP.containsKey(value.toLowerCase());
    }

    public void addAll(@Nullable Collection<? extends String> values) {
        if (values == null) return;
        values.forEach(this::add);
    }

    public Set<String> getAll() {
        return MAP.keySet();
    }
}