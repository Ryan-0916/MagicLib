package com.magicrealms.mod.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;

/**
* @author Ryan-0916
* @Desc 说明
* @date 2025-03-24
*/
public class Bat2 extends Bat {
    public Bat2(EntityType<? extends Bat2> entityType, Level level) {
        super(entityType, level);
    }

    public void tick() {
        this.remove(RemovalReason.DISCARDED);
        System.out.println("炼奶是傻批");
    }
}
