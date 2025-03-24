package com.magicrealms.mod.mixin;
import com.magicrealms.mod.entity.animal.Hedgehog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
* @author Ryan-0916
* @Desc 说明
* @date 2025-03-24
*/
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    private volatile CraftEntity bukkitEntity;

    @Shadow
    private Level level;

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    public void load(CompoundTag compound, CallbackInfo cir) {
        if((Object) this instanceof Hedgehog) {
            cir.cancel();
        }
    }

    @Inject(method = "getBukkitEntity", at = @At("HEAD"), cancellable = true)
    public void getBukkitEntity(CallbackInfoReturnable<CraftEntity> cir) {
        if((Object) this instanceof Hedgehog) {
            if (this.bukkitEntity == null) {
                synchronized(this) {
                    if (this.bukkitEntity == null) {
                        this.bukkitEntity = CraftEntity.getEntity(this.level.getCraftServer(),
                                EntityType.COW.create(this.level, EntitySpawnReason.SPAWN_ITEM_USE));
                    }
                }
            }
            System.out.println("拦截");
        }
    }


}
