package com.example.durag;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Damian – NPC z duragiem.
 *  - z duragiem na głowie: spokojnie chodzi i co chwilę pisze "Damian grr",
 *  - po zdjęciu durag-a (prawy klik pustą ręką): goni i bije gracza,
 *  - gdy goni -> gra "Tiki Tiki", w pozostałych sytuacjach -> "Halo to ja, Twój telefon".
 * Stan "ma durag" = durag leży w slocie głowy (dzięki temu widać go na modelu).
 */
public class DamianEntity extends PathAwareEntity {
    private static final TrackedData<Boolean> CHASING =
            DataTracker.registerData(DamianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    /** Ustawiane przez klienta (DuragClient) – dzięki temu klasa nie zależy od kodu klienckiego. */
    public static Consumer<DamianEntity> CLIENT_TICK = null;

    private int grrTimer = 60;

    public DamianEntity(EntityType<? extends DamianEntity> type, World world) {
        super(type, world);
        setPersistent();
        setCustomName(Text.literal("Damian"));
        setCustomNameVisible(true);
        equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.DURAG));
        setEquipmentDropChance(EquipmentSlot.HEAD, 1.0F);
    }

    public static DefaultAttributeContainer.Builder createDamianAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHASING, false);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(2, new MeleeAttackGoal(this, 1.35, true));
        goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(7, new LookAroundGoal(this));
        // atakuje każdego gracza w zasięgu, ale tylko gdy NIE ma duraga
        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 1, true, false, e -> !hasDurag()));
    }

    public boolean hasDurag() {
        return getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.DURAG);
    }

    public boolean isChasing() {
        return dataTracker.get(CHASING);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) {
            if (CLIENT_TICK != null) {
                CLIENT_TICK.accept(this);
            }
            return;
        }

        boolean durag = hasDurag();
        if (durag && getTarget() != null) {
            setTarget(null); // w duragu jest spokojny
        }
        LivingEntity target = getTarget();
        dataTracker.set(CHASING, !durag && target != null && target.isAlive());

        if (durag && --grrTimer <= 0) {
            grrTimer = 100 + random.nextInt(200); // co 5-15 sekund
            say("Damian grr");
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);

        if (hasDurag()) {
            // zdejmowanie duraga pustą ręką
            if (held.isEmpty() && hand == Hand.MAIN_HAND) {
                if (!getWorld().isClient) {
                    ItemStack durag = getEquippedStack(EquipmentSlot.HEAD).copy();
                    equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    if (!player.getInventory().insertStack(durag)) {
                        player.dropItem(durag, false);
                    }
                    setTarget(player);
                    say("DAMIAN GRRRR!!!");
                }
                return ActionResult.success(getWorld().isClient);
            }
        } else if (held.isOf(ModItems.DURAG)) {
            // oddanie duraga uspokaja Damiana
            if (!getWorld().isClient) {
                equipStack(EquipmentSlot.HEAD, held.copyWithCount(1));
                held.decrementUnlessCreative(1, player);
                setTarget(null);
                grrTimer = 40;
            }
            return ActionResult.success(getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    /** Wiadomość na czacie dla graczy w pobliżu. */
    private void say(String message) {
        if (!(getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        Text text = Text.literal("<Damian> " + message);
        for (ServerPlayerEntity p : serverWorld.getPlayers()) {
            if (p.squaredDistanceTo(this) < 24.0 * 24.0) {
                p.sendMessage(text, false);
            }
        }
    }
}
