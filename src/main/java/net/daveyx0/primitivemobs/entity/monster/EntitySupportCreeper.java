package net.daveyx0.primitivemobs.entity.monster;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.EntitiesWithinChunks;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellSpecial;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;


import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral; 
import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;




public class EntitySupportCreeper extends EntityPrimitiveCreeper {

//Access getters and setters of EntityCreeper mixin
    public IMixinEntityCreeper supportCreeperMixin;
    public IMixinEntityLiving livingMixin;


//Normal buff lists
    protected ArrayList<String> buffIdList = new ArrayList<>();
    protected ArrayList<Potion> buffObjectList = new ArrayList<>();
    protected ArrayList<Integer> buffLengthList = new ArrayList<>();
    protected ArrayList<Integer> buffStrengthList = new ArrayList<>();
    protected ArrayList<Integer> buffStrengthListPowered = new ArrayList<>();


//Special configs specific to this mob
    protected int specialChunkRadius;

//Same list logic but for special attack
    protected ArrayList<String> buffIdListSpecial = new ArrayList<>();
    protected ArrayList<Potion> buffObjectListSpecial = new ArrayList<>();
    protected ArrayList<Integer> buffLengthListSpecial = new ArrayList<>();
    protected ArrayList<Integer> buffStrengthListSpecial = new ArrayList<>();
    protected ArrayList<Integer> buffStrengthListPoweredSpecial = new ArrayList<>();

	public EntitySupportCreeper(World worldIn) {
		super(worldIn);
//Fire immunity
        isImmuneToFire = true;
//Access getters and setters of these mixins
        supportCreeperMixin = (IMixinEntityCreeper) this;
        livingMixin = (IMixinEntityLiving) this;


//Buff ids provided by user
        buffIdList = new 
            ArrayList<>(Arrays.asList(PrimitiveMobsConfigSpecial.getSupportCreeperBuffList()));
//Conversion of buff Id list to Potion list
        for(String buffId : buffIdList)
        {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(buffId));
//Fill Potion list
            buffObjectList.add(potion);
        }


//Object-wrap the ints for the ArrayLists
        buffLengthList = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffLengthList()).boxed().collect(Collectors.toList()));
        buffStrengthList = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthList()).boxed().collect(Collectors.toList()));
//Strength of buffs when powered
        buffStrengthListPowered = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthListPowered()).boxed().collect(Collectors.toList()));

//Assign the task
        this.tasks.addTask(2, new EntitySupportCreeper.EntityAIBuffMob(this, this.buffObjectList, this.buffLengthList, this.buffStrengthList, this.buffStrengthListPowered));  



//Base special configs
        supportCreeperMixin.setCreeperSpecialEnabled(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialEnabled());
        supportCreeperMixin.setCreeperSpecialCooldownInterrupted(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialCooldownInterrupted());
        supportCreeperMixin.setCreeperSpecialCooldownAttacked(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialCooldownAttacked());
        supportCreeperMixin.setCreeperSpecialCooldownFrustrated(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialCooldownFrustrated());
        supportCreeperMixin.setCreeperSpecialCooldownOver(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialCooldownOver());
        supportCreeperMixin.setCreeperSpecialCooldownStunned(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialCooldownStunned());
        supportCreeperMixin.setCreeperSpecialStunnedDuration(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialStunnedDuration());
        supportCreeperMixin.setCreeperSpecialIgnitedTimeMax(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialIgnitedTimeMax());
        supportCreeperMixin.setCreeperSpecialInterruptedMax(PrimitiveMobsConfigSpecial.getSupportCreeperSpecialInterruptedMax());
        supportCreeperMixin.setCreeperSpecialInterruptedDamage((float) PrimitiveMobsConfigSpecial.getSupportCreeperSpecialInterruptedDamage());


//Specific to this mob
        specialChunkRadius = PrimitiveMobsConfigSpecial.getSupportCreeperSpecialChunkRadius();



//If task absent
        if(!TaskUtils.mobHasTask(this, EntityAICreeperSwellSpecial.class))
        {
//And task enabled in config
            if (supportCreeperMixin.getCreeperSpecialEnabled())
            {
//Can target
                this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
//Add the special attack task
                this.tasks.addTask(1, new EntityAICreeperSwellSpecial(this));
            }
        }
//If task is here...
        else
        {
//And task disabled in config
            if (!supportCreeperMixin.getCreeperSpecialEnabled())
            {
//Can't target
                TaskUtils.mobRemoveTargetTaskIfPresent(this, EntityAINearestAttackableTarget.class);
//Remove the special attack task
                TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
            }                
        }



//Repeat list logic for special attack config
        buffIdListSpecial = new 
            ArrayList<>(Arrays.asList(PrimitiveMobsConfigSpecial.getSupportCreeperBuffListSpecial()));

//Conversion of buff Id list to Potion object list
        for(String buffId : buffIdListSpecial)
        {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(buffId));
//Fill object list
            buffObjectListSpecial.add(potion);
        }

//Use streams to convert the ints in the arrays to Integers for the ArrayLists
        buffLengthListSpecial = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffLengthListSpecial()).boxed().collect(Collectors.toList()));
        buffStrengthListSpecial = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthListSpecial()).boxed().collect(Collectors.toList()));
//Strength of buffs when powered
        buffStrengthListPoweredSpecial = new 
            ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthListPoweredSpecial()).boxed().collect(Collectors.toList()));
	}
	

    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityPlayer.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
    }


//Apply specific buffs to entity if absent
    public void applySpecifiedBuffsIfAbsent(EntityLivingBase entity, 
    ArrayList<Potion> buffObjects, ArrayList<Integer> buffLengths, ArrayList<Integer> buffStrengths)
    {
//For each buff object in ArrayList
        for(int i = 0; i < buffObjects.size(); i++)
        {
//If entity does not have that buff object
            if(entity.getActivePotionEffect(buffObjects.get(i)) == null)
            {
//Add buff
                entity.addPotionEffect(new PotionEffect(buffObjects.get(i), buffLengths.get(i), buffStrengths.get(i)));
            }
        }
    }


    public void onUpdate()
    {
/*
//Kept this for posterity on stream methods
		while(this.tasks.taskEntries.stream()
		.filter(taskEntry -> taskEntry.action instanceof EntityAIAvoidEntity).findFirst().isPresent())
		{
			this.tasks.taskEntries.stream().filter(taskEntry -> taskEntry.action instanceof EntityAIAvoidEntity)
			.findFirst().ifPresent(taskEntry -> this.tasks.removeTask(taskEntry.action));
		}
*/
    	super.onUpdate();

//Get target
        EntityLivingBase supportCreeperAttackTarget = this.getAttackTarget();

//Support Creeper specific logic on special attack
        if(supportCreeperMixin.getCreeperStateSpecial() > 0)
        {
//Null protection and reset special attack state if target is gone 
            if(supportCreeperAttackTarget != null)
            {
//Check if it's time to do attack...
                if(supportCreeperMixin.getCreeperSpecialIgnitedTime() >= supportCreeperMixin.getCreeperSpecialIgnitedTimeMax())
                {
//Get arraylist of mobs to buff
                    ArrayList<EntityMob> specialAttackBenefitterList
                     = EntitiesWithinChunks.getMobsInChunkRadius(this.getEntityWorld(), this.posX, this.posZ, this.specialChunkRadius);

//If list not empty
                    if(!specialAttackBenefitterList.isEmpty())
                    {
//For each mob in it
                        for(EntityMob specialAttackBenefitter : specialAttackBenefitterList)
	                    {
//If buffing a creeper
                            if(specialAttackBenefitter instanceof EntityCreeper)
                            {
                            	EntityCreeper entitycreeper = (EntityCreeper) specialAttackBenefitter;
//Make it charged
                            	if(!entitycreeper.getPowered() && !this.getEntityWorld().isRemote)	
                            	{
                            		entitycreeper.onStruckByLightning(null);
                            	}
//Creepers don't get that fire immunity i guess
                            	if(entitycreeper.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null)
                            	{
                            		entitycreeper.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 100));
                            	}
                            }
//If buffing a trollager
                            if(specialAttackBenefitter instanceof EntityTrollager)
                            {
//Make it stone immune
                            	EntityTrollager entitytrollager = (EntityTrollager) specialAttackBenefitter;
                            	entitytrollager.isBeingSupported = true;
                            }

//Apply specified buffs to all
                            if(this.getPowered())
                            {
                                this.applySpecifiedBuffsIfAbsent((EntityLivingBase) specialAttackBenefitter, 
                                this.buffObjectListSpecial, this.buffLengthListSpecial, this.buffStrengthListPoweredSpecial);
                            }
                            else
                            {
                                this.applySpecifiedBuffsIfAbsent((EntityLivingBase) specialAttackBenefitter, 
                                this.buffObjectListSpecial, this.buffLengthListSpecial, this.buffStrengthListSpecial);
                            }

//20 heart particles for each mob
                            for(int particle = 0; particle < 20; particle++)
                            {
//Spawn heart particles 
                                Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.HEART,
//Hitbox +- hitbox radius 
                                specialAttackBenefitter.posX 
                                    + ((double) rand.nextFloat() * 2.0D * specialAttackBenefitter.width) - specialAttackBenefitter.width,
//0 up to height + 0.5 
                                specialAttackBenefitter.posY + 0.5D 
                                    + ((double) rand.nextFloat() * specialAttackBenefitter.height),
//Hitbox +- hitbox radius  
                                specialAttackBenefitter.posZ 
                                    + ((double) rand.nextFloat() * 2.0D * specialAttackBenefitter.width) - specialAttackBenefitter.width,
//Random movement and empty parameters 
                                this.getRNG().nextGaussian() * 0.04D, this.getRNG().nextGaussian() * 0.04D, this.getRNG().nextGaussian() * 0.04D, new int[0]);
                            }
                        }	        	        
                    }
//After executing play sound
                    this.world.playSound(null, 
                    supportCreeperAttackTarget.posX, supportCreeperAttackTarget.posY, supportCreeperAttackTarget.posZ,
                    PrimitiveMobsSoundEvents.ENTITY_CREEPER_BUFF_EXECUTE, SoundCategory.NEUTRAL, 5.0F, 1.0F);


//After executing reset special and apply cooldown
                    this.resetCreeperSpecial();
                    supportCreeperMixin.setCreeperSpecialCooldown(supportCreeperMixin.getCreeperSpecialCooldownOver());
                }
            }
//If no target reset state
            else
            {
                this.resetCreeperSpecial();
            }
        }
    }


/*
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {      	
        return super.onInitialSpawn(difficulty, livingdata);
    }
*/

	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Preserves field values including assigned by NBT

//Loop through field lists and append their values to NBT tag lists
        NBTTagList idList = new NBTTagList();
        for (String id : this.buffIdList) 
        {
            idList.appendTag(new NBTTagString(id));
        }
        compound.setTag("CreeperBuffIds", idList);

        NBTTagList lengthList = new NBTTagList();
        for (int length : this.buffLengthList)
        {
            lengthList.appendTag(new NBTTagInt(length));
        }
        compound.setTag("CreeperBuffLengths", lengthList);

        NBTTagList strengthList = new NBTTagList();
        for (int strength : this.buffStrengthList)
        {
            strengthList.appendTag(new NBTTagInt(strength));
        }
        compound.setTag("CreeperBuffStrengths", strengthList);

        NBTTagList strengthListPowered = new NBTTagList();
        for (int strengthPowered : this.buffStrengthListPowered)
        {
            strengthListPowered.appendTag(new NBTTagInt(strengthPowered));
        }
        compound.setTag("CreeperBuffStrengthsPowered", strengthListPowered);



        compound.setInteger("CreeperSpecialChunkRadius", this.specialChunkRadius);



//Repeat for special attack NBT
        NBTTagList idListSpecial = new NBTTagList();
        for (String id : this.buffIdListSpecial) 
        {
            idListSpecial.appendTag(new NBTTagString(id));
        }
        compound.setTag("CreeperBuffIdsSpecial", idListSpecial);

        NBTTagList lengthListSpecial = new NBTTagList();
        for (int length : this.buffLengthListSpecial)
        {
            lengthListSpecial.appendTag(new NBTTagInt(length));
        }
        compound.setTag("CreeperBuffLengthsSpecial", lengthListSpecial);

        NBTTagList strengthListSpecial = new NBTTagList();
        for (int strength : this.buffStrengthListSpecial)
        {
            strengthListSpecial.appendTag(new NBTTagInt(strength));
        }
        compound.setTag("CreeperBuffStrengthsSpecial", strengthListSpecial);

        NBTTagList strengthListPoweredSpecial = new NBTTagList();
        for (int strengthPowered : this.buffStrengthListPoweredSpecial)
        {
            strengthListPoweredSpecial.appendTag(new NBTTagInt(strengthPowered));
        }
        compound.setTag("CreeperBuffStrengthsPoweredSpecial", strengthListPoweredSpecial);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//If corresponding NBT present
        if (compound.hasKey("CreeperBuffIds"))
        {
//Clear corresponding field lists
            buffIdList.clear();
            buffObjectList.clear(); 
//8 is for strings
//Get NBT list
            NBTTagList idList = compound.getTagList("CreeperBuffIds", 8);
            for (int i = 0; i < idList.tagCount(); i++) 
            {
//Get each buff ID
                String id = idList.getStringTagAt(i);
//Fill buff ID list
                buffIdList.add(id);
//Buff object list too
                buffObjectList.add(ForgeRegistries.POTIONS.getValue(new ResourceLocation(id)));
            }
        }

        if (compound.hasKey("CreeperBuffLengths"))
        {
            buffLengthList.clear();
//3 is for ints
            NBTTagList lengthList = compound.getTagList("CreeperBuffLengths", 3);

            for (int i = 0; i < lengthList.tagCount(); i++)
            {
                buffLengthList.add(lengthList.getIntAt(i));
            }
        }

        if (compound.hasKey("CreeperBuffStrengths"))
        {
            buffStrengthList.clear();

            NBTTagList strengthList = compound.getTagList("CreeperBuffStrengths", 3);

            for (int i = 0; i < strengthList.tagCount(); i++)
            {
                buffStrengthList.add(strengthList.getIntAt(i));
            }
        }

        if (compound.hasKey("CreeperBuffStrengthsPowered"))
        {
            buffStrengthListPowered.clear();

            NBTTagList poweredList = compound.getTagList("CreeperBuffStrengthsPowered", 3);

            for (int i = 0; i < poweredList.tagCount(); i++)
            {
                buffStrengthListPowered.add(poweredList.getIntAt(i));
            }
        }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIBuffMob.class))
        {
            this.tasks.addTask(2, new EntitySupportCreeper.EntityAIBuffMob(this, this.buffObjectList, this.buffLengthList, this.buffStrengthList, this.buffStrengthListPowered));   
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBuffMob.class);

            this.tasks.addTask(2, new EntitySupportCreeper.EntityAIBuffMob(this, this.buffObjectList, this.buffLengthList, this.buffStrengthList, this.buffStrengthListPowered));         
        }



//Special attack NBT

//Chunk radius
        if (compound.hasKey("CreeperSpecialChunkRadius")) { this.specialChunkRadius = compound.getInteger("CreeperSpecialChunkRadius"); }


//Remove task if present, and
        TaskUtils.mobRemoveTargetTaskIfPresent(this, EntityAINearestAttackableTarget.class); 
        TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
        if (supportCreeperMixin.getCreeperSpecialEnabled()) 
        {
            this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
            this.tasks.addTask(1, new EntityAICreeperSwellSpecial(this));
        }



//Repeat list logic for special attack NBT
        if (compound.hasKey("CreeperBuffIdsSpecial"))
        {
            buffIdListSpecial.clear();
            buffObjectListSpecial.clear(); 

            NBTTagList idList = compound.getTagList("CreeperBuffIdsSpecial", 8);

            for (int i = 0; i < idList.tagCount(); i++) 
            {
                String id = idList.getStringTagAt(i);

                buffIdListSpecial.add(id);
                buffObjectListSpecial.add(ForgeRegistries.POTIONS.getValue(new ResourceLocation(id)));
            }
        }

        if (compound.hasKey("CreeperBuffLengthsSpecial"))
        {
            buffLengthListSpecial.clear();

            NBTTagList lengthList = compound.getTagList("CreeperBuffLengthsSpecial", 3);

            for (int i = 0; i < lengthList.tagCount(); i++)
            {
                buffLengthListSpecial.add(lengthList.getIntAt(i));
            }
        }

        if (compound.hasKey("CreeperBuffStrengthsSpecial"))
        {
            buffStrengthListSpecial.clear();

            NBTTagList strengthList = compound.getTagList("CreeperBuffStrengthsSpecial", 3);

            for (int i = 0; i < strengthList.tagCount(); i++)
            {
                buffStrengthListSpecial.add(strengthList.getIntAt(i));
            }
        }

        if (compound.hasKey("CreeperBuffStrengthsPoweredSpecial"))
        {
            buffStrengthListPoweredSpecial.clear();

            NBTTagList poweredList = compound.getTagList("CreeperBuffStrengthsPoweredSpecial", 3);

            for (int i = 0; i < poweredList.tagCount(); i++)
            {
                buffStrengthListPoweredSpecial.add(poweredList.getIntAt(i));
            }
        }
    }
    

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }




//Reset all special attack logic except cooldown
    public void resetCreeperSpecial()
    {
        supportCreeperMixin.setCreeperSpecialIgnitedTime(0);
        supportCreeperMixin.setCreeperStateSpecial(-1);
    }

    public void creeperSpecialAttemptSound(double atX, double atY, double atZ)
    {
        EntityLivingBase supportCreeperAttackTarget = this.getAttackTarget();

        this.world.playSound(null, 
        supportCreeperAttackTarget.posX, supportCreeperAttackTarget.posY, supportCreeperAttackTarget.posZ,
        PrimitiveMobsSoundEvents.ENTITY_CREEPER_BUFF_ATTEMPT, SoundCategory.NEUTRAL, 5.0F, 1.0F);
    }

    public void creeperSpecialParticles()
    {
        for (int angleStepAt = 0; angleStepAt < 45; angleStepAt += 5) 
        {
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(this.world, 40,
            this.posX, this.posY, this.posZ, this.posX, this.posZ, 80, 8, angleStepAt, 4.0D, 0.15D));
        }
    }
    
 

   
    public class EntityAIBuffMob extends EntityAIBase
    {

    	EntitySupportCreeper creeper;
    	EntityLivingBase mobIdol;
        ArrayList<Potion> buffObjects;
        ArrayList<Integer> buffLengths;
        ArrayList<Integer> buffStrengths;
        ArrayList<Integer> buffStrengthsPowered;
    	
		public EntityAIBuffMob(EntitySupportCreeper entitySupportCreeper, ArrayList<Potion> buffObjects, ArrayList<Integer> buffLengths, ArrayList<Integer> buffStrengths, ArrayList<Integer> buffStrengthsPowered) {

			creeper = entitySupportCreeper;
			mobIdol = null;
            this.buffObjects = buffObjects;
            this.buffLengths = buffLengths;
            this.buffStrengths = buffStrengths;
            this.buffStrengthsPowered = buffStrengthsPowered;

//Mutexbits made it
//trample on EntityAIAvoidEntity

//          this.setMutexBits(1);
		}
		/**
		* Returns whether the EntityAIBase should begin execution.
		*/
		public boolean shouldExecute()
		{
//Check special state instead for incompat with special attack
            if(this.creeper.supportCreeperMixin.getCreeperStateSpecial() > 0)
            {
                return false;
            }
//State special resets when stunned so make sure to check stun as well
            if(this.creeper.livingMixin.getAbsurdcraftStunned())
            {
                return false;
            }

			ITameableEntity tameable = EntityUtil.getCapability(this.creeper, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
			if(this.creeper.isTamed() && tameable.getFollowState() == 0)
			{
				return false;
			}
	        return true;
		}

		/**
	    * Returns whether an in-progress EntityAIBase should continue executing
		*/
		public boolean continueExecuting()
	    {
			ITameableEntity tameable = EntityUtil.getCapability(this.creeper, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
			if(this.creeper.isTamed() && tameable.getFollowState() == 0)
			{
				return false;
			}
			return this.mobIdol.isEntityAlive() && this.creeper.getDistanceSq(this.mobIdol) <= 25D * 25D;
	    }
		
	    /**
	     * Execute a one shot task or start executing a continuous task
	     */
	    public void startExecuting()
	    {
	    	this.mobIdol = this.findMobToSupport();
	    }

	    /**
	     * Resets the task
	     */
	    public void resetTask()
	    {
	        this.mobIdol = null;
	    }
	    
	    public EntityLivingBase findMobToSupport()
	    {
			ITameableEntity tameable = EntityUtil.getCapability(this.creeper, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
			if(this.creeper.isTamed() && tameable.getFollowState() != 0)
			{
				if(tameable.getOwner(this.creeper) != null && tameable.getOwner(this.creeper).getDistanceSq(this.creeper) < 30)
				{
					return tameable.getOwner(this.creeper);
				}
			}
			else
			{
	            List<Entity> list = this.creeper.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this.creeper, this.creeper.getEntityBoundingBox().expand(10.0D, 4.0D, 10.0D));
	            EntityMob mob = null;
	            double d0 = Double.MAX_VALUE;

	            for (Entity entity : list)
	            {
	            	if(entity != null && entity instanceof EntityMob && !(entity instanceof EntitySupportCreeper))
	            	{
	            		EntityMob mob1 = (EntityMob) entity;
	            		
	            		if(mob1.getActivePotionEffects().isEmpty())
	            		{
	            			double d1 = this.creeper.getDistanceSq(mob1);

	            			if (d1 <= d0)
	            			{
	            				d0 = d1;
                        		mob = mob1;
	            			}
	            		}
	            	}
	            }
	            
	            if(mob != null)
	            {
	            	 return mob;
	            }
			}
	        return null;
	    }
	    
	    /**
	     * Updates the task
	     */
	    public void updateTask()
	    {
	    	if(mobIdol == null)
	    	{
	    		this.mobIdol = this.findMobToSupport();
	    	}
	    	else
	    	{  	
	            if (this.creeper.getDistance(this.mobIdol) > 2D)
	            {
	                this.creeper.getNavigator().tryMoveToEntityLiving(this.mobIdol, 1F);
	            }
	            
                if(this.mobIdol instanceof EntityCreeper)
                {
                	EntityCreeper entitycreeper = (EntityCreeper) mobIdol;
//Make creepers charged
                	if(!entitycreeper.getPowered() && !this.creeper.getEntityWorld().isRemote)	
                	{
                		entitycreeper.onStruckByLightning(null);
                	}
//Make creepers fire immune
                	if(entitycreeper.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null)
                	{
                		entitycreeper.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 100));
                	}
                }
//If supported mob not creeper
                else
                {
//If trollager set stone immune
                    if(this.mobIdol instanceof EntityTrollager)
                    {
                    	EntityTrollager entitytrollager = (EntityTrollager) mobIdol;
                    	entitytrollager.isBeingSupported = true;
                    }
//Apply specified buffs to all
                    if(this.creeper.getPowered())
                    {
                        applySpecifiedBuffsIfAbsent(this.mobIdol, 
                        this.buffObjects, this.buffLengths, this.buffStrengthsPowered);
                    }
                    else
                    {
                        applySpecifiedBuffsIfAbsent(this.mobIdol, 
                        this.buffObjects, this.buffLengths, this.buffStrengths);
                    }
                }
//Buff self
                if(this.creeper.getPowered())
                {
                    applySpecifiedBuffsIfAbsent(this.mobIdol, 
                    this.buffObjects, this.buffLengths, this.buffStrengthsPowered);
                }
                else
                {
                    applySpecifiedBuffsIfAbsent(this.mobIdol, 
                    this.buffObjects, this.buffLengths, this.buffStrengths);
                }
	    	}       
	    }	
    }
    
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_SUPPORTCREEPER;
    }
}
