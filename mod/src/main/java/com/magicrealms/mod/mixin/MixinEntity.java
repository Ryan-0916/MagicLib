package com.magicrealms.mod.mixin;
import com.magicrealms.mod.entity.animal.CustomEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
* @author Ryan-0916
* @Desc 说明
* @date 2025-03-24
*/
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    public void load(CompoundTag compound, CallbackInfo cir) {
        if((Object) this instanceof CustomEntity) {
            cir.cancel();
        }
    }
}
