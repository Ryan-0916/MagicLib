package com.magicrealms.mod.mixin;

import com.magicrealms.mod.entity.animal.Bat2;
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
        System.out.println("尝试注册刺猬");
        EntityType<Bat2> hedgehogEntityType = register("hedgehog",
                EntityType.Builder.of(Bat2::new, MobCategory.MISC).noLootTable().noSave()
                .sized(0.6F, 0.6F)
                .eyeHeight(0.4F)
                .clientTrackingRange(10)
        );
        System.out.println("注册刺猬成功！");
        System.out.println(hedgehogEntityType);
        assert hedgehogEntityType != null;
        System.out.println("是否可以Summon" + hedgehogEntityType.canSummon());
    }

    @Shadow
    private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
        return null;
    }

}
