package com.example.durag;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<DamianEntity> DAMIAN = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DuragMod.MOD_ID, "damian"),
            EntityType.Builder.create(DamianEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6F, 1.8F)
                    .maxTrackingRange(10)
                    .build("damian")
    );

    public static final EntityType<KameEntity> KAME_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(DuragMod.MOD_ID, "kame_npc"),
            EntityType.Builder.create(KameEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6F, 1.8F)
                    .maxTrackingRange(10)
                    .build("kame_npc")
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(KAME_NPC, KameEntity.createKameAttributes());
        FabricDefaultAttributeRegistry.register(DAMIAN, DamianEntity.createDamianAttributes());
    }
}
