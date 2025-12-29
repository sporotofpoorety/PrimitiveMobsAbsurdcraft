package net.daveyx0.primitivemobs.entity.item;

import net.daveyx0.multimob.client.particle.MMParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFlameSpit extends EntitySmallFireball {

    protected final int totalLifetime;
    protected final int particleFactor;

	public EntityFlameSpit(World worldIn){
		super(worldIn);

        totalLifetime = 200;
        particleFactor = 10;
	}
	
	public EntityFlameSpit(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, 
    int lifetime, int particles) {
		super(worldIn, shooter, accelX, accelY, accelZ);

        totalLifetime = lifetime;
        particleFactor = particles;	
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

}
