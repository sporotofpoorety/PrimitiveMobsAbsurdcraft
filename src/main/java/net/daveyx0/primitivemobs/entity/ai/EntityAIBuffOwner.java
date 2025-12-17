package net.daveyx0.primitivemobs.entity.ai;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;




//No need for capabilities in this one lol
public class EntityAIBuffOwner
<EntityOwned extends EntityTameable>
extends EntityAIBase
{
    EntityOwned petEntity;
    Potion petBuff;
    int petBuffStrength;
    double petBuffDistance;

    EntityLivingBase ownerEntity;
    int buffCooldown;    
	
	public EntityAIBuffOwner(EntityOwned pet, Potion buff, int strength, double distance) 
    {
		petEntity = pet;
        petBuff = buff;
        petBuffStrength = strength;
        petBuffDistance = distance;

        ownerEntity = petEntity.getOwner();
        buffCooldown = 10; 
	}

	/**
	* Returns whether the EntityAIBase should begin execution.
	*/
	public boolean shouldExecute()
	{
//Refresh owner state
        ownerEntity = petEntity.getOwner();
//Check if tamed and has owner
		if(petEntity.isTamed() && ownerEntity != null)
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
