package net.daveyx0.primitivemobs.entity.ai;

import net.daveyx0.primitivemobs.entity.monster.EntityFlameSpewer;
import net.daveyx0.primitivemobs.entity.item.EntityFlameSpit;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

	public class EntityAIFlameSpewAttack extends EntityAIBase
    {
        private final EntityFlameSpewer spewer;
        private float visualState;


//1 means preparing, above means shooting
        private int attackStep;
//Saw target while preattack
        private boolean hasSeenTargetYet;


//Total time spent preattack and preparing before attacking
        private int preAttackCountdownMax;
//Shot lifetime
        private int shotLifetime;
//Shot particle density
        private int shotParticles;
//When in preattack phase, not on fire, and grant invulnerability
        private int goInvulnerableWhen;
//When in preattack phase sets on fire and go vulnerable
        protected int goVulnerableWhen;


//Attack rapidfire specific
        protected int attackRapidfireShots;
        protected int attackRapidfireInterval;
        protected double attackRapidfireSpread;


//Exclusive with movement and look tasks
        public EntityAIFlameSpewAttack(EntityFlameSpewer spewerIn, int preAttackCountdownMax, int shotLifetime, int shotParticles,
        int goInvulnerableWhen, int goVulnerableWhen, int attackRapidfireShots, int attackRapidfireInterval, double attackRapidfireSpread)
        {
            this.spewer = spewerIn;
            this.setMutexBits(3);

            this.preAttackCountdownMax = preAttackCountdownMax;
            this.shotLifetime = shotLifetime;
            this.shotParticles = shotParticles;
            this.goInvulnerableWhen = goInvulnerableWhen;
            this.goVulnerableWhen = goVulnerableWhen;
            this.attackRapidfireShots = attackRapidfireShots;
            this.attackRapidfireInterval = attackRapidfireInterval;
            this.attackRapidfireSpread = attackRapidfireSpread;
        }

//Executes if it has target
        public boolean shouldExecute()
        {
            EntityLivingBase target = this.spewer.getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        public void startExecuting()
        {
//Delay until next attack
            this.spewer.nextActionCountdown = this.preAttackCountdownMax;
//The max attackStep actually represents the attack being FINISHED
            this.attackStep = this.attackRapidfireShots;

            this.spewer.setReadyToShoot(false);

            hasSeenTargetYet = false;

            this.visualState = 0;
        }
        
        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return this.shouldExecute();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask()
        {
            this.spewer.setOnFire(false);
            this.spewer.nextActionCountdown = this.preAttackCountdownMax;
            this.attackStep = this.attackRapidfireShots;

            this.spewer.setReadyToShoot(false);

            this.spewer.setInDanger(false);
            this.visualState = 0;
            spewer.setVisualState(visualState);
            spewer.setActionCountUp(attackStep);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
//Decrements attack delay
            --this.spewer.nextActionCountdown;
            EntityLivingBase target = this.spewer.getAttackTarget();




//Inbetween-attack preparation and animation logic
            if(!this.spewer.isReadyToShoot() && this.spewer.isInLava() && this.spewer.canEntityBeSeen(target))
            {

//Will still be on fire from a previous attack here
            	if(this.spewer.nextActionCountdown <= this.goInvulnerableWhen)
            	{
//Cancel prior on fire
            		this.spewer.setOnFire(false);
            		this.hasSeenTargetYet = false;
            	}


//And then
                if(!spewer.isOnFire())
                {
                    if(this.spewer.canEntityBeSeen(target))
                    {
//Set necessary flag for attack
                    	this.hasSeenTargetYet = true;
                    }


//When not performing attack yet use attackStep for animation
                    this.attackStep = (this.spewer.nextActionCountdown * (20 / this.preAttackCountdownMax));

//When attack time is very near does something related to animation
                    if(this.spewer.nextActionCountdown <= 3)
                    {
                        this.visualState += 0.05f;
                    }
                }
            }




//Get target distance
            double d0 = this.spewer.getDistanceSq(target);
//If target is close and spewer is not in lava, in danger
            if (d0 < 5.0D && !this.spewer.isInLava()) { this.spewer.setInDanger(true); }




//Attack logic is here
            else if (d0 < (this.getFollowDistance() * this.getFollowDistance())  && this.spewer.isInLava())
            {
            	this.spewer.setInDanger(false);
                double d1 = target.posX - this.spewer.posX;
                double d2 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F + 0.25f) - (this.spewer.posY + (double)(this.spewer.height / 2.0F));
                double d3 = target.posZ - this.spewer.posZ;




//Attack only executes after nextActionCountdown reaches 0
                if (this.spewer.nextActionCountdown <= 0)
                {
//attackStep of 1 represents preparation
                    ++this.attackStep;
                    visualState -= 0.05f;

//Preparation, some ticks between setting on fire and actually attacking
                    if (this.attackStep == 1)
                    {
                        this.spewer.nextActionCountdown = this.goVulnerableWhen;
                        this.spewer.setOnFire(true);
                        this.spewer.setReadyToShoot(true);
                    }
//Main shot delay logic
                    else if (this.attackStep <= this.attackRapidfireShots)
                    {
                        this.spewer.nextActionCountdown = this.attackRapidfireInterval;
                        this.spewer.setOnFire(true);
                        this.spewer.setReadyToShoot(true);
                    }
//If max shots performed
                    else
                    {
                        this.spewer.nextActionCountdown = this.preAttackCountdownMax;
                        this.attackStep = this.attackRapidfireShots;
//This is the ACTUAL state reset and takes it back to inbetween-attack logic
                        this.spewer.setReadyToShoot(false);
                    }



//Past first attackStep, each one shoots
                    if (this.attackStep > 1 && hasSeenTargetYet)
                    {
                        float f = MathHelper.sqrt(MathHelper.sqrt(d0) * 0.1F);
                        this.spewer.world.playEvent((EntityPlayer)null, 1018, new BlockPos((int)this.spewer.posX, (int)this.spewer.posY, (int)this.spewer.posZ), 0);

                        for (int i = 0; i < 1; ++i)
                        {
                            EntityFlameSpit entitysmallfireball = new EntityFlameSpit(this.spewer.world, this.spewer, d1 + this.spewer.getRNG().nextGaussian() * this.attackRapidfireSpread * (double)f, d2 - this.spewer.getRNG().nextGaussian() * this.attackRapidfireSpread * (double)f, d3 + this.spewer.getRNG().nextGaussian() * this.attackRapidfireSpread * (double)f, this.shotLifetime, this.shotParticles);
                            entitysmallfireball.posY = this.spewer.posY + (double)(this.spewer.height / 2.0F) -0.5F;
                            this.spewer.world.spawnEntity(entitysmallfireball);
                        }
                    }
                }
            }




//If not in range can approach target's position
            else if(this.spewer.isInLava()  && this.spewer.canEntityBeSeen(target) && !this.spewer.isReadyToShoot())
            {
                double d1 = target.posX - this.spewer.posX;
                double d3 = target.posZ - this.spewer.posZ;
                spewer.motionX = d1 * 0.01;
                spewer.motionZ = d3 * 0.01;
            	this.spewer.setInDanger(false);
                spewer.setOnFire(false);
            }
            else
            {
                if(!this.spewer.isReadyToShoot())
                {
                	this.spewer.getNavigator().clearPath();
                	this.spewer.setInDanger(false);
                    spewer.setOnFire(false);
                }
            }
            
            this.spewer.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
            
            if(visualState < 0)
            {
            	visualState = 0; 
            }
            else if (visualState > 0.4)
            {
            	visualState = 0.4f;
            }
            
            spewer.setVisualState(visualState);
            spewer.setActionCountUp(attackStep);

            super.updateTask();
        }

        private double getFollowDistance()
        {
            IAttributeInstance iattributeinstance = this.spewer.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        }
    }
