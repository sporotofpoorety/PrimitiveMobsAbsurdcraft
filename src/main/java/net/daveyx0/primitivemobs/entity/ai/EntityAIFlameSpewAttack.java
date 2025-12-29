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
        private final EntityFlameSpewer gooner;
        private float irrelevantVariable;


//1 means leaking, above means cumming
        private int damBursting;
//Linear general timer
        private int nextActionCountdown;


//Total time spent gooning and leaking before cumming
        private int precumCountdownMax;
//Cum lifetime
        private int cumLifetime;
//Cum particle density
        private int cumParticles;
//When in precum phase does foreskin protract and grant invulnerability
        private int foreskinProtractWhen;
//Is either leaking or already cumming
        private boolean readyToCum;
//Saw waifu while gooning
        private boolean hasSeenWaifuYet;
//When in precum phase does foreskin retract and go vulnerable
        protected int foreskinRetractWhen;


//Cum rapidfire specific
        protected int cumRapidfireShots;
        protected int cumRapidfireInterval;
        protected double cumRapidfireSpread;


//Exclusive with movement and look tasks
        public EntityAIFlameSpewAttack(EntityFlameSpewer goonerIn, int precumCountdownMax, int cumLifetime, int cumParticles,
        int foreskinProtractWhen, int foreskinRetractWhen, int cumRapidfireShots, int cumRapidfireInterval, double cumRapidfireSpread)
        {
            this.gooner = goonerIn;
            this.setMutexBits(3);

            this.precumCountdownMax = precumCountdownMax;
            this.cumLifetime = cumLifetime;
            this.cumParticles = cumParticles;
            this.foreskinProtractWhen = foreskinProtractWhen;
            this.foreskinRetractWhen = foreskinRetractWhen;
            this.cumRapidfireShots = cumRapidfireShots;
            this.cumRapidfireInterval = cumRapidfireInterval;
            this.cumRapidfireSpread = cumRapidfireSpread;
        }

//Executes if it has target
        public boolean shouldExecute()
        {
            EntityLivingBase waifu = this.gooner.getAttackTarget();
            return waifu != null && waifu.isEntityAlive();
        }

        public void startExecuting()
        {
            this.irrelevantVariable = 0;
//It's weird that it's implemented this way, but
//the max damBursting actually represents the attack being FINISHED
            this.damBursting = this.cumRapidfireShots;
//Delay until next attack
            this.nextActionCountdown = this.precumCountdownMax;
            readyToCum = false;
            hasSeenWaifuYet = false;
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
            this.gooner.setForeskinPulled(false);
            this.irrelevantVariable = 0;
            this.damBursting = this.cumRapidfireShots;
            this.nextActionCountdown = this.precumCountdownMax;
            gooner.setNextActionCountdown(damBursting);
            gooner.setIrrelevantVariable(irrelevantVariable);
            this.gooner.setTouchingGrass(false);
            readyToCum = false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
//Decrements attack delay
            --this.nextActionCountdown;
            EntityLivingBase waifu = this.gooner.getAttackTarget();

//Inbetween-attack preparation and animation logic
            if(!this.readyToCum && this.gooner.isInLava() && this.gooner.canEntityBeSeen(waifu))
            {
//Will still be on fire from a previous attack here
//If half or less cooldown left
            	if(nextActionCountdown <= this.foreskinProtractWhen)
            	{
//Cancel prior on fire
            		this.gooner.setForeskinPulled(false);
            		this.hasSeenWaifuYet = false;
            	}
//And then
                if(!gooner.isForeskinPulled())
                {
                    if(this.gooner.canEntityBeSeen(waifu))
                    {
//Set necessary flag for attack
                    	this.hasSeenWaifuYet = true;
                    }
//When not performing attack yet use damBursting for animation
                    this.damBursting = (this.nextActionCountdown * (20 / this.precumCountdownMax));

//When attack time is very near does something related to animation
                    if(nextActionCountdown <= 3)
                    {
                        this.irrelevantVariable += 0.05f;
                    }
                }
            }

            double d0 = this.gooner.getDistanceSq(waifu);

//If target is close and gooner is not in lava
            if (d0 < 5.0D && !this.gooner.isInLava())
            {
//In danger
            	this.gooner.setTouchingGrass(true);
/*
//Melee attack every 20 ticks
                if (this.nextActionCountdown <= 0)
                {
                    this.nextActionCountdown = 20;
                    this.gooner.attackEntityAsMob(waifu);
                }
*/
            }

//Attack logic is here
            else if (d0 < (this.getFollowDistance() * this.getFollowDistance())  && this.gooner.isInLava())
            {
            	this.gooner.setTouchingGrass(false);
                double d1 = waifu.posX - this.gooner.posX;
                double d2 = waifu.getEntityBoundingBox().minY + (double)(waifu.height / 2.0F + 0.25f) - (this.gooner.posY + (double)(this.gooner.height / 2.0F));
                double d3 = waifu.posZ - this.gooner.posZ;

//Attack only executes after nextActionCountdown reaches 0
                if (this.nextActionCountdown <= 0)
                {
//damBursting of 1 represents pre-attack
                    ++this.damBursting;
                    irrelevantVariable -= 0.05f;

//Pre-attack, some ticks between setting on fire and actually attacking
                    if (this.damBursting == 1)
                    {
                        this.nextActionCountdown = this.foreskinRetractWhen;
                        this.gooner.setForeskinPulled(true);
                        readyToCum = true;
                    }
//Main shot delay logic
                    else if (this.damBursting <= this.cumRapidfireShots)
                    {
                        this.nextActionCountdown = this.cumRapidfireInterval;
                        this.gooner.setForeskinPulled(true);
                        readyToCum = true;
                    }
//If max shots performed
                    else
                    {
                        this.nextActionCountdown = this.precumCountdownMax;
                        this.damBursting = this.cumRapidfireShots;
//This is the ACTUAL state reset and takes it back to inbetween-attack logic
                        readyToCum = false;
                    }

                    if (this.damBursting > 1 && hasSeenWaifuYet)
                    {
                        float f = MathHelper.sqrt(MathHelper.sqrt(d0) * 0.1F);
                        this.gooner.world.playEvent((EntityPlayer)null, 1018, new BlockPos((int)this.gooner.posX, (int)this.gooner.posY, (int)this.gooner.posZ), 0);

                        for (int i = 0; i < 1; ++i)
                        {
                            EntityFlameSpit entitysmallfireball = new EntityFlameSpit(this.gooner.world, this.gooner, d1 + this.gooner.getRNG().nextGaussian() * this.cumRapidfireSpread * (double)f, d2 - this.gooner.getRNG().nextGaussian() * this.cumRapidfireSpread * (double)f, d3 + this.gooner.getRNG().nextGaussian() * this.cumRapidfireSpread * (double)f, this.cumLifetime, this.cumParticles);
                            entitysmallfireball.posY = this.gooner.posY + (double)(this.gooner.height / 2.0F) -0.5F;
                            this.gooner.world.spawnEntity(entitysmallfireball);
                        }
                    }
                }
            }
//If not in range can approach target's position
            else if(this.gooner.isInLava()  && this.gooner.canEntityBeSeen(waifu))
            {
                double d1 = waifu.posX - this.gooner.posX;
                double d3 = waifu.posZ - this.gooner.posZ;
                gooner.motionX = d1 * 0.01;
                gooner.motionZ = d3 * 0.01;
            	this.gooner.setTouchingGrass(false);
                gooner.setForeskinPulled(false);
            }
            else
            {
            	this.gooner.getNavigator().clearPath();
            	this.gooner.setTouchingGrass(false);
                gooner.setForeskinPulled(false);
            }
            
            this.gooner.getLookHelper().setLookPositionWithEntity(waifu, 10.0F, 10.0F);
            
            if(irrelevantVariable < 0)
            {
            	irrelevantVariable = 0; 
            }
            else if (irrelevantVariable > 0.4)
            {
            	irrelevantVariable = 0.4f;
            }
            
            gooner.setIrrelevantVariable(irrelevantVariable);
            gooner.setNextActionCountdown(damBursting);

            super.updateTask();
        }

        private double getFollowDistance()
        {
            IAttributeInstance iattributeinstance = this.gooner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        }
    }
