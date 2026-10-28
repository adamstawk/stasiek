package com.example.durag;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    // ArmorItem.Type.HELMET = zakłada się na głowę (prawy klik lub slot hełmu)
    public static final Item DURAG = Registry.register(
            Registries.ITEM,
            Identifier.of(DuragMod.MOD_ID, "durag"),
            new ArmorItem(ModArmorMaterials.DURAG, ArmorItem.Type.HELMET,
                    new Item.Settings().maxCount(1))
    );

    public static final Item DAMIAN_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            Identifier.of(DuragMod.MOD_ID, "damian_spawn_egg"),
            new SpawnEggItem(ModEntities.DAMIAN, 0x141418, 0x8D5524, new Item.Settings())
    );

    public static final Item KAME_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            Identifier.of(DuragMod.MOD_ID, "kame_npc_spawn_egg"),
            new SpawnEggItem(ModEntities.KAME_NPC, 0xF07814, 0x1E46AA, new Item.Settings())
    );

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register(entries -> entries.add(DURAG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register(entries -> { entries.add(DAMIAN_SPAWN_EGG); entries.add(KAME_SPAWN_EGG); });
    }
}
