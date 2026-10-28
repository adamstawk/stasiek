package com.example.durag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;

import java.util.Map;
import java.util.WeakHashMap;

public class DuragClient implements ClientModInitializer {
    private static final Map<DamianEntity, DamianSound> SOUNDS = new WeakHashMap<>();

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.DAMIAN, DamianRenderer::new);
        EntityRendererRegistry.register(ModEntities.KAME_NPC, KameRenderer::new);
        DamianEntity.CLIENT_TICK = DuragClient::tickSound;
    }

    /** Spokojny / bez celu -> "Halo to ja, Twój telefon"; goni -> "Tiki Tiki". */
    private static void tickSound(DamianEntity damian) {
        if (damian.isRemoved() || !damian.isAlive()) {
            SOUNDS.remove(damian);
            return;
        }
        boolean chase = damian.isChasing();
        DamianSound current = SOUNDS.get(damian);
        if (current == null || current.isDone() || current.chase != chase) {
            SoundManager manager = MinecraftClient.getInstance().getSoundManager();
            DamianSound next = new DamianSound(damian, chase ? ModSounds.CHASE : ModSounds.PHONE, chase);
            SOUNDS.put(damian, next);
            manager.play(next);
        }
    }
}
