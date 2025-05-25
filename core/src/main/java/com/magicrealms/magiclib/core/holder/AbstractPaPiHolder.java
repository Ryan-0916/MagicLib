package com.magicrealms.magiclib.core.holder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 变量
 * @date 2025-05-10
 */
@SuppressWarnings("unused")
public abstract class AbstractPaPiHolder extends PlaceholderExpansion {

    private final String IDENTIFIER;

    private final String AUTHOR;

    private final String VERSION;

    public AbstractPaPiHolder(String identifier, String author, String version) {
        this.IDENTIFIER = identifier;
        this.AUTHOR = author;
        this.VERSION = version;
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull String getAuthor() {
        return AUTHOR;
    }

    @Override
    public @NotNull String getVersion() {
        return VERSION;
    }

    protected abstract String onOffline(OfflinePlayer player, String params) throws Exception;

    protected abstract String onOnline(Player player, String params) throws Exception;

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || params.isEmpty()) { return null; }
        try {
            return onOffline(player, params);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null || params.isEmpty()) { return null; }
        try {
            return onOnline(player, params);
        } catch (Exception e) {
            return null;
        }
    }

}
