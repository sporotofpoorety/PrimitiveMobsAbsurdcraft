package net.daveyx0.primitivemobs.entity.ai;


import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveCreeper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.SoundCategory;

public class EntityAICreeperSwellSpecial extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityPrimitiveCreeper swellingCreeper;

    /**
     * The creeper's attack target. This is used for the changing of the creeper's state.
     */
    EntityLivingBase creeperAttackTarget;

    public EntityAICreeperSwellSpecial(EntityPrimitiveCreeper entityprimitivecreeperIn)
    {
        this.swellingCreeper = entityprimitivecreeperIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        //First, needs cooldown to be expired
        //and needs to not already be executing normal swell
        if(this.swellingCreeper.getCreeperSpecialCooldown() <= 0 && this.swellingCreeper.getCreeperState() < 1 && this.swellingCreeper.creeperSpecialConditions() == true)
        {
            EntityLivingBase entitylivingbase = this.swellingCreeper.getAttackTarget();
            //Executes if is already swelling or can see target
            return (this.swellingCreeper.getCreeperStateSpecial() > 0) || (entitylivingbase != null && this.swellingCreeper.canEntityBeSeen(entitylivingbase));
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
        //Also, first cancel regular swelling
        this.swellingCreeper.resetCreeperSpecial();
        this.swellingCreeper.setIgnitedTime(0);
//      this.swellingCreeper.playSound(PrimitiveMobsSoundEvents.ENTITY_CREEPER_NUKE, 3.0F, 1.0F);
        this.swellingCreeper.world.playSound(null, this.swellingCreeper.posX, this.swellingCreeper.posY, this.swellingCreeper.posZ,
                                            PrimitiveMobsSoundEvents.ENTITY_CREEPER_NUKE, SoundCategory.NEUTRAL, 5.0F, 1.0F);
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
            this.swellingCreeper.setCreeperStateSpecial(-1);
//If interrupt by losing target will give up swelling after enough times
            this.swellingCreeper.setCreeperSpecialInterrupted(this.swellingCreeper.getCreeperSpecialInterrupted() + 1);
        }
//Else keep swelling
        else
        {
            this.swellingCreeper.setCreeperStateSpecial(1);
        }
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
//idk why this is even here in the original task
//      this.creeperAttackTarget = null;

//When swelling interrupted undo it
        this.swellingCreeper.resetCreeperSpecial();
//      this.swellingCreeper.setCreeperStateSpecial(-1);

//If interrupted enough times
        if(this.swellingCreeper.getCreeperSpecialInterrupted() >= this.swellingCreeper.getCreeperSpecialInterruptedMax())
        {
//Give up and reactivate cooldown
            this.swellingCreeper.setCreeperSpecialCooldown(this.swellingCreeper.getCreeperSpecialCooldownFrustrated());
            this.swellingCreeper.setCreeperSpecialInterrupted(0);
            this.swellingCreeper.playSound(PrimitiveMobsSoundEvents.ENTITY_CREEPER_ANNOYED, 3.0F, 1.0F);
        }
//For regular interruptions has smaller cooldown
        else
        {
            this.swellingCreeper.setCreeperSpecialCooldown(this.swellingCreeper.getCreeperSpecialCooldownInterrupted());            
        }
    }
}
