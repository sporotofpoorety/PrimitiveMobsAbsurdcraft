package net.daveyx0.primitivemobs.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;

import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;


public class EntityAIStun extends EntityAIBase
{
    EntityMob stunnedMob;
    IMixinEntityMob stunnedMobMixin;

    public EntityAIStun(EntityMob entityMobIn)
    {
//Should stop mostly everything 
//except target tasks and tasks that run just by having
//a target and independent of mutexbits, for that i mixined into setAttackTarget()
        this.setMutexBits(7);

        this.stunnedMob = entityMobIn;
        this.stunnedMobMixin = (IMixinEntityMob) entityMobIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return (stunnedMobMixin.getAbsurdcraftStunned());
    }

	/**
    * Returns whether an in-progress EntityAIBase should continue executing
	*/
	public boolean continueExecuting()
    {
        return (stunnedMobMixin.getAbsurdcraftStunned());
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
//Stop moving
        this.stunnedMob.getNavigator().clearPath();
//Clear target
        this.stunnedMob.setAttackTarget(null);
        this.stunnedMob.setRevengeTarget(null);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {

    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {

    }
}

