package com.magicrealms.mod.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-03-24
 */
@Mixin(MappedRegistry.class)
public abstract class MixinMappedRegistry<T> {
    @Shadow
    @Nullable
    private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    @Redirect(
            method = "freeze()Lnet/minecraft/core/Registry;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/core/MappedRegistry;unregisteredIntrusiveHolders:Ljava/util/Map;",
                    opcode = 181
            )
    )
    private void skipUnregisteredIntrusiveHoldersAssignment(MappedRegistry<?> instance, Map<T, Holder.Reference<T>> value) {
        if (instance == BuiltInRegistries.ENTITY_TYPE) {
            System.out.println("skipUnregisteredIntrusiveHoldersAssignment");
        }else {
            this.unregisteredIntrusiveHolders = value;
        }
    }

    @Inject(
            method = "validateWrite(Lnet/minecraft/resources/ResourceKey;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipValidateWriteKey(CallbackInfo ci) {
        if ((Object)this == BuiltInRegistries.ENTITY_TYPE) {
            ci.cancel();
        }
    }

    @Inject(
            method = "validateWrite()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipValidateWrite(CallbackInfo ci) {
        if ((Object) this == BuiltInRegistries.ENTITY_TYPE) {
            ci.cancel();
        }
    }
}
