package com.example.durag;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    /** "Halo to ja, Twój telefon" – normalny dźwięk Damiana. */
    public static final SoundEvent PHONE = register("damian.phone");
    /** "Tiki Tiki" – gdy Damian goni gracza. */
    public static final SoundEvent CHASE = register("damian.chase");

    /** Kamehameha - odtwarzany, gdy ktoś uderzy Kame NPC. */
    public static final SoundEvent KAMEHAMEHA = register("kamehameha");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(DuragMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        // wywołanie wymusza załadowanie klasy
    }
}
