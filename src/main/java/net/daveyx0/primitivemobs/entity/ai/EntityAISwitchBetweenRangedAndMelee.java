package net.daveyx0.primitivemobs.entity.ai;

import net.daveyx0.primitivemobs.entity.monster.EntitySkeletonWarrior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

public class EntityAISwitchBetweenRangedAndMelee extends EntityAIAttackMelee
{
	private final EntitySkeleton entity;
	private final double moveSpeedAmp;
	private int attackCooldown;
//Sets outer bound distances that force strafe pattern
	private final float maxAttackDistance;
    private final int useTimeNeeded;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
//Controls strafing oscillation
	private int strafingTime = -1;

	public EntityAISwitchBetweenRangedAndMelee(EntitySkeletonWarrior skeleton, double speedAmplifier, int delay, float maxDistance, int useNeeded)
	{
		super(skeleton, speedAmplifier, false);
		this.entity = skeleton;
		this.moveSpeedAmp = speedAmplifier;
		this.attackCooldown = delay;
		this.maxAttackDistance = maxDistance * maxDistance;
        this.useTimeNeeded = useNeeded;
	}

	public void setAttackCooldown(int p_189428_1_)
	{
		this.attackCooldown = p_189428_1_;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		return this.entity.getAttackTarget() != null;
	}

	protected boolean isBowInMainhand()
	{
		return this.entity.getHeldItemMainhand().getItem() instanceof ItemBow;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting()
	{
		return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand() || super.shouldContinueExecuting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		super.startExecuting();
		this.entity.setSwingingArms(true);
	}

	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		super.resetTask();
		this.entity.setSwingingArms(false);
//seeTime can be negative
		this.seeTime = 0;
		this.attackTime = -1;
		this.entity.resetActiveHand();
	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

		if (entitylivingbase != null) {
//If holding a bow
			if(this.isBowInMainhand()) {

				double targetDist = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
				boolean seesTarget = this.entity.getEntitySenses().canSee(entitylivingbase);
				boolean positiveSeeTime = this.seeTime > 0;




//If positive see time but doesn't see target
				if ((positiveSeeTime && !seesTarget)
//Or sees target but negative seeTime
                || (seesTarget && !positiveSeeTime))
				{
//Set seeTime to 0
					this.seeTime = 0;
				}




//seeTime increment
				if (seesTarget)
				{
					++this.seeTime;
				}
//Or decrement
				else
				{
					--this.seeTime;
				}




//If target is close enough 
//and has seen target for enough time
				if (targetDist <= (double)this.maxAttackDistance && this.seeTime >= 20)
				{
//Clear current path
					this.entity.getNavigator().clearPath();
//And increment strafe time
					++this.strafingTime;
				}
//If target far or not seen long enough
				else
				{
//Approach target
					this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
//And reset strafe time
					this.strafingTime = -1;
				}




//At 1 second intervals of strafing alternate strafing randomly
				if (this.strafingTime >= 20)
				{
					if ((double)this.entity.getRNG().nextFloat() < 0.3D)
					{
						this.strafingClockwise = !this.strafingClockwise;
					}

					if ((double)this.entity.getRNG().nextFloat() < 0.3D)
					{
						this.strafingBackwards = !this.strafingBackwards;
					}

					this.strafingTime = 0;
				}




//Strafing forced to change at certain distance intervals
				if (this.strafingTime > -1)
				{
					if (targetDist > (double)(this.maxAttackDistance * 0.75F))
					{
						this.strafingBackwards = false;
					}
					else if (targetDist < (double)(this.maxAttackDistance * 0.25F))
					{
						this.strafingBackwards = true;
					}

					this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
					this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
				}
				else
				{
					this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
				}




				if (this.entity.isHandActive())
				{
					if (!seesTarget && this.seeTime < -60)
					{
						this.entity.resetActiveHand();
					}
					else if (seesTarget)
					{
						int i = this.entity.getItemInUseMaxCount();

						if (i >= useTimeNeeded)
						{
							this.entity.resetActiveHand();
							this.entity.attackEntityWithRangedAttack(entitylivingbase, ItemBow.getArrowVelocity(i));
							this.attackTime = this.attackCooldown;
						}
					}
				}
				else if (--this.attackTime <= 0 && this.seeTime >= -60)
				{
					this.entity.setActiveHand(EnumHand.MAIN_HAND);
				}




			}
//If not holding a bow
			else
			{
//Movement becomes linear
				this.entity.setMoveStrafing(0);
//And executes EntityAIAttackMelee
				super.updateTask();
			}
		}
	}
}
