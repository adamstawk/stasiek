package com.example.durag;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class DamianRenderer extends BipedEntityRenderer<DamianEntity, PlayerEntityModel<DamianEntity>> {
    private static final Identifier TEXTURE = Identifier.of(DuragMod.MOD_ID, "textures/entity/damian.png");

    public DamianRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5F);
        // dzięki temu durag w slocie głowy widać na modelu
        addFeature(new ArmorFeatureRenderer<>(
                this,
                new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()
        ));
    }

    @Override
    public Identifier getTexture(DamianEntity entity) {
        return TEXTURE;
    }
}
