package net.daveyx0.primitivemobs.entity;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import net.daveyx0.primitivemobs.client.particles.ParticleFireSpiral;

//I TOOK THIS VISUAL EFFECT FROM A DEAD MOD CALLED SWITCHBOW


public class EntityFireSpiral extends Entity 
{

    private int lifetimeTicks = 100;
    private double damageRadius;
    private double visualRadius;
    private double riseSpeed;
    private int textureIndex;

    public EntityFireSpiral(World world) 
    {
        super(world);
        setSize(0.0F, 0.0F);
    }

    public EntityFireSpiral(World world, double x, double y, double z, 
    double damageRadius, double visualRadius, double riseSpeed, int textureIndex) 
    {
        this(world);
        setPosition(x, y, z);
        this.damageRadius = damageRadius;
        this.visualRadius = visualRadius;
        this.riseSpeed = riseSpeed;
        this.textureIndex = textureIndex;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if (--lifetimeTicks <= 0) 
        {
            setDead();
            return;
        }

//Server side set entities on fire i will probably remove
/*
        if (!world.isRemote) 
        {
            List<EntityLivingBase> mobs =
            world.getEntitiesWithinAABB
            (
                EntityLivingBase.class,
                getEntityBoundingBox().grow(damageRadius, 1.0, damageRadius)
            );

            for (EntityLivingBase mob : mobs) 
            {
                mob.setFire(10);
            }
            return;
        }
*/

//Client side
        for (int angle = 0; angle < 45; angle += 5) 
        {
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleFireSpiral(world,
            posX, posY, posZ, posX, posZ, textureIndex, angle, 4.0D, 0.15D));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) 
    {
        lifetimeTicks = tag.getInteger("Lifetime");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) 
    {
        tag.setInteger("Lifetime", lifetimeTicks);
    }
}
