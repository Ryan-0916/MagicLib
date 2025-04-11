package com.magicrealms.mod.entity.animal;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

/**
 * @author Ryan-0916
 * @Desc 自定义实体
 * 用于注册一个自定义实体
 * 该实体仅用于占位功能
 * 生成后会自动消亡，仅用于数据包生成。
 * @date 2025-03-20
 */
@SuppressWarnings("unused")
public class CustomEntity extends Bat {

    public CustomEntity(EntityType<? extends CustomEntity> entityType, Level level) {
        super(entityType, level);
    }

    public void tick() {
        this.remove(Entity.RemovalReason.DISCARDED);
    }

}
