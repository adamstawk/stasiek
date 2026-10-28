package com.example.durag;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    /**
     * Materiał "durag": czysto kosmetyczny (0 ochrony).
     * Tekstura na postaci: assets/durag/textures/models/armor/durag_layer_1.png
     */
    public static final RegistryEntry<ArmorMaterial> DURAG = Registry.registerReference(
            Registries.ARMOR_MATERIAL,
            Identifier.of(DuragMod.MOD_ID, "durag"),
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 0);
                        map.put(ArmorItem.Type.LEGGINGS, 0);
                        map.put(ArmorItem.Type.CHESTPLATE, 0);
                        map.put(ArmorItem.Type.HELMET, 0);
                        map.put(ArmorItem.Type.BODY, 0);
                    }),
                    15,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(Items.BLACK_WOOL),
                    List.of(new ArmorMaterial.Layer(Identifier.of(DuragMod.MOD_ID, "durag"))),
                    0.0F,
                    0.0F
            )
    );

    public static void register() {
        // wywołanie wymusza załadowanie klasy (i rejestrację materiału)
    }
}
