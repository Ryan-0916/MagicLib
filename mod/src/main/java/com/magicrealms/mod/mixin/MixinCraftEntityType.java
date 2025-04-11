package com.magicrealms.mod.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-11
 */
@Mixin(CraftEntityType.class)
public abstract class MixinCraftEntityType {

    @Inject(
            method = "minecraftToBukkit",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void fixCustomEntityType(EntityType<?> minecraft, CallbackInfoReturnable<org.bukkit.entity.EntityType> cir) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(minecraft);
        if (!key.getNamespace().equals("magiclib")) {
            return;
        }
        cir.setReturnValue(org.bukkit.entity.EntityType.BAT);
    }
}

