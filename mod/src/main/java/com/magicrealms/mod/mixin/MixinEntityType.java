package com.magicrealms.mod.mixin;

import com.magicrealms.mod.entity.animal.Hedgehog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-03-20
 */
@Mixin(EntityType.class)
@SuppressWarnings("unused")
public abstract class MixinEntityType <T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T> {


    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void entityRegister(CallbackInfo ci) {
        register("hedgehog", EntityType.Builder.of(Hedgehog::new, MobCategory.MISC).noLootTable().noSave().noSummon()
                .sized(0.6F, 0.6F)
                .eyeHeight(0.4F)
                .clientTrackingRange(10));
    }

    @Shadow
    private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
        return null;
    }

}
