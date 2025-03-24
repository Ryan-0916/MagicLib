package com.magicrealms.mod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-03-20
 */
public class MagicLibPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(final @NotNull String mixinPackage) {
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(@NotNull String targetClassName,
                                    @NotNull String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(@NotNull Set<String> myTargets,
                              @NotNull Set<String> otherTargets) {
    }

    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(@NotNull String targetClassName,
                         @NotNull ClassNode targetClass,
                         @NotNull String mixinClassName,
                         @NotNull IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(@NotNull String targetClassName,
                          @NotNull ClassNode targetClass,
                          @NotNull String mixinClassName,
                          @NotNull IMixinInfo mixinInfo) {
    }
}
