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
            cir.setReturnValue(this.bukkitEntity);
        }
    }

    // private EntityDamageEvent handleEntityDamage(final DamageSource damagesource, float amount, float invulnerabilityRelatedLastDamage)
    // hurtServer

//    private EntityDamageEvent handleEntityDamage(final DamageSource damagesource, float amount, float invulnerabilityRelatedLastDamage) {
//        // 保存原始的伤害数值
//        float originalDamage = amount;
//
//        // 免疫减少公式，返回一个基于当前伤害和上次免疫相关伤害的减少值
//        com.google.common.base.Function<Double, Double> invulnerabilityReductionEquation = (d) -> {
//            if (invulnerabilityRelatedLastDamage == 0.0F) {
//                return 0.0;  // 如果免疫相关的最后伤害为零，则没有免疫减少
//            } else {
//                return d.floatValue() < invulnerabilityRelatedLastDamage ? 0.0 : (double)(-invulnerabilityRelatedLastDamage);
//                // 否则，如果伤害小于上次免疫相关伤害，则免疫减少为零，否则按上次免疫相关伤害减少
//            }
//        };
//
//        // 计算免疫减少后的伤害
//        float originalInvulnerabilityReduction = ((Double)invulnerabilityReductionEquation.apply((double)amount)).floatValue();
//        amount += originalInvulnerabilityReduction;
//
//        // 冻结效果的计算，如果伤害源是冻结并且目标是可以被额外伤害的类型，增加额外伤害
//        com.google.common.base.Function<Double, Double> freezing = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return damagesource.is(DamageTypeTags.IS_FREEZING) && LivingEntity.this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)
//                        ? -(f - f * 5.0)  // 如果是冻结伤害，增加伤害
//                        : -0.0;  // 否则，不做修改
//            }
//        };
//        float freezingModifier = ((Double)freezing.apply((double)amount)).floatValue();
//        amount += freezingModifier;
//
//        // 硬帽效果的计算，如果伤害源属于伤害头盔并且头部装备不为空，则减少伤害
//        com.google.common.base.Function<Double, Double> hardHat = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return damagesource.is(DamageTypeTags.DAMAGES_HELMET) && !LivingEntity.this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
//                        ? -(f - f * 0.75)  // 如果穿戴了头盔，减少25%的伤害
//                        : -0.0;  // 否则，不做修改
//            }
//        };
//        float hardHatModifier = ((Double)hardHat.apply((double)amount)).floatValue();
//        amount += hardHatModifier;
//
//        // 格挡效果的计算，如果玩家正在格挡，则减少伤害
//        com.google.common.base.Function<Double, Double> blocking = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return -(LivingEntity.this.isDamageSourceBlocked(damagesource) ? f : 0.0);  // 如果正在格挡，伤害为0
//            }
//        };
//        float blockingModifier = ((Double)blocking.apply((double)amount)).floatValue();
//        amount += blockingModifier;
//
//        // 护甲效果的计算，减少的伤害量由护甲吸收
//        com.google.common.base.Function<Double, Double> armor = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return -(f - (double)LivingEntity.this.getDamageAfterArmorAbsorb(damagesource, f.floatValue()));  // 根据护甲吸收计算伤害
//            }
//        };
//        float armorModifier = ((Double)armor.apply((double)amount)).floatValue();
//        amount += armorModifier;
//
//        // 抗性效果的计算，如果伤害源不是忽略抗性且目标有抗性效果，则减少伤害
//        com.google.common.base.Function<Double, Double> resistance = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                if (!damagesource.is(DamageTypeTags.BYPASSES_EFFECTS) && LivingEntity.this.hasEffect(MobEffects.DAMAGE_RESISTANCE)
//                        && !damagesource.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
//                    int i = (LivingEntity.this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
//                    int j = 25 - i;
//                    float f1 = f.floatValue() * (float)j;
//                    return -(f - (double)Math.max(f1 / 25.0F, 0.0F));  // 按照抗性减少伤害
//                } else {
//                    return -0.0;  // 如果没有抗性效果，则不做修改
//                }
//            }
//        };
//        float resistanceModifier = ((Double)resistance.apply((double)amount)).floatValue();
//        amount += resistanceModifier;
//
//        // 魔法吸收效果的计算，如果伤害源是可以被魔法吸收的，则减少伤害
//        com.google.common.base.Function<Double, Double> magic = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return -(f - (double)LivingEntity.this.getDamageAfterMagicAbsorb(damagesource, f.floatValue()));  // 根据魔法吸收减少伤害
//            }
//        };
//        float magicModifier = ((Double)magic.apply((double)amount)).floatValue();
//        amount += magicModifier;
//
//        // 吸收效果的计算，根据目标的吸收量减少伤害
//        com.google.common.base.Function<Double, Double> absorption = new com.google.common.base.Function<Double, Double>() {
//            public Double apply(Double f) {
//                return -Math.max(f - Math.max(f - (double)LivingEntity.this.getAbsorptionAmount(), 0.0), 0.0);  // 根据吸收量减少伤害
//            }
//        };
//        float absorptionModifier = ((Double)absorption.apply((double)amount)).floatValue();
//
//        // 最终通过CraftEventFactory生成事件，并将各项伤害修改传入事件中进行处理
//        return CraftEventFactory.handleLivingEntityDamageEvent(this, damagesource, (double)originalDamage, (double)freezingModifier,
//                (double)hardHatModifier, (double)blockingModifier, (double)armorModifier, (double)resistanceModifier, (double)magicModifier,
//                (double)absorptionModifier, freezing, hardHat, blocking, armor, resistance, magic, absorption,
//                (damageModifierDoubleMap, damageModifierFunctionMap) -> {
//                    // 设置免疫减少的计算公式和原始免疫减少
//                    damageModifierFunctionMap.put(DamageModifier.INVULNERABILITY_REDUCTION, invulnerabilityReductionEquation);
//                    damageModifierDoubleMap.put(DamageModifier.INVULNERABILITY_REDUCTION, (double)originalInvulnerabilityReduction);
//                });
//    }
//
//    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
//        // 检查是否对当前伤害源免疫，如果免疫，则不受伤害
//        if (this.isInvulnerableTo(level, damageSource)) {
//            return false;
//        } else if (!this.isRemoved() && !this.dead && !(this.getHealth() <= 0.0F)) {
//            // 如果实体没有被移除、没有死亡并且生命值大于0，继续处理伤害
//            if (damageSource.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
//                // 如果伤害来源是火，并且实体有火焰抗性效果，则不受伤害
//                return false;
//            } else {
//                // 如果实体在睡眠状态，停止睡觉
//                if (this.isSleeping()) {
//                    this.stopSleeping();
//                }
//
//                // 重置 noActionTime
//                this.noActionTime = 0;
//
//                // 如果伤害值小于0，设置为0
//                if (amount < 0.0F) {
//                    amount = 0.0F;
//                }
//
//                // 处理伤害相关变量
//                float f = amount;
//                float originalAmount = amount;
//                boolean flag = amount > 0.0F && this.isDamageSourceBlocked(damageSource);
//                float f1 = 0.0F;
//                this.walkAnimation.setSpeed(1.5F);
//
//                // 如果伤害值是非法的（NaN 或 无限大），将其设置为最大值
//                if (Float.isNaN(amount) || Float.isInfinite(amount)) {
//                    amount = Float.MAX_VALUE;
//                }
//
//                boolean flag1 = true;
//                EntityDamageEvent event;
//
//                // 检查是否在免伤冷却中，且当前伤害是否超过了冷却限制
//                if ((float)super.invulnerableTime > (float)this.invulnerableDuration / 2.0F && !damageSource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
//                    // 如果伤害不大于上次受伤值，跳过处理
//                    if (amount <= this.lastHurt) {
//                        return false;
//                    }
//
//                    // 处理伤害事件
//                    event = this.handleEntityDamage(damageSource, amount, this.lastHurt);
//                    amount = this.computeAmountFromEntityDamageEvent(event);
//
//                    // 计算最终伤害，并应用伤害
//                    if (!this.actuallyHurt(level, damageSource, (float)event.getFinalDamage(), event)) {
//                        return false;
//                    }
//
//                    // 如果伤害为零且原始伤害也是零，则不处理
//                    if (this instanceof ServerPlayer && event.getDamage() == 0.0 && originalAmount == 0.0F) {
//                        return false;
//                    }
//
//                    // 更新最后受伤时间
//                    this.lastHurt = amount;
//                    flag1 = false;
//                } else {
//                    // 如果不在免伤冷却中，处理伤害
//                    event = this.handleEntityDamage(damageSource, amount, 0.0F);
//                    amount = this.computeAmountFromEntityDamageEvent(event);
//                    if (!this.actuallyHurt(level, damageSource, (float)event.getFinalDamage(), event)) {
//                        return false;
//                    }
//
//                    // 如果伤害为零且原始伤害也是零，则不处理
//                    if (this instanceof ServerPlayer && event.getDamage() == 0.0 && originalAmount == 0.0F) {
//                        return false;
//                    }
//
//                    // 更新伤害记录
//                    this.lastHurt = amount;
//                    super.invulnerableTime = this.invulnerableDuration;
//                    this.hurtDuration = 10;
//                    this.hurtTime = this.hurtDuration;
//                }
//
//                // 处理伤害源和责任方
//                this.resolveMobResponsibleForDamage(damageSource);
//                this.resolvePlayerResponsibleForDamage(damageSource);
//
//                if (flag1) {
//                    // 如果伤害有效，则广播伤害事件
//                    if (flag) {
//                        level.broadcastEntityEvent(this, (byte)29);
//                    } else {
//                        level.broadcastDamageEvent(this, damageSource);
//                    }
//
//                    // 如果伤害源没有标记为 "NO_IMPACT" 且没有被阻挡，标记实体为受伤
//                    if (!damageSource.is(DamageTypeTags.NO_IMPACT) && !flag) {
//                        this.markHurt();
//                    }
//
//                    // 如果伤害源没有标记为 "NO_KNOCKBACK"，则处理击退效果
//                    if (!damageSource.is(DamageTypeTags.NO_KNOCKBACK)) {
//                        double d = 0.0;
//                        double d1 = 0.0;
//                        Entity var15 = damageSource.getDirectEntity();
//
//                        // 如果伤害源是投射物，计算击退方向
//                        if (var15 instanceof Projectile) {
//                            Projectile projectile = (Projectile)var15;
//                            DoubleDoubleImmutablePair doubleDoubleImmutablePair = projectile.calculateHorizontalHurtKnockbackDirection(this, damageSource);
//                            d = -doubleDoubleImmutablePair.leftDouble();
//                            d1 = -doubleDoubleImmutablePair.rightDouble();
//                        } else if (damageSource.getSourcePosition() != null) {
//                            // 如果伤害源有来源位置，计算方向
//                            d = damageSource.getSourcePosition().x() - this.getX();
//                            d1 = damageSource.getSourcePosition().z() - this.getZ();
//                        }
//
//                        // 防止计算出过大的击退值
//                        if (Math.abs(d) > 200.0) {
//                            d = Math.random() - Math.random();
//                        }
//
//                        if (Math.abs(d1) > 200.0) {
//                            d1 = Math.random() - Math.random();
//                        }
//
//                        // 施加击退效果
//                        this.knockback(0.4000000059604645, d, d1, damageSource.getDirectEntity(), damageSource.getDirectEntity() == null ? io.papermc.paper.event.entity.EntityKnockbackEvent.Cause.DAMAGE : io.papermc.paper.event.entity.EntityKnockbackEvent.Cause.ENTITY_ATTACK);
//
//                        // 如果伤害源不是投射物，标记击退效果
//                        if (!flag) {
//                            this.indicateDamage(d, d1);
//                        }
//                    }
//                }
//
//                // 如果实体已经死亡或正在死亡，处理死亡逻辑
//                if (this.isDeadOrDying()) {
//                    if (!this.checkTotemDeathProtection(damageSource)) {
//                        this.silentDeath = !flag1;
//                        this.die(damageSource);
//                        this.silentDeath = false;
//                    }
//                } else if (flag1) {
//                    // 播放受伤音效
//                    this.playHurtSound(damageSource);
//                }
//
//                boolean flag2 = !flag;
//                if (flag2) {
//                    // 记录最后一次伤害信息
//                    this.lastDamageSource = damageSource;
//                    this.lastDamageStamp = this.level().getGameTime();
//
//                    // 遍历当前所有有效效果，处理每个效果的 "onMobHurt" 事件
//                    Iterator var11 = this.getActiveEffects().iterator();
//                    while(var11.hasNext()) {
//                        MobEffectInstance mobEffectInstance = (MobEffectInstance)var11.next();
//                        mobEffectInstance.onMobHurt(level, this, damageSource, amount);
//                    }
//                }
//
//                // 如果伤害来自玩家，触发相关触发器
//                ServerPlayer serverPlayerx;
//                if (this instanceof ServerPlayer) {
//                    serverPlayerx = (ServerPlayer)this;
//                    CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayerx, damageSource, f, amount, flag);
//                    if (f1 > 0.0F && f1 < 3.4028235E37F) {
//                        serverPlayerx.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
//                    }
//                }
//
//                // 如果伤害来自其他玩家，触发相应的触发器
//                Entity var19 = damageSource.getEntity();
//                if (var19 instanceof ServerPlayer) {
//                    serverPlayerx = (ServerPlayer)var19;
//                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayerx, this, damageSource, f, amount, flag);
//                }
//
//                // 返回是否成功处理了伤害
//                return flag2;
//            }
//        } else {
//            // 如果实体无效或已死亡，返回 false
//            return false;
//        }
//    }
//
//    public static float getDamageAfterAbsorb(LivingEntity entity,
//                                             float damage,
//                                             DamageSource damageSource,
//                                             float armorValue,
//                                             float armorToughness) {
//        float f3;
//        label12: {
//            // 计算基础的伤害吸收系数
//            float f = 2.0F + armorToughness / 4.0F;
//            // 计算护甲减免后的防护值
//            float f1 = Mth.clamp(armorValue - damage / f, armorValue * 0.2F, 20.0F);
//            // 计算最终的护甲效果，限制在0.2到20范围内
//            float f2 = f1 / 25.0F;
//            // 获取攻击源的武器
//            ItemStack weaponItem = damageSource.getWeaponItem();
//            if (weaponItem != null) {
//                Level var11 = entity.level();
//                if (var11 instanceof ServerLevel) {
//                    // 如果攻击源的武器存在，并且是在服务器端，调整护甲效果
//                    ServerLevel serverLevel = (ServerLevel) var11;
//                    f3 = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(serverLevel, weaponItem, entity, damageSource, f2), 0.0F, 1.0F);
//                    break label12;
//                }
//            }
//
//            // 默认情况下，护甲效果是f2
//            f3 = f2;
//        }
//
//        // 计算伤害减免后的最终伤害
//        float f4 = 1.0F - f3;
//        return damage * f4;
//    }

}
