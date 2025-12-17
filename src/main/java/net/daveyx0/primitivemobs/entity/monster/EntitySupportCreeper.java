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
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntitySupportCreeper extends EntityPrimitiveCreeper {

    public ArrayList<String> buffIdList = new ArrayList<>();
    public ArrayList<Potion> buffObjectList = new ArrayList<>();
    public ArrayList<Integer> buffLengthList = new ArrayList<>();
    public ArrayList<Integer> buffStrengthList = new ArrayList<>();
    public ArrayList<Integer> buffStrengthListPowered = new ArrayList<>();

	public EntitySupportCreeper(World worldIn) {
		super(worldIn);

//Buff ids provided by user
        buffIdList = new ArrayList<>(Arrays.asList(PrimitiveMobsConfigSpecial.getSupportCreeperBuffList()));
//Conversion of buff Id list to Potion object list
        for(String buffId : buffIdList)
        {
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(buffId));
            buffObjectList.add(potion);
        }
//Use streams to convert the ints in the arrays to Integers for the ArrayLists
        buffLengthList = new ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffLengthList()).boxed().collect(Collectors.toList()));
        buffStrengthList = new ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthList()).boxed().collect(Collectors.toList()));
//Strength of buffs when powered
        buffStrengthListPowered = new ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getSupportCreeperBuffStrengthListPowered()).boxed().collect(Collectors.toList()));

        this.tasks.addTask(2, new EntitySupportCreeper.EntityAIBuffMob(this, this.buffObjectList, this.buffLengthList, this.buffStrengthList, this.buffStrengthListPowered));  
	}
	
    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityPlayer.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
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
        compound.setTag("BuffIds", idList);

//Loop through field lists and append their values to NBT tag lists
        NBTTagList lengthList = new NBTTagList();
        for (int length : this.buffLengthList)
        {
            lengthList.appendTag(new NBTTagInt(length));
        }
        compound.setTag("BuffLengths", lengthList);

//Loop through field lists and append their values to NBT tag lists
        NBTTagList strengthList = new NBTTagList();
        for (int strength : this.buffStrengthList)
        {
            strengthList.appendTag(new NBTTagInt(strength));
        }
        compound.setTag("BuffStrengths", strengthList);

//Loop through field lists and append their values to NBT tag lists
        NBTTagList strengthListPowered = new NBTTagList();
        for (int strengthPowered : this.buffStrengthListPowered)
        {
            strengthListPowered.appendTag(new NBTTagInt(strengthPowered));
        }
        compound.setTag("BuffStrengthsPowered", strengthListPowered);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//If corresponding NBT present
        if (compound.hasKey("BuffIds"))
        {
//Clear corresponding field lists
            buffIdList.clear();
            buffObjectList.clear(); 
//8 is for strings
//Get NBT list
            NBTTagList idList = compound.getTagList("BuffIds", 8);
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

//If corresponding NBT present
        if (compound.hasKey("BuffLengths"))
        {
//Clear corresponding field list
            buffLengthList.clear();
//3 is for ints
//Get NBT list
            NBTTagList lengthList = compound.getTagList("BuffLengths", 3);
            for (int i = 0; i < lengthList.tagCount(); i++)
            {
//Fill field list
                buffLengthList.add(lengthList.getIntAt(i));
            }
        }

//If corresponding NBT present
        if (compound.hasKey("BuffStrengths"))
        {
//Clear corresponding field list
            buffStrengthList.clear();
//3 is for ints
//Get NBT list
            NBTTagList strengthList = compound.getTagList("BuffStrengths", 3);
            for (int i = 0; i < strengthList.tagCount(); i++)
            {
//Fill field list
                buffStrengthList.add(strengthList.getIntAt(i));
            }
        }

//If corresponding NBT present
        if (compound.hasKey("BuffStrengthsPowered"))
        {
//Clear corresponding field list
            buffStrengthListPowered.clear();
//3 is for ints
//Get NBT list
            NBTTagList poweredList = compound.getTagList("BuffStrengthsPowered", 3);
            for (int i = 0; i < poweredList.tagCount(); i++)
            {
//Fill field list
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
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }
    
    public void onUpdate()
    {
    	if(this.getHealth() < this.getMaxHealth()/2)
    	{
			while(this.tasks.taskEntries.stream()
			.filter(taskEntry -> taskEntry.action instanceof EntityAIAvoidEntity).findFirst().isPresent())
			{
				this.tasks.taskEntries.stream().filter(taskEntry -> taskEntry.action instanceof EntityAIAvoidEntity)
				.findFirst().ifPresent(taskEntry -> this.tasks.removeTask(taskEntry.action));
			}
			this.tasks.addTask(2, new EntityAICreeperSwellTameable(this));
    		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    	}
    	super.onUpdate();
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
		}
		/**
		* Returns whether the EntityAIBase should begin execution.
		*/
		public boolean shouldExecute()
		{
			ITameableEntity tameable = EntityUtil.getCapability(this.creeper, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
			if(tameable != null && tameable.isTamed() && tameable.getFollowState() == 0)
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
			if(tameable != null && tameable.isTamed() && tameable.getFollowState() == 0)
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
			if(tameable != null && tameable.isTamed() && tameable.getFollowState() != 0)
			{
				if(tameable.getOwner(this.creeper) != null && tameable.getOwner(this.creeper).getDistanceSq(this.creeper) < 30)
				{
					return tameable.getOwner(this.creeper);
				}
			}
			else if(tameable == null || !tameable.isTamed())
			{
	            List<Entity> list = this.creeper.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this.creeper, this.creeper.getEntityBoundingBox().expand(10.0D, 4.0D, 10.0D));
	            EntityMob mob = null;
	            double d0 = Double.MAX_VALUE;

	            for (Entity entity : list)
	            {
	            	if(entity != null && entity instanceof EntityMob && !(entity instanceof EntitySupportCreeper))
	            	{
	            		EntityMob mob1 = (EntityMob)entity;
	            		
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

        public void applySpecifiedBuffsIfAbsent(EntityLivingBase entity, ArrayList<Potion> buffObjects, ArrayList<Integer> buffLengths, ArrayList<Integer> buffStrengths)
        {
            for(int i = 0; i < buffObjects.size(); i++)
            {
                if(entity.getActivePotionEffect(buffObjects.get(i)) == null)
                {
                    entity.addPotionEffect(new PotionEffect(buffObjects.get(i), buffLengths.get(i), buffStrengths.get(i)));
                }
            }
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
                	EntityCreeper entitycreeper = (EntityCreeper)mobIdol;
//Make creepers charged
                	if(!entitycreeper.getPowered() && !getEntityWorld().isRemote)	
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
                    	EntityTrollager entitytrollager = (EntityTrollager)mobIdol;
                    	entitytrollager.isBeingSupported = true;
                    }
//Apply specified buffs to all
                    if(this.creeper.getPowered())
                    {
                        this.applySpecifiedBuffsIfAbsent(this.mobIdol, 
                        this.buffObjects, this.buffLengths, this.buffStrengthsPowered);
                    }
                    else
                    {
                        this.applySpecifiedBuffsIfAbsent(this.mobIdol, 
                        this.buffObjects, this.buffLengths, this.buffStrengths);
                    }
                }
//Buff self
                    if(this.creeper.getPowered())
                    {
                        this.applySpecifiedBuffsIfAbsent(this.mobIdol, 
                        this.buffObjects, this.buffLengths, this.buffStrengthsPowered);
                    }
                    else
                    {
                        this.applySpecifiedBuffsIfAbsent(this.mobIdol, 
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
