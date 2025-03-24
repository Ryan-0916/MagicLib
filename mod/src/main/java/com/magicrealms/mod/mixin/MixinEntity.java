package com.magicrealms.mod.mixin;
import ca.spottedleaf.moonrise.patches.chunk_system.entity.ChunkSystemEntity;
import ca.spottedleaf.moonrise.patches.chunk_system.level.chunk.ChunkData;
import ca.spottedleaf.moonrise.patches.entity_tracker.EntityTrackerEntity;
import com.magicrealms.mod.entity.animal.Bat2;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.ScoreHolder;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
* @author Ryan-0916
* @Desc 说明
* @date 2025-03-24
*/
@Mixin(Entity.class)
public abstract class MixinEntity implements SyncedDataHolder, Nameable, EntityAccess, ScoreHolder, ChunkSystemEntity, EntityTrackerEntity {

    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    public void load(CompoundTag compound, CallbackInfo cir) {
        if((Object) this instanceof Bat2) {
            System.out.println("拦截");
            cir.cancel();
        }
    }

    @Override
    public boolean moonrise$isHardColliding() {
        return false;
    }

    @Override
    public boolean moonrise$isHardCollidingUncached() {
        return ChunkSystemEntity.super.moonrise$isHardCollidingUncached();
    }

    @Override
    public FullChunkStatus moonrise$getChunkStatus() {
        return null;
    }

    @Override
    public void moonrise$setChunkStatus(FullChunkStatus fullChunkStatus) {

    }

    @Override
    public ChunkData moonrise$getChunkData() {
        return null;
    }

    @Override
    public void moonrise$setChunkData(ChunkData chunkData) {

    }

    @Override
    public int moonrise$getSectionX() {
        return 0;
    }

    @Override
    public void moonrise$setSectionX(int i) {

    }

    @Override
    public int moonrise$getSectionY() {
        return 0;
    }

    @Override
    public void moonrise$setSectionY(int i) {

    }

    @Override
    public int moonrise$getSectionZ() {
        return 0;
    }

    @Override
    public void moonrise$setSectionZ(int i) {

    }

    @Override
    public boolean moonrise$isUpdatingSectionStatus() {
        return false;
    }

    @Override
    public void moonrise$setUpdatingSectionStatus(boolean b) {

    }

    @Override
    public boolean moonrise$hasAnyPlayerPassengers() {
        return false;
    }

    @Override
    public ChunkMap.TrackedEntity moonrise$getTrackedEntity() {
        return null;
    }

    @Override
    public void moonrise$setTrackedEntity(ChunkMap.TrackedEntity trackedEntity) {

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {

    }

    @Override
    public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> list) {

    }

    @Override
    public Component getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return Nameable.super.hasCustomName();
    }

    @Override
    public Component getDisplayName() {
        return Nameable.super.getDisplayName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return Nameable.super.getCustomName();
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public BlockPos blockPosition() {
        return null;
    }

    @Override
    public AABB getBoundingBox() {
        return null;
    }

    @Override
    public void setLevelCallback(EntityInLevelCallback entityInLevelCallback) {

    }

    @Override
    public Stream<? extends EntityAccess> getSelfAndPassengers() {
        return Stream.empty();
    }

    @Override
    public Stream<? extends EntityAccess> getPassengersAndSelf() {
        return Stream.empty();
    }

    @Override
    public void setRemoved(Entity.RemovalReason removalReason) {

    }

    @Override
    public void setRemoved(Entity.RemovalReason removalReason, EntityRemoveEvent.Cause eventCause) {
        EntityAccess.super.setRemoved(removalReason, eventCause);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isAlwaysTicking() {
        return false;
    }

    @Override
    public String getScoreboardName() {
        return "";
    }

    @Override
    public Component getFeedbackDisplayName() {
        return ScoreHolder.super.getFeedbackDisplayName();
    }
}
