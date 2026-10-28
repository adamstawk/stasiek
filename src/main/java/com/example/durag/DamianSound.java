package com.example.durag;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/** Zapętlony dźwięk, który podąża za Damianem. */
public class DamianSound extends MovingSoundInstance {
    private final DamianEntity damian;
    public final boolean chase;

    public DamianSound(DamianEntity damian, SoundEvent event, boolean chase) {
        super(event, SoundCategory.HOSTILE, SoundInstance.createRandom());
        this.damian = damian;
        this.chase = chase;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.5F;
        this.pitch = 1.0F;
        this.x = damian.getX();
        this.y = damian.getY();
        this.z = damian.getZ();
    }

    @Override
    public void tick() {
        if (damian.isRemoved() || !damian.isAlive() || damian.isChasing() != chase) {
            setDone();
            return;
        }
        this.x = damian.getX();
        this.y = damian.getY();
        this.z = damian.getZ();
    }
}
