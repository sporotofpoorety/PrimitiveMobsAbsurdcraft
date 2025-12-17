package net.daveyx0.primitivemobs.entity.passive;

import javax.annotation.Nullable;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.entity.IMultiMobPassive;
import net.daveyx0.multimob.util.ColorUtil;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsItems;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAIBuffOwnerMultimob;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityChameleon extends EntityAnimal implements IMultiMobPassive
{

	private float R;
	private float G;
	private float B;
	private float NewR;
	private float NewG;
	private float NewB;
	private int colorSpeed = 4;

	private IBlockState currentState;
	private int currentMultiplier;

    protected int shedCooldown;

    protected int shedCooldownMax;
    protected boolean buffEnabled;
    protected String buffId;
    protected Potion buffObject;
    protected int buffStrength;
    protected double buffDistance;
	
	public EntityChameleon(World worldIn)
	{
		super(worldIn);
		this.setSize(0.7f, 0.5f);
		this.setSkinRGB(new int[]{0,125,25});
		this.stepHeight = 1.0f;

        this.shedCooldown = 20;

        this.shedCooldownMax = PrimitiveMobsConfigSpecial.getChameleonShedCooldown() * 20;
        this.buffEnabled = PrimitiveMobsConfigSpecial.getChameleonBuffEnabled();
        this.buffId = PrimitiveMobsConfigSpecial.getChameleonBuffID();
        this.buffObject = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.buffId));
        this.buffStrength = PrimitiveMobsConfigSpecial.getChameleonBuffStrength();
        this.buffDistance = PrimitiveMobsConfigSpecial.getChameleonBuffDistance();

//If buff invalid
        if(this.buffObject == null)
        {
//Remove task
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBuffOwnerMultimob.class);
        }
        else
        {
//If task enabled in config
            if (this.buffEnabled) 
            {
//Get Multimob tameable mob capability
                ITameableEntity chameleonTameableCapability = this.getTameableCapability();

//Add the task with appropriate arguments
                if(chameleonTameableCapability != null)
                {
                    this.tasks.addTask(2, new EntityAIBuffOwnerMultimob(this, 
                    this.buffObject, this.buffStrength, this.buffDistance, chameleonTameableCapability));
                }            
            }
//If task disabled
            else
            {
//Remove the task
                TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBuffOwnerMultimob.class);             
            }
        } 
	}

	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Preserves field values including assigned by NBT
        compound.setInteger("ShedCooldownMax", this.shedCooldownMax);
        compound.setBoolean("BuffEnabled", this.buffEnabled);
        compound.setString("BuffId", this.buffId);
        compound.setInteger("BuffStrength", this.buffStrength);
        compound.setDouble("BuffDistance", this.buffDistance);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("ShedCooldownMax")) { this.shedCooldownMax = compound.getInteger("ShedCooldownMax"); }
        if (compound.hasKey("BuffEnabled")) { this.buffEnabled = compound.getBoolean("BuffEnabled"); }
        if (compound.hasKey("BuffId")) { 
            this.buffId = compound.getString("BuffId"); 
            this.buffObject = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.buffId));
        }
        if (compound.hasKey("BuffStrength")) { this.buffStrength = compound.getInteger("BuffStrength"); }
        if (compound.hasKey("BuffDistance")) { this.buffDistance = compound.getDouble("BuffDistance"); }


//If buff invalid
        if(this.buffObject == null)
        {
//Remove task
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBuffOwnerMultimob.class);
        }
        else
        {
//Remove task if present, and 
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBuffOwnerMultimob.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
            if (this.buffEnabled) 
            {
//Get Multimob tameable mob capability
                ITameableEntity chameleonTameableCapability = this.getTameableCapability();

//Add the task with NBT arguments
                if(chameleonTameableCapability != null)
                {
                    this.tasks.addTask(2, new EntityAIBuffOwnerMultimob(this, 
                    this.buffObject, this.buffStrength, this.buffDistance, chameleonTameableCapability));
                }
            }
        } 
    }
	
	protected void initEntityAI()
    {
//Made panic lower priority just in case
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(3, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }
	
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
    }
   
    
    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F;
    }

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) 
    {
		return new EntityChameleon(this.getEntityWorld());
	}
	
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_CHAMELEON;
    }
	
	@Override
	public void onUpdate() 
	{
		if (this.isInWater() && !collidedHorizontally) {
			motionY = 0.02D;
		}
		
		if(getEntityWorld().isRemote)
		{
			changeColor(this);
		}

//Clever, each update,
//it checks its own color and
//loops until the color timer is reached,
//adjusting the R, G, and B to the assigned new values
		if(R != NewR || G != NewG || B != NewB)
		{
			for(int i = 0; i < colorSpeed; i++)
			{
				if(R > NewR)
				{
					R--;
				}
				else if (R < NewR)
				{
					R++;
				}
			
				if(G > NewG)
				{
					G--;
				}
				else if (G < NewG)
				{
					G++;
				}
			
				if(B > NewB)
				{
					B--;
				}
				else if (B < NewB)
				{
					B++;
				}
			}
		}

        if(!this.getEntityWorld().isRemote && --this.shedCooldown <= 0) {
            this.dropItem(PrimitiveMobsItems.CAMOUFLAGE_DYE, 1);            
            this.shedCooldown = this.shedCooldownMax;
        }
        
		super.onUpdate();
	}

//Get the multimob tameable mob capability
    public ITameableEntity getTameableCapability()
    {
//Get capability by entity, capability type, and facing direction (nullable)
		ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
		return tameable;
    }


    
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.FERMENTED_SPIDER_EYE;
    }
	
    public float[] getSkinRGB()
	{
		return new float[]{R,G,B};
	}
	
	public void setSkinRGB(int[] RGB)
	{
		R = (float)RGB[0];
		G = (float)RGB[1];
		B = (float)RGB[2];
	}
	
	public float[] getNewSkinRGB()
	{
		return new float[]{NewR,NewG,NewB};
	}
	
	public void setNewSkinRGB(int[] RGB)
	{
		NewR = (float)RGB[0];
		NewG = (float)RGB[1];
		NewB = (float)RGB[2];
	}

	public void changeColor(Entity entity)
	{
		int i = MathHelper.floor(entity.posX);
        int j = MathHelper.floor(entity.getEntityBoundingBox().minY);
        int k = MathHelper.floor(entity.posZ);
        
		if(entity.getEntityWorld().getBlockState(new BlockPos(i, j, k)).getBlock() == Blocks.AIR)
		{
			j = MathHelper.floor(entity.getEntityBoundingBox().minY - 0.1);
		}
		
		BlockPos pos = new BlockPos(i, j, k);
		IBlockState state = entity.getEntityWorld().getBlockState(pos);
		
		int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, getEntityWorld(), pos, 0);
		
		//PrimitiveMobsLogger.info(worldObj, state + " " + colorMultiplier);
		if(state.getBlock() != Blocks.AIR && (currentState != state || currentMultiplier != colorMultiplier))
		{
			currentState = state;
			currentMultiplier = colorMultiplier;
			
			int[] newColor = ColorUtil.getBlockStateColor(state, pos, getEntityWorld(), true);
			if(newColor != null)
			{
				if(ColorUtil.isColorInvalid(newColor))
				{
					newColor = new int[]{63,118,42,255};
				}
				setNewSkinRGB(newColor);
			}
		}
	}
	
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	if(type == EnumCreatureType.CREATURE){return false;}
    	return super.isCreatureType(type, forSpawnCount);
    }

}
