package com.magicrealms.mod.mixin;

import com.magicrealms.mod.entity.animal.Hedgehog;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
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
    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void entityRegister(CallbackInfo ci) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("migiclib", "hedgehog");
        ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(Registries.ENTITY_TYPE, location);
        EntityType<Hedgehog> hedgehogEntityType = EntityType.Builder.of(Hedgehog::new, MobCategory.MISC).noLootTable().noSave()
                .sized(0.6F, 0.6F)
                .eyeHeight(0.4F)
                .clientTrackingRange(10).build(resourceKey);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, location, hedgehogEntityType);
    }
}
