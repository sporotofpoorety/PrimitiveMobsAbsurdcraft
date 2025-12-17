package net.daveyx0.primitivemobs.entity.ai;


import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;




//Other than the capabilities 
//conundrum, this went quite smoothly
public class EntityAIBuffOwnerMultimob
<EntityOwned extends EntityLivingBase>
extends EntityAIBase
{
    EntityOwned petEntity;
    Potion petBuff;
    int petBuffStrength;
    double petBuffDistance;
    ITameableEntity tameableCapability;

    EntityLivingBase ownerEntity;
    int buffCooldown;    
	
	public EntityAIBuffOwnerMultimob(EntityOwned pet, Potion buff, int strength, double distance, ITameableEntity capability) 
    {
		petEntity = pet;
        petBuff = buff;
        petBuffStrength = strength;
        petBuffDistance = distance;
        tameableCapability = capability;

        ownerEntity = tameableCapability.getOwner(petEntity);
        buffCooldown = 10; 
	}

	/**
	* Returns whether the EntityAIBase should begin execution.
	*/
	public boolean shouldExecute()
	{
//Refresh owner state
        ownerEntity = tameableCapability.getOwner(petEntity);
//Check if tamed and has owner
		if(tameableCapability.isTamed() && ownerEntity != null)
        {
            return true;            
        }
        return false;
	}
	
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {

    }

	/**
    * Returns whether an in-progress EntityAIBase should continue executing
	*/
	public boolean continueExecuting()
    {
        return shouldExecute();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
//Uninterrupt upkeep while saving computation
        if(--buffCooldown <= 0) 
        {
//If the owner is in range
        	if(petEntity.getDistanceSq(ownerEntity) <= Math.pow(petBuffDistance, 2))
//Apply with specified strength
        	{
                ownerEntity.addPotionEffect(new PotionEffect(petBuff, 25, petBuffStrength));
        	}
            buffCooldown = 10;
        }
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {

    }
	
}
