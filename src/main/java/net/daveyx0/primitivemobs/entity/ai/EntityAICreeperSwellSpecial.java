package net.daveyx0.primitivemobs.entity.ai;

import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.entity.EntityFireSpiral;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.SoundCategory;

public class EntityAICreeperSwellSpecial extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityCreeper swellingCreeper;
    IMixinEntityCreeper swellingCreeperMixin;

    /**
     * The creeper's attack target. This is used for the changing of the creeper's state.
     */
    EntityLivingBase creeperAttackTarget;

    public EntityAICreeperSwellSpecial(EntityCreeper entityCreeperIn)
    {
        this.swellingCreeper = entityCreeperIn;
        this.swellingCreeperMixin = (IMixinEntityCreeper) this.swellingCreeper;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
//First, needs cooldown to be expired
//and needs to not already be executing normal swell
        if(this.swellingCreeperMixin.getCreeperSpecialCooldown() <= 0 
        && this.swellingCreeper.getCreeperState() < 1
        && this.swellingCreeperMixin.getCreeperIgnitedTime() <= 0 
        && this.swellingCreeperMixin.creeperSpecialConditions() == true)
        {
            EntityLivingBase entitylivingbase = this.swellingCreeper.getAttackTarget();
//Executes if is already swelling or can see target
            return (this.swellingCreeperMixin.getCreeperStateSpecial() > 0) || (entitylivingbase != null && this.swellingCreeper.canEntityBeSeen(entitylivingbase));
        }
        return false;
    }

	/**
    * Returns whether an in-progress EntityAIBase should continue executing
	*/
	public boolean continueExecuting()
    {
        return shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
//Clear current pathfinding route
        this.swellingCreeper.getNavigator().clearPath();
//Get attack target
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();

//If target null
        if(creeperAttackTarget != null)
        {
//Play sound
            this.swellingCreeperMixin.creeperSpecialAttemptSound(this.creeperAttackTarget.posX, this.creeperAttackTarget.posY, this.creeperAttackTarget.posZ);
//Do fire spiral
		    EntityFireSpiral fireSpiral = new EntityFireSpiral(this.swellingCreeper.getEntityWorld(), 
                this.swellingCreeper.posX, this.swellingCreeper.posY, this.swellingCreeper.posZ, 1.0D, 4.0D, 0.15D, 48);
		    this.swellingCreeper.getEntityWorld().spawnEntity(fireSpiral);
        }    

    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
//If target gone or no line of sight
        if (this.creeperAttackTarget == null || !this.swellingCreeper.canEntityBeSeen(this.creeperAttackTarget))
        {
//Then, starts unswelling
            this.swellingCreeperMixin.setCreeperStateSpecial(-1);
//If interrupted by losing target will give up swelling after enough times
            this.swellingCreeperMixin.setCreeperSpecialInterrupted(this.swellingCreeperMixin.getCreeperSpecialInterrupted() + 1);
        }
//Else keep swelling
        else
        {
//Should look at target 
		    this.swellingCreeper.getLookHelper().setLookPositionWithEntity(this.creeperAttackTarget, 30.0F, 30.0F);
            this.swellingCreeperMixin.setCreeperStateSpecial(1);
        }
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.swellingCreeperMixin.resetCreeperSpecial();

//If interrupted enough times
        if(this.swellingCreeperMixin.getCreeperSpecialInterrupted() >= this.swellingCreeperMixin.getCreeperSpecialInterruptedMax())
        {
//Give up and reactivate cooldown
            this.swellingCreeperMixin.setCreeperSpecialCooldown(this.swellingCreeperMixin.getCreeperSpecialCooldownFrustrated());
            this.swellingCreeperMixin.setCreeperSpecialInterrupted(0);
            //this.swellingCreeper.playSound(PrimitiveMobsSoundEvents.ENTITY_CREEPER_ANNOYED, 3.0F, 1.0F);
            this.swellingCreeper.world.playSound(null, this.swellingCreeper.posX, this.swellingCreeper.posY, this.swellingCreeper.posZ, 
            PrimitiveMobsSoundEvents.ENTITY_CREEPER_ANNOYED, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        }
//For regular interruptions has smaller cooldown
        else
        {
            this.swellingCreeperMixin.setCreeperSpecialCooldown(this.swellingCreeperMixin.getCreeperSpecialCooldownInterrupted());            
        }
    }
}
