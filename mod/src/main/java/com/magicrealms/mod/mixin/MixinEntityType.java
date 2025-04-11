package com.magicrealms.mod.mixin;

import com.magicrealms.mod.entity.animal.CustomEntity;
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

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void entityRegister(CallbackInfo ci) {
        /* 注册自定义生物 */
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("magiclib", "custom");
        ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(Registries.ENTITY_TYPE, location);
        EntityType<CustomEntity> hedgehogEntityType = EntityType.Builder
                .of(CustomEntity::new, MobCategory.MISC)
                .noLootTable()
                .build(resourceKey);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, location, hedgehogEntityType);
    }

}
