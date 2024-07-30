package com.github.cyberedcake.hystats.mixin;

import com.github.cyberedcake.hystats.utils.ColorCode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(RenderManager.class)
public class RenderManagerMixin {

    @Inject(method = "renderDebugBoundingBox", at = @At("HEAD"), cancellable = true)
    private void onRenderDebugBoundingBox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!(entityIn instanceof EntityPlayer || entityIn instanceof EntityItem)) return;

        int color = Color.BLACK.getRGB();

        if (entityIn instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) entityIn;
            ItemStack item = entityItem.getEntityItem();

            String[] unLocalName = item.getUnlocalizedName().split("\\.");
            if (unLocalName.length == 3 && ColorCode.hasColor(unLocalName[2])) {
                color = ColorCode.getColor(unLocalName[2]).getHex();
            } else {
                if (item.getItem() == Items.iron_ingot) color = Color.WHITE.getRGB();
                else if (item.getItem() == Items.gold_ingot) color = Color.ORANGE.getRGB();
                else if (item.getItem() == Items.diamond) color = Color.CYAN.getRGB();
                else if (item.getItem() == Items.emerald) color = Color.GREEN.getRGB();
                else color = Color.GRAY.getRGB();
            }
        } else if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            color = hyStats$getPlayerNameColor(player);
        }

        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = 0.5F; // Set alpha to 50%

        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.color(red, green, blue, alpha);

        float f = entityIn.width / 2.0F;
        AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ - entityIn.posZ + z, axisalignedbb.maxX - entityIn.posX + x, axisalignedbb.maxY - entityIn.posY + y, axisalignedbb.maxZ - entityIn.posZ + z);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, (int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255));

        if (entityIn instanceof EntityLivingBase) {
            float f1 = 0.01F;
            RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(x - (double)f, y + (double)entityIn.getEyeHeight() - 0.009999999776482582, z - (double)f, x + (double)f, y + (double)entityIn.getEyeHeight() + 0.009999999776482582, z + (double)f), (int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255));
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Vec3 vec3 = entityIn.getLook(partialTicks);
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y + (double)entityIn.getEyeHeight(), z).color((int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255)).endVertex();
        worldrenderer.pos(x + vec3.xCoord * 2.0, y + (double)entityIn.getEyeHeight() + vec3.yCoord * 2.0, z + vec3.zCoord * 2.0).color((int)(red * 255), (int)(green * 255), (int)(blue * 255), (int)(alpha * 255)).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        ci.cancel(); // Prevent the original method from executing
    }


    @Unique
    private int hyStats$getPlayerNameColor(EntityPlayer player) {
        try {
            String name = player.getDisplayName().getFormattedText().substring(2);

            Pattern pattern = Pattern.compile("§[0-9A-Fa-fK-OR]");
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                char colorCode = matcher.group().charAt(1);
                return ColorCode.getColorFromCode(colorCode);
            }
            return Color.BLACK.getRGB();
        } catch (Exception exception) {
            return Color.red.getRGB();
        }
    }
}