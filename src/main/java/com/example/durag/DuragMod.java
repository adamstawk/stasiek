package com.example.durag;

import net.fabricmc.api.ModInitializer;

public class DuragMod implements ModInitializer {
    public static final String MOD_ID = "durag";

    @Override
    public void onInitialize() {
        ModArmorMaterials.register();
        ModSounds.register();
        ModEntities.register();
        ModItems.register();
    }
}
