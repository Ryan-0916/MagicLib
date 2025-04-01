package com.magicrealms.mod;

import com.magicrealms.mod.entity.animal.Hedgehog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-03-20
 */
public class MagicLibPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(final String mixinPackage) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.out.println("1111111111");

        // 延迟30秒执行任务
        scheduler.schedule(() -> {
            try {
                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MagicLib")),
                        new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "magiclib hello");
                            }
                        });
                System.out.println("任务开始执行");
                // 创建 EntityType 并注册
                ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.withDefaultNamespace("hedgehog2"));
                EntityType<Hedgehog> hedgehogEntityType = Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
                        EntityType.Builder.of(Hedgehog::new, MobCategory.MISC).noLootTable().noSave()
                                .sized(0.6F, 0.6F)
                                .eyeHeight(0.4F)
                                .clientTrackingRange(10).build(key));
                System.out.println("生成成功: " + hedgehogEntityType);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 30, TimeUnit.SECONDS);
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName,
                                    String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets,
                              Set<String> otherTargets) {
    }

    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName,
                         ClassNode targetClass,
                         String mixinClassName,
                         IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName,
                          ClassNode targetClass,
                          String mixinClassName,
                          IMixinInfo mixinInfo) {
    }
}
