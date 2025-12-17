/*
package net.daveyx0.primitivemobs.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//I took this from a dead mod

@SideOnly(Side.CLIENT)
public class FireSpiralParticle extends Particle {

    private float initialScale;
    private double centerX;
    private double centerZ;
    private int angleIndex;
    private double radius = 1.0D;

    public FireSpiralParticle(World world, double x, double y, double z,
                              double speedX, double speedY, double speedZ,
                              double centerX, double centerZ, int startingAngle) {
        super(world, x, y, z, speedX, speedY, speedZ);

        this.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
        this.motionY += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
        this.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.05F;

        this.initialScale = this.particleScale + 0.5F;

        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;

        int fps = Minecraft.getDebugFPS();
        if (fps < 25) this.particleMaxAge = 25;
        else if (fps < 45) this.particleMaxAge = 50;
        else this.particleMaxAge = 100;

        this.setParticleTextureIndex(48);

        this.centerX = centerX;
        this.centerZ = centerZ;
        this.angleIndex = startingAngle;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        double step = Math.toRadians(8); // 0.13962634D

        double newX = Math.cos(angleIndex * step) * radius + centerX;
        double newZ = Math.sin(angleIndex * step) * radius + centerZ;

        angleIndex++;

        this.setPosition(newX, posY - 0.05D, newZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        float t = MathHelper.clamp(
            ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge,
            0.0F, 1.0F
        );

        int base = super.getBrightnessForRender(partialTicks);
        int blockLight = base & 255;
        int skyLight = (base >> 16) & 255;

        blockLight += (int)(t * 15.0F * 16.0F);
        if (blockLight > 240) blockLight = 240;

        return blockLight | (skyLight << 16);
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        @Override
        public Particle createParticle(int id, World world, double x, double y, double z,
                                       double speedX, double speedY, double speedZ,
                                       int... args) {
            return new FireSpiralParticle(world, x, y, z,
                speedX, speedY, speedZ,
                0.0D, 0.0D, 0
            );
        }
    }
}
*/
