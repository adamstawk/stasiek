package com.example.durag;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * NPC z twarzą na skinie. Spokojnie chodzi, ale gdy ktoś go uderzy -> KAMEHAMEHA:
 * gra dźwięk, ładuje kulę (CHARGE_TICKS), po czym wystrzeliwuje niebieski promień w stronę napastnika.
 */
public class KameEntity extends PathAwareEntity {
    // ---- USTAWIENIA (1 sekunda = 20 ticków) ----
    /** Ile trwa ładowanie (dopasowane do dźwięku: "KA-ME-HA-ME..." ~8 s). */
    private static final int CHARGE_TICKS = 160;
    /** Ile trwa sam promień ("...HAAAA!" ~5 s). */
    private static final int BEAM_TICKS = 100;
    /** Przerwa po strzale, zanim znów odpali kamehamehę. */
    private static final int COOLDOWN_TICKS = 100;
    private static final double BEAM_LENGTH = 28.0;
    private static final float BEAM_DAMAGE = 10.0F;   // 10 = 5 serc
    private static final double BEAM_KNOCKBACK = 2.0;

    private static final TrackedData<Boolean> CHARGING =
            DataTracker.registerData(KameEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int chargeLeft = 0;
    private int beamLeft = 0;
    private int cooldown = 0;
    private LivingEntity victim;
    private Vec3d beamDir = Vec3d.ZERO;
    private final Set<UUID> alreadyHit = new HashSet<>();

    public KameEntity(EntityType<? extends KameEntity> type, World world) {
        super(type, world);
        setPersistent();
    }

    public static DefaultAttributeContainer.Builder createKameAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHARGING, false);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(5, new WanderAroundFarGoal(this, 0.7));
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goalSelector.add(7, new LookAroundGoal(this));
    }

    /** Używane przez renderer, żeby podnieść ręce podczas ładowania/strzału. */
    public boolean isCharging() {
        return dataTracker.get(CHARGING);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean hurt = super.damage(source, amount);
        if (hurt && !getWorld().isClient && isAlive()
                && chargeLeft <= 0 && beamLeft <= 0 && cooldown <= 0
                && source.getAttacker() instanceof LivingEntity attacker && attacker != this) {
            startKamehameha(attacker);
        }
        return hurt;
    }

    private void startKamehameha(LivingEntity target) {
        victim = target;
        chargeLeft = CHARGE_TICKS;
        beamLeft = 0;
        alreadyHit.clear();
        dataTracker.set(CHARGING, true);
        getNavigation().stop();
        // dźwięk (14 s) - serwer wysyła go wszystkim graczom w pobliżu
        getWorld().playSound(null, getX(), getY(), getZ(), ModSounds.KAMEHAMEHA, SoundCategory.HOSTILE, 4.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient || !(getWorld() instanceof ServerWorld world)) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
        }

        if (chargeLeft > 0) {
            tickCharge(world);
        } else if (beamLeft > 0) {
            tickBeam(world);
        } else if (isCharging()) {
            dataTracker.set(CHARGING, false);
        }
    }

    private Vec3d handPos() {
        Vec3d look = Vec3d.fromPolar(0, getHeadYaw());  // kierunek poziomy, w który patrzy
        return getPos().add(0, getStandingEyeHeight() - 0.35, 0).add(look.multiply(0.9));
    }

    private void freeze() {
        getNavigation().stop();
        setVelocity(0, Math.min(0, getVelocity().y), 0);
    }

    private void tickCharge(ServerWorld world) {
        freeze();
        if (victim != null && victim.isAlive()) {
            lookAtEntity(victim, 360.0F, 360.0F);
            getLookControl().lookAt(victim, 360.0F, 360.0F);
            setBodyYaw(getHeadYaw());
        }
        chargeLeft--;

        // kula energii rośnie w rękach
        float progress = 1.0F - chargeLeft / (float) CHARGE_TICKS;
        Vec3d hand = handPos();
        int count = 2 + (int) (progress * 10);
        double spread = 0.15 + progress * 0.35;
        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, hand.x, hand.y, hand.z, count, spread, spread, spread, 0.01);
        world.spawnParticles(ParticleTypes.END_ROD, hand.x, hand.y, hand.z, 1 + (int) (progress * 4), spread * 0.6, spread * 0.6, spread * 0.6, 0.0);
        if (chargeLeft % 10 == 0) {
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, hand.x, hand.y, hand.z, 6, 1.0, 1.0, 1.0, 0.1);
        }

        if (chargeLeft <= 0) {
            fire(world);
        }
    }

    private void fire(ServerWorld world) {
        Vec3d from = handPos();
        if (victim != null && victim.isAlive()) {
            beamDir = victim.getPos().add(0, victim.getHeight() * 0.5, 0).subtract(from).normalize();
        } else {
            beamDir = getRotationVec(1.0F).normalize();
        }
        beamLeft = BEAM_TICKS;
        say("KAMEHAMEHAAAAA!!!");
    }

    private void tickBeam(ServerWorld world) {
        freeze();
        beamLeft--;

        Vec3d start = handPos();
        Vec3d wantedEnd = start.add(beamDir.multiply(BEAM_LENGTH));
        // promień zatrzymuje się na blokach
        Vec3d end = world.raycast(new RaycastContext(start, wantedEnd,
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getPos();
        double length = start.distanceTo(end);

        for (double d = 0; d <= length; d += 0.5) {
            Vec3d p = start.add(beamDir.multiply(d));
            world.spawnParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 2, 0.15, 0.15, 0.15, 0.0);
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, p.x, p.y, p.z, 3, 0.4, 0.4, 0.4, 0.0);
        }
        // wybuch na końcu promienia
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, end.x, end.y, end.z, 12, 0.8, 0.8, 0.8, 0.3);
        world.spawnParticles(ParticleTypes.EXPLOSION, end.x, end.y, end.z, 1, 0.3, 0.3, 0.3, 0.0);

        // obrażenia - każdy trafiony raz na strzał
        for (double d = 0; d <= length; d += 1.0) {
            Vec3d p = start.add(beamDir.multiply(d));
            Box box = new Box(p, p).expand(1.3);
            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box, x -> x != this && x.isAlive())) {
                if (alreadyHit.add(e.getUuid())) {
                    e.damage(getDamageSources().mobAttack(this), BEAM_DAMAGE);
                    e.takeKnockback(BEAM_KNOCKBACK, -beamDir.x, -beamDir.z);
                    e.setVelocity(e.getVelocity().add(0, 0.5, 0));
                    e.velocityModified = true;
                }
            }
        }

        if (beamLeft <= 0) {
            cooldown = COOLDOWN_TICKS;
            victim = null;
            dataTracker.set(CHARGING, false);
        }
    }

    private void say(String message) {
        if (!(getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }
        Text text = Text.literal("<Stasiek> " + message);
        for (ServerPlayerEntity p : serverWorld.getPlayers()) {
            if (p.squaredDistanceTo(this) < 32.0 * 32.0) {
                p.sendMessage(text, false);
            }
        }
    }
}
