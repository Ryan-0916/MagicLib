package com.magicrealms.mod.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 刺猬
 * 动物设计：刺猬（Hedgehog）
 * 1. 外观：
 * 体型：刺猬是小型动物，身长大约 0.5 米左右，体型较为圆润，呈球状。它们的身体覆盖着短小的尖刺，刺上有自然的花纹。
 * 颜色：它们的刺是灰色或棕色的，腹部和面部则是浅色的。眼睛较大，带有圆润的表情。
 * 尾巴：刺猬有一条小小的、毛茸茸的尾巴，通常看起来非常可爱。
 * 2. 栖息地：
 * 森林和草地：刺猬生活在 森林、草地、草丛 等环境中。它们更喜欢潮湿和温暖的地方，通常隐藏在草丛、落叶堆或者石缝中。
 * 3. 行为：
 * 安静：刺猬通常比较安静，不会主动攻击玩家，它们更喜欢在周围环境中悄悄行动。
 * 防御：当感到威胁时，刺猬会迅速蜷缩成一个小球，露出身上的尖刺来保护自己。
 * 夜行性：刺猬是夜行性动物，白天通常会藏匿在栖息地里，晚上出来觅食。
 * 4. 饮食：
 * 食物偏好：刺猬是 昆虫 的捕食者，特别喜欢吃 甲虫、蚯蚓 和其他小型无脊椎动物。偶尔它们也会吃一些植物性食物，如 浆果 或 蘑菇。
 * 5. 特殊能力：
 * 刺伤：刺猬的尖刺能够对敌对生物造成一定的伤害。如果敌人接触到刺猬时，会受到轻微的伤害（类似于触碰荆棘）。这让它们能够有效地防御其他掠食者。
 * 小球防御：刺猬可以通过快速蜷缩成小球来增加其防御力，使得它们在一段时间内对攻击有更高的免疫力。
 * 6. 互动：
 * 驯服：玩家可以使用 浆果 来驯服刺猬。驯服后的刺猬会跟随玩家，并为玩家提供一定的 防御增益。它们能够增加玩家在与敌对生物战斗时的防御力，特别是在近战时。
 * 繁殖：玩家可以喂养两只刺猬 浆果，它们会进入繁殖状态并生下小刺猬。小刺猬会随着时间的推移长大，并学会防御技能。
 * 7. 掉落物：
 * 刺猬刺：刺猬死亡后会掉落 刺猬刺，可以用来制作 刺网（用来减缓敌人移动的陷阱），或者作为武器的强化材料，增加武器的穿刺效果。
 * 刺猬皮：刺猬的皮可以用来制作 皮革装备 或 防御手套，为玩家提供额外的防御能力。
 * 8. 与玩家的互动：
 * 警觉性：当玩家靠近刺猬时，它们会表现出警觉并迅速蜷缩成小球。如果玩家对它们没有威胁，它们会保持相对平静，但如果玩家接触到它们，它们可能会进入防御模式并迅速逃跑。
 * 玩法创新：
 * 生态系统补充：刺猬的加入为游戏增添了更多生物互动，特别是生态链的完整性。它们帮助玩家清理周围环境的昆虫，也可以为玩家提供生物制品。
 * 森林保护者：刺猬可以与 兔子、狐狸 等森林中的其他动物共存，形成生态平衡，玩家可以利用这种生态关系来获取资源，维护森林的自然和谐。
 * @date 2025-03-20
 */
public class Hedgehog extends Animal {

    private static final EntityDimensions BABY_DIMENSIONS;

    public Hedgehog(EntityType<? extends Hedgehog> entityType, Level level) {
        super(entityType, level);
    }

    public void tick() {
        this.remove(RemovalReason.DISCARDED);
    }

    protected void registerGoals() {
        super.goalSelector.addGoal(0, new FloatGoal(this));
        super.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        super.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        super.goalSelector.addGoal(3, new TemptGoal(this, 1.25, this::isFood, false));
        super.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        super.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        super.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        super.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public boolean isFood(@NotNull ItemStack stack) {
        return false; // 假设您有一个HEDGEHOG_FOOD的标签
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.read("entity.cow.ambient").getOrThrow());
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.read("entity.cow.hurt").getOrThrow());
    }

    public SoundEvent getDeathSound() {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.read("entity.cow.death").getOrThrow());
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState block) {
        this.playSound(SoundEvent.createVariableRangeEvent(ResourceLocation.read("entity.cow.step").getOrThrow()), 0.15F, 1.0F); // 调整步伐声音
    }

    public float getSoundVolume() {
        return 0.2F;
    }

    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(Items.SWEET_BERRIES) && !this.isBaby()) {
            // 如果玩家使用浆果进行互动
            ItemStack itemStack = ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.SWEET_BERRIES)); // 假设您希望通过浆果给刺猬喂食
            player.setItemInHand(hand, itemStack);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Nullable
    public Hedgehog getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null;
    }

    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    static {
        BABY_DIMENSIONS = EntityType.COW.getDimensions().scale(0.5F).withEyeHeight(0.665F); // 这里是刺猬的尺寸调整
    }
}
