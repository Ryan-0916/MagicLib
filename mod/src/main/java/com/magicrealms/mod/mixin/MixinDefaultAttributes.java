package com.magicrealms.mod.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-03-24
 */
@Mixin(DefaultAttributes.class)
public class MixinDefaultAttributes {

    @Shadow
    @Final
    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS;

    @Inject(method = "getSupplier", at = @At("HEAD"), cancellable = true)
    private static void getSupplier(EntityType<? extends LivingEntity> livingEntity,
                                    CallbackInfoReturnable<AttributeSupplier> cir) {
        if(!SUPPLIERS.containsKey(livingEntity)) {
            cir.setReturnValue(Animal.createAnimalAttributes()
                    .add(Attributes.MAX_HEALTH, 20)
                    .add(Attributes.MOVEMENT_SPEED, 1).build());
        }
    }


}
