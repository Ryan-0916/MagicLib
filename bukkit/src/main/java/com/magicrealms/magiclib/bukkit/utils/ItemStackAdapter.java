package com.magicrealms.magiclib.bukkit.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;


public class ItemStackAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(ItemUtil.serializerUnClone(value).orElse(null));
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            return null;
        }
        return ItemUtil.deserializer(in.nextString()).orElse(null);
    }
}
