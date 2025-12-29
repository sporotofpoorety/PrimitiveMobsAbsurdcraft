package net.daveyx0.primitivemobs.entity.item;

import net.daveyx0.multimob.client.particle.MMParticles;
import net.daveyx0.primitivemobs.entity.item.EntityFlameSpit;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;


public class EntityFlameSpitBurst extends EntitySmallFireball {

    public EntityLivingBase theShooter;

    protected final int totalLifetime;
    protected final int particleFactor;

    protected final int totalLifetimeSplits;
    protected final int particleFactorSplits;

    protected final int projectileCount;

	public EntityFlameSpitBurst(World worldIn)
    {
		super(worldIn);

        this.theShooter = null;

        this.totalLifetime = 40;
        this.particleFactor = 10;

        this.totalLifetimeSplits = 100;
        this.particleFactorSplits = 10;

        this.projectileCount = 32;
	}
	
	public EntityFlameSpitBurst(World worldIn, EntityLivingBase shooter, int totalLifetime, int particleFactor, 
    double accelX, double accelY, double accelZ, int totalLifetimeSplits, int particleFactorSplits, int projectileCount) 
    {
		super(worldIn, shooter, accelX, accelY, accelZ);

        this.theShooter = shooter;

        this.totalLifetime = totalLifetime;
        this.particleFactor = particleFactor;

        this.totalLifetimeSplits = totalLifetimeSplits;
        this.particleFactorSplits = particleFactorSplits;

        this.projectileCount = projectileCount;
	}
	
    protected EnumParticleTypes getParticleType()
    {
        return EnumParticleTypes.FLAME;
    }
	
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
    	super.onUpdate();
        if (this.world.isRemote)
        {
        	for(int i = 0; i < particleFactor; i++)
        	{
        		MMParticles.spawnParticle("flame", this.world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new float[3]);
        	}
        }
        
        if(this.ticksExisted > totalLifetime)
        {
        	this.setDead();
        }
    }

    public void setDead()
    {
        this.randomKaboom();

        super.setDead();
    }

    public void randomKaboom()
    {
        if(this.theShooter != null)
        {
            for(int times = 0; times < projectileCount; times++)
            {
                EntityFlameSpit entitysmallfireball = new EntityFlameSpit(this.world, theShooter, 
                (rand.nextFloat() * 2.0) - 1.0, (rand.nextFloat() * 2.0) - 1.0, (rand.nextFloat() * 2.0) - 1.0,
                this.totalLifetimeSplits, this.particleFactorSplits);

                entitysmallfireball.posX = this.posX;
                entitysmallfireball.posY = this.posY;
                entitysmallfireball.posZ = this.posZ;
                this.world.spawnEntity(entitysmallfireball);
            }
        }
    }

}
