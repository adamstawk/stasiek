package com.example.durag;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KameRenderer extends BipedEntityRenderer<KameEntity, PlayerEntityModel<KameEntity>> {
    private static final Identifier TEXTURE = Identifier.of(DuragMod.MOD_ID, "textures/entity/kame_npc.png");

    public KameRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public void render(KameEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        // podczas kamehamehy obie ręce wyciągnięte do przodu
        BipedEntityModel.ArmPose pose = entity.isCharging()
                ? BipedEntityModel.ArmPose.CROSSBOW_HOLD
                : BipedEntityModel.ArmPose.EMPTY;
        model.rightArmPose = pose;
        model.leftArmPose = pose;
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(KameEntity entity) {
        return TEXTURE;
    }
}
