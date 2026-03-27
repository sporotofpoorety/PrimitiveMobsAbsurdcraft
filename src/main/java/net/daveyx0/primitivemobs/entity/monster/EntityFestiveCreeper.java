package net.daveyx0.primitivemobs.entity.monster;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.entity.ai.EntityAIBackOffFromEntity;

import org.sporotofpoorety.eternitymode.core.EternityModeSoundEvents;
import org.sporotofpoorety.eternitymode.util.FireworkUtil;

import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellSpecial;
import net.daveyx0.primitivemobs.entity.item.EntityPrimitiveTNTPrimed;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;




public class EntityFestiveCreeper extends EntityPrimitiveCreeper implements IMultiMob {

//Access getters and setters of EntityCreeper mixin
    public IMixinEntityCreeper festiveCreeperMixin;


//Non-special configs
	protected double creeperRetreatDistance;
	protected boolean creeperDestructive;
    protected int creeperCooldownMax;
	protected double creeperPowerBase;
	protected double creeperPowerCharged;
    protected double creeperRange;
	protected boolean creeperRangeIgnore;

    protected int creeperExtraCooldownMax;

    protected boolean creeperRingAttack;
    protected boolean creeperRingAttackDestructive;
    protected double creeperRingAttackPower;
    protected int creeperRingAttackAmount;
    protected boolean creeperRingAttackCharged;
    protected boolean creeperRingAttackDestructiveCharged;
    protected double creeperRingAttackPowerCharged;
    protected int creeperRingAttackAmountCharged;

    protected boolean creeperCrossAttack;
    protected boolean creeperCrossAttackDestructive;
    protected double creeperCrossAttackPower;
    protected int creeperCrossAttackAmount;
    protected double creeperCrossAttackExtent;
    protected boolean creeperCrossAttackCharged;
    protected boolean creeperCrossAttackDestructiveCharged;
    protected double creeperCrossAttackPowerCharged;
    protected int creeperCrossAttackAmountCharged;
    protected double creeperCrossAttackExtentCharged;

    protected boolean creeperLineAttack;
    protected boolean creeperLineAttackDestructive;
    protected double creeperLineAttackPower;
    protected int creeperLineAttackAmount;
    protected double creeperLineAttackExtent;
    protected boolean creeperLineAttackCharged;
    protected boolean creeperLineAttackDestructiveCharged;
    protected double creeperLineAttackPowerCharged;
    protected int creeperLineAttackAmountCharged;
    protected double creeperLineAttackExtentCharged;


//Special handlers
    private static final DataParameter<Boolean> IS_PARTYING = EntityDataManager.<Boolean>createKey(EntityFestiveCreeper.class, DataSerializers.BOOLEAN);
    protected int specialCurrentDuration;
    protected int specialCurrentInterval;
    protected double specialInitialRadians;
    protected double specialCurrentRadians;


//Special configs specific to this mob
    protected int creeperSpecialEndTime;
    protected int creeperSpecialInterval;
    protected double creeperSpecialRadianTurns;
    protected boolean creeperSpecialAttackDestructive;
    protected float creeperSpecialAttackPower;
    protected double creeperSpecialAttackExtent;
    protected boolean creeperSpecialAttackDestructiveCharged;
    protected float creeperSpecialAttackPowerCharged;
    protected double creeperSpecialAttackExtentCharged;



//Constructor
	public EntityFestiveCreeper(World worldIn) 
    {
		super(worldIn);
		isImmuneToFire = true;
//Access getters and setters of EntityCreeper mixin
        festiveCreeperMixin = (IMixinEntityCreeper) this;


//Non-special configs
        creeperRetreatDistance = PrimitiveMobsConfigSpecial.getFestiveCreeperRetreatDistance();
	    creeperDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperDestructive();
	    creeperCooldownMax = PrimitiveMobsConfigSpecial.getFestiveCreeperCooldownMax();
	    creeperPowerBase = PrimitiveMobsConfigSpecial.getFestiveCreeperPowerBase();
	    creeperPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperPowerCharged();
        creeperRange = PrimitiveMobsConfigSpecial.getFestiveCreeperRange();
	    creeperRangeIgnore = PrimitiveMobsConfigSpecial.getFestiveCreeperRangeIgnore();

	    creeperExtraCooldownMax = PrimitiveMobsConfigSpecial.getFestiveCreeperExtraCooldownMax();

        creeperRingAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttack();
        creeperRingAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackDestructive();
        creeperRingAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackPower();
        creeperRingAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackAmount();
        creeperRingAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackCharged();
        creeperRingAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackDestructiveCharged();
        creeperRingAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackPowerCharged();
        creeperRingAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackAmountCharged();

        creeperCrossAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttack();
        creeperCrossAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackDestructive();
        creeperCrossAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackPower();
        creeperCrossAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackAmount();
        creeperCrossAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackExtent();
        creeperCrossAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackCharged();
        creeperCrossAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackDestructiveCharged();
        creeperCrossAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackPowerCharged();
        creeperCrossAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackAmountCharged();
        creeperCrossAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackExtentCharged();

        creeperLineAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttack();
        creeperLineAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackDestructive();
        creeperLineAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackPower();
        creeperLineAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackAmount();
        creeperLineAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackExtent();
        creeperLineAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackCharged();
        creeperLineAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackDestructiveCharged();
        creeperLineAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackPowerCharged();
        creeperLineAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackAmountCharged();
        creeperLineAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackExtentCharged();



//Add tasks
        this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.creeperRetreatDistance, true));
        this.tasks.addTask(2, 
        new EntityFestiveCreeper.EntityAIThrowTNT(this, creeperDestructive, creeperCooldownMax, 
        creeperPowerBase, creeperPowerCharged, creeperRange, creeperRangeIgnore, creeperExtraCooldownMax,

        creeperRingAttack, creeperRingAttackDestructive, creeperRingAttackPower, creeperRingAttackAmount,
        creeperRingAttackCharged, creeperRingAttackDestructiveCharged, creeperRingAttackPowerCharged, creeperRingAttackAmountCharged,

        creeperCrossAttack, creeperCrossAttackDestructive, creeperCrossAttackPower, creeperCrossAttackAmount, creeperCrossAttackExtent, 
        creeperCrossAttackCharged, creeperCrossAttackDestructiveCharged, creeperCrossAttackPowerCharged, creeperCrossAttackAmountCharged,
        creeperCrossAttackExtentCharged,

        creeperLineAttack, creeperLineAttackDestructive, creeperLineAttackPower, creeperLineAttackAmount, creeperLineAttackExtent, 
        creeperLineAttackCharged, creeperLineAttackDestructiveCharged, creeperLineAttackPowerCharged, creeperLineAttackAmountCharged,
        creeperLineAttackExtentCharged));



//Special handlers
		setCreeperPartying(false);
        specialCurrentDuration = 0;
        specialCurrentInterval = 0;
        specialInitialRadians = 69420;
        specialCurrentRadians = 69420;


//Base special configs
        festiveCreeperMixin.setCreeperSpecialEnabled(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialEnabled());
        festiveCreeperMixin.setCreeperSpecialCooldownInterrupted(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownInterrupted());
        festiveCreeperMixin.setCreeperSpecialCooldownAttacked(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownAttacked());
        festiveCreeperMixin.setCreeperSpecialCooldownFrustrated(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownFrustrated());
        festiveCreeperMixin.setCreeperSpecialCooldownOver(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownOver());
        festiveCreeperMixin.setCreeperSpecialCooldownStunned(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownStunned());
        festiveCreeperMixin.setCreeperSpecialStunnedDuration(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialStunnedDuration());
        festiveCreeperMixin.setCreeperSpecialIgnitedTimeMax(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialIgnitedTimeMax());
        festiveCreeperMixin.setCreeperSpecialInterruptedMax(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterruptedMax());
        festiveCreeperMixin.setCreeperSpecialInterruptedDamage((float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterruptedDamage());


//Special configs specific to this mob
        creeperSpecialEndTime = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialEndTime();
        creeperSpecialInterval = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterval();
        creeperSpecialRadianTurns = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialRadianTurns();

        creeperSpecialAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackDestructive();
        creeperSpecialAttackPower = (float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackPower();
        creeperSpecialAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackExtent();
        creeperSpecialAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackDestructiveCharged();
        creeperSpecialAttackPowerCharged = (float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackPowerCharged();
        creeperSpecialAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackExtentCharged();



//If task absent
        if(!TaskUtils.mobHasTask(this, EntityAICreeperSwellSpecial.class))
        {
//And task enabled in config
            if (festiveCreeperMixin.getCreeperSpecialEnabled())
            {
//Add the task
                this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
            }
        }
//If task is here...
        else
        {
//And task disabled in config
            if (!festiveCreeperMixin.getCreeperSpecialEnabled())
            {
//Remove the task
                TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
            }                
        }
	}

    protected void entityInit()
    {
        super.entityInit();
        this.getDataManager().register(IS_PARTYING, Boolean.valueOf(false));
    }


    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(6, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
    }


    public void onUpdate()
    {
        super.onUpdate();
//Get target
        EntityLivingBase festiveCreeperAttackTarget = this.getAttackTarget();

//Festive Creeper specific logic on special attack
        if(festiveCreeperMixin.getCreeperStateSpecial() > 0)
        {
//Null protection and reset special attack state if target is gone 
            if(festiveCreeperAttackTarget != null)
            {
//If attack not already executing
                if(this.getCreeperPartying() == false)
                {
//Check if it's time to do attack...
                    if(festiveCreeperMixin.getCreeperSpecialIgnitedTime() >= festiveCreeperMixin.getCreeperSpecialIgnitedTimeMax())
                    {
//Set necessary logic
                        specialCurrentInterval = creeperSpecialInterval;
                        specialInitialRadians = Math.atan2(festiveCreeperAttackTarget.posX - this.posX, festiveCreeperAttackTarget.posZ - this.posZ);
                        specialCurrentRadians = specialInitialRadians;
//Then play sound and start attack
                        this.world.playSound(null, festiveCreeperAttackTarget.posX, festiveCreeperAttackTarget.posY, festiveCreeperAttackTarget.posZ,
                        EternityModeSoundEvents.ENTITY_CREEPER_PARTY, SoundCategory.HOSTILE, 3.0F, 1.0F);
                        this.setCreeperPartying(true);
                    }
                }
//This is the special attack
                else if(this.getCreeperPartying() == true)
                {
                    --specialCurrentInterval;

                    if(specialCurrentInterval <= 0)
                    {
//Get hypotenuse to the target
                        double horizontalDistance = 
                        Math.sqrt(Math.pow(festiveCreeperAttackTarget.posX - this.posX, 2) + Math.pow(festiveCreeperAttackTarget.posZ - this.posZ, 2));

//Get distance fraction (half to full)
                        double distanceFraction = creeperSpecialAttackExtent * (double) ((float) 1 - (rand.nextFloat() / 2));
//Prepare TNT
                        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.getEntityWorld(), this.posX, this.posY, this.posZ, this,
                            this.creeperSpecialAttackDestructive, this.creeperSpecialAttackPower, 30);
                        tnt.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
//Use radians sin and cos, and adjusted distance
                        tnt.motionX = distanceFraction * Math.sin(specialCurrentRadians) * horizontalDistance / 18D;
                        tnt.motionY = (festiveCreeperAttackTarget.posY - tnt.posY) / 18D + 0.5D;
                        tnt.motionZ = distanceFraction * Math.cos(specialCurrentRadians) * horizontalDistance / 18D;
//Spawn TNT
                        this.getEntityWorld().spawnEntity(tnt);

//Adjust next throw radians (slightly randomized)
                        specialCurrentRadians += creeperSpecialRadianTurns * (double) (1.0F + (0.6F * rand.nextFloat()));
//Reset interval
                        specialCurrentInterval = creeperSpecialInterval;
                    }

//If duration over
                    specialCurrentDuration++;
//Stop attack, reset and apply cooldown
                    if(specialCurrentDuration >= creeperSpecialEndTime)
                    {
                        this.resetCreeperSpecial();
                        festiveCreeperMixin.setCreeperSpecialCooldown(festiveCreeperMixin.getCreeperSpecialCooldownOver());
                    }
                }
            }
//If no target reset state
            else
            {
                this.resetCreeperSpecial();
            }
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if(source.isExplosion() || source == DamageSource.FALL)
		{
			return false;
		}

        return super.attackEntityFrom(source, amount);
    }

    public void creeperSpecialAttemptSound(double atX, double atY, double atZ)
    {
        this.world.playSound(null, atX, atY, atZ,
        EternityModeSoundEvents.ENTITY_CREEPER_ITEMBOX, SoundCategory.HOSTILE, 3.0F, 1.0F);
    }

    public void creeperSpecialParticles()
    {
//If attack preparing
        if(!this.getCreeperPartying())
        {
//Infrequent spherical fireworks
            if(this.ticksExisted % 15 == 0)
            {
                FireworkUtil.makeFireworkEffects(this.world, this.posX, this.posY + 8.0D, this.posZ,
                    0.0D, 0.5D, 0.0D,
                    1, 
                    true, true, 1);
            }
        }
//If attack executing
        else
        {
//Frequent creeper or star fireworks
            if(this.ticksExisted % 15 == 0)
            {
                FireworkUtil.makeFireworkEffects(this.world, this.posX, this.posY + 8.0D, this.posZ,
                    0.0D, 0.5D, 0.0D,
                    1, 
                    true, true, 2 + this.rand.nextInt(2));
            }
        }
    }


	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


//Non-special configs
        compound.setDouble("CreeperRetreatDistance", this.creeperRetreatDistance);
	    compound.setBoolean("CreeperDestructive", this.creeperDestructive);
        compound.setInteger("CreeperCooldownMax", this.creeperCooldownMax);
        compound.setDouble("CreeperPowerBase", this.creeperPowerBase);
        compound.setDouble("CreeperPowerCharged", this.creeperPowerCharged);
        compound.setDouble("CreeperRange", this.creeperRange);
	    compound.setBoolean("CreeperRangeIgnore", this.creeperRangeIgnore);

        compound.setInteger("CreeperExtraCooldownMax", this.creeperExtraCooldownMax);

	    compound.setBoolean("CreeperRingAttack", this.creeperRingAttack);
	    compound.setBoolean("CreeperRingAttackDestructive", this.creeperRingAttackDestructive);
	    compound.setDouble("CreeperRingAttackPower", this.creeperRingAttackPower);
	    compound.setInteger("CreeperRingAttackAmount", this.creeperRingAttackAmount);
	    compound.setBoolean("CreeperRingAttackCharged", this.creeperRingAttackCharged);
	    compound.setBoolean("CreeperRingAttackDestructiveCharged", this.creeperRingAttackDestructiveCharged);
	    compound.setDouble("CreeperRingAttackPowerCharged", this.creeperRingAttackPowerCharged);
	    compound.setInteger("CreeperRingAttackAmountCharged", this.creeperRingAttackAmountCharged);

	    compound.setBoolean("CreeperCrossAttack", this.creeperCrossAttack);
	    compound.setBoolean("CreeperCrossAttackDestructive", this.creeperCrossAttackDestructive);
	    compound.setDouble("CreeperCrossAttackPower", this.creeperCrossAttackPower);
	    compound.setInteger("CreeperCrossAttackAmount", this.creeperCrossAttackAmount);
	    compound.setDouble("CreeperCrossAttackExtent", this.creeperCrossAttackExtent);
	    compound.setBoolean("CreeperCrossAttackCharged", this.creeperCrossAttackCharged);
	    compound.setBoolean("CreeperCrossAttackDestructiveCharged", this.creeperCrossAttackDestructiveCharged);
	    compound.setDouble("CreeperCrossAttackPowerCharged", this.creeperCrossAttackPowerCharged);
	    compound.setInteger("CreeperCrossAttackAmountCharged", this.creeperCrossAttackAmountCharged);
	    compound.setDouble("CreeperCrossAttackExtentCharged", this.creeperCrossAttackExtentCharged);

	    compound.setBoolean("CreeperLineAttack", this.creeperLineAttack);
	    compound.setBoolean("CreeperLineAttackDestructive", this.creeperLineAttackDestructive);
	    compound.setDouble("CreeperLineAttackPower", this.creeperLineAttackPower);
	    compound.setInteger("CreeperLineAttackAmount", this.creeperLineAttackAmount);
	    compound.setDouble("CreeperLineAttackExtent", this.creeperLineAttackExtent);
	    compound.setBoolean("CreeperLineAttackCharged", this.creeperLineAttackCharged);
	    compound.setBoolean("CreeperLineAttackDestructiveCharged", this.creeperLineAttackDestructiveCharged);
	    compound.setDouble("CreeperLineAttackPowerCharged", this.creeperLineAttackPowerCharged);
	    compound.setInteger("CreeperLineAttackAmountCharged", this.creeperLineAttackAmountCharged);
	    compound.setDouble("CreeperLineAttackExtentCharged", this.creeperLineAttackExtentCharged);



//Special handlers
        compound.setBoolean("CreeperPartying", this.getCreeperPartying());
	    compound.setInteger("CreeperSpecialCurrentDuration", this.specialCurrentDuration);
	    compound.setInteger("CreeperSpecialCurrentInterval", this.specialCurrentInterval);
	    compound.setDouble("CreeperSpecialInitialRadians", this.specialInitialRadians);
	    compound.setDouble("CreeperSpecialCurrentRadians", this.specialCurrentRadians);



//Special configs specific to this mob
	    compound.setInteger("CreeperSpecialEndTime", this.creeperSpecialEndTime);
	    compound.setInteger("CreeperSpecialInterval", this.creeperSpecialInterval);
	    compound.setDouble("CreeperSpecialRadianTurns", this.creeperSpecialRadianTurns);
	    compound.setBoolean("CreeperSpecialAttackDestructive", this.creeperSpecialAttackDestructive);
	    compound.setDouble("CreeperSpecialAttackPower", (double) this.creeperSpecialAttackPower);
	    compound.setDouble("CreeperSpecialAttackExtent", this.creeperSpecialAttackExtent);
	    compound.setBoolean("CreeperSpecialAttackDestructiveCharged", this.creeperSpecialAttackDestructiveCharged);
	    compound.setDouble("CreeperSpecialAttackPowerCharged", (double) this.creeperSpecialAttackPowerCharged);
	    compound.setDouble("CreeperSpecialAttackExtentCharged", this.creeperSpecialAttackExtentCharged);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("CreeperRetreatDistance")) { this.creeperRetreatDistance = compound.getDouble("CreeperRetreatDistance"); }
        if (compound.hasKey("CreeperDestructive")) { this.creeperDestructive = compound.getBoolean("CreeperDestructive"); }
        if (compound.hasKey("CreeperCooldownMax")) { this.creeperCooldownMax = compound.getInteger("CreeperCooldownMax"); }
        if (compound.hasKey("CreeperPowerBase")) { this.creeperPowerBase = compound.getDouble("CreeperPowerBase"); }
        if (compound.hasKey("CreeperPowerCharged")) { this.creeperPowerCharged = compound.getDouble("CreeperPowerCharged"); }
        if (compound.hasKey("CreeperRange")) { this.creeperRange = compound.getDouble("CreeperRange"); }
        if (compound.hasKey("CreeperRangeIgnore")) { this.creeperRangeIgnore = compound.getBoolean("CreeperRangeIgnore"); }

        if (compound.hasKey("CreeperExtraCooldownMax")) { this.creeperExtraCooldownMax = compound.getInteger("CreeperExtraCooldownMax"); }

        if (compound.hasKey("CreeperRingAttack")) { this.creeperRingAttack = compound.getBoolean("CreeperRingAttack"); }
        if (compound.hasKey("CreeperRingAttackDestructive")) { this.creeperRingAttackDestructive = compound.getBoolean("CreeperRingAttackDestructive"); }
        if (compound.hasKey("CreeperRingAttackPower")) { this.creeperRingAttackPower = compound.getDouble("CreeperRingAttackPower"); }
        if (compound.hasKey("CreeperRingAttackAmount")) { this.creeperRingAttackAmount = compound.getInteger("CreeperRingAttackAmount"); }
        if (compound.hasKey("CreeperRingAttackCharged")) { this.creeperRingAttackCharged = compound.getBoolean("CreeperRingAttackCharged"); }
        if (compound.hasKey("CreeperRingAttackDestructiveCharged")) { this.creeperRingAttackDestructiveCharged 
            = compound.getBoolean("CreeperRingAttackDestructiveCharged"); }
        if (compound.hasKey("CreeperRingAttackPowerCharged")) { this.creeperRingAttackPowerCharged 
            = compound.getDouble("CreeperRingAttackPowerCharged"); }
        if (compound.hasKey("CreeperRingAttackAmountCharged")) { this.creeperRingAttackAmountCharged 
            = compound.getInteger("CreeperRingAttackAmountCharged"); }

        if (compound.hasKey("CreeperCrossAttack")) { this.creeperCrossAttack = compound.getBoolean("CreeperCrossAttack"); }
        if (compound.hasKey("CreeperCrossAttackDestructive")) { this.creeperCrossAttackDestructive = compound.getBoolean("CreeperCrossAttackDestructive"); }
        if (compound.hasKey("CreeperCrossAttackPower")) { this.creeperCrossAttackPower = compound.getDouble("CreeperCrossAttackPower"); }
        if (compound.hasKey("CreeperCrossAttackAmount")) { this.creeperCrossAttackAmount = compound.getInteger("CreeperCrossAttackAmount"); }
        if (compound.hasKey("CreeperCrossAttackExtent")) { this.creeperCrossAttackExtent = compound.getDouble("CreeperCrossAttackExtent"); }
        if (compound.hasKey("CreeperCrossAttackCharged")) { this.creeperCrossAttackCharged = compound.getBoolean("CreeperCrossAttackCharged"); }
        if (compound.hasKey("CreeperCrossAttackDestructiveCharged")) { this.creeperCrossAttackDestructiveCharged 
            = compound.getBoolean("CreeperCrossAttackDestructiveCharged"); }
        if (compound.hasKey("CreeperCrossAttackPowerCharged")) { this.creeperCrossAttackPowerCharged 
            = compound.getDouble("CreeperCrossAttackPowerCharged"); }
        if (compound.hasKey("CreeperCrossAttackAmountCharged")) { this.creeperCrossAttackAmountCharged 
            = compound.getInteger("CreeperCrossAttackAmountCharged"); }
        if (compound.hasKey("CreeperCrossAttackExtentCharged")) { this.creeperCrossAttackExtentCharged 
            = compound.getDouble("CreeperCrossAttackExtentCharged"); }

        if (compound.hasKey("CreeperLineAttack")) { this.creeperLineAttack = compound.getBoolean("CreeperLineAttack"); }
        if (compound.hasKey("CreeperLineAttackDestructive")) { this.creeperLineAttackDestructive = compound.getBoolean("CreeperLineAttackDestructive"); }
        if (compound.hasKey("CreeperLineAttackPower")) { this.creeperLineAttackPower = compound.getDouble("CreeperLineAttackPower"); }
        if (compound.hasKey("CreeperLineAttackAmount")) { this.creeperLineAttackAmount = compound.getInteger("CreeperLineAttackAmount"); }
        if (compound.hasKey("CreeperLineAttackExtent")) { this.creeperLineAttackExtent = compound.getDouble("CreeperLineAttackExtent"); }
        if (compound.hasKey("CreeperLineAttackCharged")) { this.creeperLineAttackCharged = compound.getBoolean("CreeperLineAttackCharged"); }
        if (compound.hasKey("CreeperLineAttackDestructiveCharged")) { this.creeperLineAttackDestructiveCharged 
            = compound.getBoolean("CreeperLineAttackDestructiveCharged"); }
        if (compound.hasKey("CreeperLineAttackPowerCharged")) { this.creeperLineAttackPowerCharged 
            = compound.getDouble("CreeperLineAttackPowerCharged"); }
        if (compound.hasKey("CreeperLineAttackAmountCharged")) { this.creeperLineAttackAmountCharged 
            = compound.getInteger("CreeperLineAttackAmountCharged"); }
        if (compound.hasKey("CreeperLineAttackExtentCharged")) { this.creeperLineAttackExtentCharged 
            = compound.getDouble("CreeperLineAttackExtentCharged"); }



//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIBackOffFromEntity.class))
        {
            this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.creeperRetreatDistance, true));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBackOffFromEntity.class);

            this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.creeperRetreatDistance, true));
        }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIThrowTNT.class))
        {
            this.tasks.addTask(2, 
            new EntityFestiveCreeper.EntityAIThrowTNT(this, creeperDestructive, creeperCooldownMax, 
            creeperPowerBase, creeperPowerCharged, creeperRange, creeperRangeIgnore, creeperExtraCooldownMax,

            creeperRingAttack, creeperRingAttackDestructive, creeperRingAttackPower, creeperRingAttackAmount,
            creeperRingAttackCharged, creeperRingAttackDestructiveCharged, creeperRingAttackPowerCharged, creeperRingAttackAmountCharged,

            creeperCrossAttack, creeperCrossAttackDestructive, creeperCrossAttackPower, creeperCrossAttackAmount, creeperCrossAttackExtent, 
            creeperCrossAttackCharged, creeperCrossAttackDestructiveCharged, creeperCrossAttackPowerCharged, creeperCrossAttackAmountCharged,
            creeperCrossAttackExtentCharged,

            creeperLineAttack, creeperLineAttackDestructive, creeperLineAttackPower, creeperLineAttackAmount, creeperLineAttackExtent, 
            creeperLineAttackCharged, creeperLineAttackDestructiveCharged, creeperLineAttackPowerCharged, creeperLineAttackAmountCharged,
            creeperLineAttackExtentCharged));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIThrowTNT.class);

            this.tasks.addTask(2, 
            new EntityFestiveCreeper.EntityAIThrowTNT(this, creeperDestructive, creeperCooldownMax, 
            creeperPowerBase, creeperPowerCharged, creeperRange, creeperRangeIgnore, creeperExtraCooldownMax,

            creeperRingAttack, creeperRingAttackDestructive, creeperRingAttackPower, creeperRingAttackAmount,
            creeperRingAttackCharged, creeperRingAttackDestructiveCharged, creeperRingAttackPowerCharged, creeperRingAttackAmountCharged,

            creeperCrossAttack, creeperCrossAttackDestructive, creeperCrossAttackPower, creeperCrossAttackAmount, creeperCrossAttackExtent, 
            creeperCrossAttackCharged, creeperCrossAttackDestructiveCharged, creeperCrossAttackPowerCharged, creeperCrossAttackAmountCharged,
            creeperCrossAttackExtentCharged,

            creeperLineAttack, creeperLineAttackDestructive, creeperLineAttackPower, creeperLineAttackAmount, creeperLineAttackExtent, 
            creeperLineAttackCharged, creeperLineAttackDestructiveCharged, creeperLineAttackPowerCharged, creeperLineAttackAmountCharged,
            creeperLineAttackExtentCharged));
        }


//Special handlers
        if (compound.hasKey("CreeperPartying")) { this.setCreeperPartying(compound.getBoolean("CreeperPartying")); }
        if (compound.hasKey("CreeperSpecialCurrentDuration")) { this.specialCurrentDuration = compound.getInteger("CreeperSpecialCurrentDuration"); }
        if (compound.hasKey("CreeperSpecialCurrentInterval")) { this.specialCurrentInterval = compound.getInteger("CreeperSpecialCurrentInterval"); }
        if (compound.hasKey("CreeperSpecialInitialRadians")) { this.specialInitialRadians = compound.getDouble("CreeperSpecialInitialRadians"); }
        if (compound.hasKey("CreeperSpecialCurrentRadians")) { this.specialCurrentRadians = compound.getDouble("CreeperSpecialCurrentRadians"); }


//Special configs specific to this mob
        if (compound.hasKey("CreeperSpecialEndTime")) { this.creeperSpecialEndTime = compound.getInteger("CreeperSpecialEndTime"); }
        if (compound.hasKey("CreeperSpecialInterval")) { this.creeperSpecialInterval = compound.getInteger("CreeperSpecialInterval"); }
        if (compound.hasKey("CreeperSpecialRadianTurns")) { this.creeperSpecialRadianTurns = compound.getDouble("CreeperSpecialRadianTurns"); }
        if (compound.hasKey("CreeperSpecialAttackDestructive")) 
            { this.creeperSpecialAttackDestructive = compound.getBoolean("CreeperSpecialAttackDestructive"); }
        if (compound.hasKey("CreeperSpecialAttackPower")) { this.creeperSpecialAttackPower = (float) compound.getDouble("CreeperSpecialAttackPower"); }
        if (compound.hasKey("CreeperSpecialAttackExtent")) { this.creeperSpecialAttackExtent = compound.getDouble("CreeperSpecialAttackExtent"); }
        if (compound.hasKey("CreeperSpecialAttackDestructiveCharged")) 
            { this.creeperSpecialAttackDestructiveCharged = compound.getBoolean("CreeperSpecialAttackDestructiveCharged"); }
        if (compound.hasKey("CreeperSpecialAttackPowerCharged")) 
            { this.creeperSpecialAttackPowerCharged = (float) compound.getDouble("CreeperSpecialAttackPowerCharged"); }
        if (compound.hasKey("CreeperSpecialAttackExtentCharged")) 
            { this.creeperSpecialAttackExtentCharged = compound.getDouble("CreeperSpecialAttackExtentCharged"); }



//Remove task if present, and 
        TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
        if (festiveCreeperMixin.getCreeperSpecialEnabled()) 
        {
            this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
        }
    }
    


    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
    }
    


    public class EntityAIThrowTNT extends EntityAIBase
    {
    	EntityFestiveCreeper creeper;
        boolean tntDestructive;
        int tntAttackCooldownMax;
    	float tntPower;
    	float tntPowerCharged;
        double tntRange;
        boolean tntRangeIgnore;
        int tntAttackExtraCooldownMax;

        boolean tntRingAttack;
        boolean tntRingAttackDestructive;
        float tntRingAttackPower;
        int tntRingAttackAmount;
        boolean tntRingAttackCharged;
        boolean tntRingAttackDestructiveCharged;
        float tntRingAttackPowerCharged;
        int tntRingAttackAmountCharged;

        boolean tntCrossAttack;
        boolean tntCrossAttackDestructive;
        float tntCrossAttackPower;
        int tntCrossAttackAmount;
        double tntCrossAttackExtent;
        boolean tntCrossAttackCharged;
        boolean tntCrossAttackDestructiveCharged;
        float tntCrossAttackPowerCharged;
        int tntCrossAttackAmountCharged;
        double tntCrossAttackExtentCharged;

        boolean tntLineAttack;
        boolean tntLineAttackDestructive;
        float tntLineAttackPower;
        int tntLineAttackAmount;
        double tntLineAttackExtent;
        boolean tntLineAttackCharged;
        boolean tntLineAttackDestructiveCharged;
        float tntLineAttackPowerCharged;
        int tntLineAttackAmountCharged;
        double tntLineAttackExtentCharged;

        ArrayList<String> tntAttackPossible = new ArrayList<>();
        ArrayList<String> tntAttackPossibleCharged = new ArrayList<>();

        boolean creeperPoweredChecked;
    	EntityLivingBase target;
    	int tntAttackCooldown;
        int tntAttackExtraCooldown;
    	

        public EntityAIThrowTNT(EntityFestiveCreeper theCreeper, boolean destructive, 
        int attackCooldownMax, double power, double powerCharged, double range, boolean rangeIgnore, int attackExtraCooldownMax,
        boolean ringAttack, boolean ringAttackDestructive, double ringAttackPower, int ringAttackAmount,
        boolean ringAttackCharged, boolean ringAttackDestructiveCharged, double ringAttackPowerCharged, int ringAttackAmountCharged,
        boolean crossAttack, boolean crossAttackDestructive, double crossAttackPower, int crossAttackAmount, double crossAttackExtent, 
        boolean crossAttackCharged, boolean crossAttackDestructiveCharged, double crossAttackPowerCharged, int crossAttackAmountCharged, double crossAttackExtentCharged,
        boolean lineAttack, boolean lineAttackDestructive, double lineAttackPower, int lineAttackAmount, double lineAttackExtent, 
        boolean lineAttackCharged, boolean lineAttackDestructiveCharged, double lineAttackPowerCharged, int lineAttackAmountCharged, double lineAttackExtentCharged) {

        	creeper = theCreeper;
            tntDestructive = destructive;
            tntAttackCooldownMax = attackCooldownMax;
        	tntPower = (float) power;
        	tntPowerCharged = (float) powerCharged;
            tntRange = range;
            tntRangeIgnore = rangeIgnore;
            tntAttackExtraCooldownMax = attackExtraCooldownMax;

            tntRingAttack = ringAttack;
            tntRingAttackDestructive = ringAttackDestructive;
            tntRingAttackPower = (float) ringAttackPower;
            tntRingAttackAmount = ringAttackAmount;
            tntRingAttackCharged = ringAttackCharged;
            tntRingAttackDestructiveCharged = ringAttackDestructiveCharged;
            tntRingAttackPowerCharged = (float) ringAttackPowerCharged;
            tntRingAttackAmountCharged = ringAttackAmountCharged;

            tntCrossAttack = crossAttack;
            tntCrossAttackDestructive = crossAttackDestructive;
            tntCrossAttackPower = (float) crossAttackPower;
            tntCrossAttackAmount = crossAttackAmount;
            tntCrossAttackExtent = crossAttackExtent;
            tntCrossAttackCharged = crossAttackCharged;
            tntCrossAttackDestructiveCharged = crossAttackDestructiveCharged;
            tntCrossAttackPowerCharged = (float) crossAttackPowerCharged;
            tntCrossAttackAmountCharged = crossAttackAmountCharged;
            tntCrossAttackExtentCharged = crossAttackExtentCharged;

            tntLineAttack = lineAttack;
            tntLineAttackDestructive = lineAttackDestructive;
            tntLineAttackPower = (float) lineAttackPower;
            tntLineAttackAmount = lineAttackAmount;
            tntLineAttackExtent = lineAttackExtent;
            tntLineAttackCharged = lineAttackCharged;
            tntLineAttackDestructiveCharged = lineAttackDestructiveCharged;
            tntLineAttackPowerCharged = (float) lineAttackPowerCharged;
            tntLineAttackAmountCharged = lineAttackAmountCharged;
            tntLineAttackExtentCharged = lineAttackExtentCharged;

            if(tntRingAttack) { tntAttackPossible.add("ring"); }
            if(tntRingAttackCharged) { tntAttackPossibleCharged.add("ring"); }

            if(tntCrossAttack) { tntAttackPossible.add("cross"); }
            if(tntCrossAttackCharged) { tntAttackPossibleCharged.add("cross"); }

            if(tntLineAttack) { tntAttackPossible.add("line"); }
            if(tntLineAttackCharged) { tntAttackPossibleCharged.add("line"); }

            creeperPoweredChecked = false;
    		tntAttackCooldown = 0;
//Lower initial cooldown for extra attacks
            tntAttackExtraCooldown = (tntAttackExtraCooldownMax / 2);
		}

		/**
		* Returns whether the EntityAIBase should begin execution.
		*/
		public boolean shouldExecute()
		{

            if(festiveCreeperMixin.getCreeperStateSpecial() > 0 && !this.creeper.getCreeperPartying())
            {
                return false;
            }
        
	        target = this.creeper.getAttackTarget();

	        if (target == null)
	        {
	            return false;
	        }
	        else if (!target.isEntityAlive())
	        {
	            return false;
	        }
	        else
	        {
//Another example of short circuit
//being useful, this time to save some computation.
//Condition for this one is for the festive creeper to have sight and range to the target
	        	if(this.creeper.canEntityBeSeen(target) && 
                (this.tntRangeIgnore || 
                (this.creeper.getDistance(target) > 2.0D && this.creeper.getDistanceSq(target) < Math.pow(this.tntRange, 2))))
	        	{
	        		return true;
	        	}
	        	
	        	return false;
	        }
		}
		
		/**
	    * Returns whether an in-progress EntityAIBase should continue executing
		*/
		public boolean continueExecuting()
	    {
			return shouldExecute();
	    }

	    /**
	     * Resets the task
	     */
	    public void resetTask()
	    {
	    	target = null;
	    	tntAttackCooldown = 0;
//Won't instantly do ring attack
            tntAttackExtraCooldown = (tntAttackExtraCooldownMax / 2);
	    }
	    
	    /**
	     * Updates the task
	     */
	    public void updateTask()
	    {
	    	if(!this.creeperPoweredChecked && this.creeper.getPowered())
            { 
            	tntPower = tntPowerCharged;

                tntRingAttack = tntRingAttackCharged;
                tntRingAttackDestructive = tntRingAttackDestructiveCharged;
                tntRingAttackPower = tntRingAttackPowerCharged;
                tntRingAttackAmount = tntRingAttackAmountCharged;

                tntCrossAttack = tntCrossAttackCharged;
                tntCrossAttackDestructive = tntCrossAttackDestructiveCharged;
                tntCrossAttackPower = tntCrossAttackPowerCharged;
                tntCrossAttackAmount = tntCrossAttackAmountCharged;
                tntCrossAttackExtent = tntCrossAttackExtentCharged;

                tntLineAttack = tntLineAttackCharged;
                tntLineAttackDestructive = tntLineAttackDestructiveCharged;
                tntLineAttackPower = tntLineAttackPowerCharged;
                tntLineAttackAmount = tntLineAttackAmountCharged;
                tntLineAttackExtent = tntLineAttackExtentCharged;

                tntAttackPossible = tntAttackPossibleCharged;

                this.creeperPoweredChecked = true;
            }
	    	
	    	if(target != null)
	    	{
//Regular old attack
                if(--tntAttackCooldown <= 0)    
                {                
                    if(!getEntityWorld().isRemote) 
                    {
    			        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.creeper.getEntityWorld(), creeper.posX, creeper.posY, creeper.posZ, this.creeper, this.tntDestructive, this.tntPower, 30);
    			        tnt.setLocationAndAngles(this.creeper.posX, this.creeper.posY, this.creeper.posZ, this.creeper.rotationYaw, 0.0F);
    			        tnt.motionX = (this.target.posX - tnt.posX) / 18D;
    			        tnt.motionY = (this.target.posY - tnt.posY) / 18D + 0.5D;
    			        tnt.motionZ = (this.target.posZ - creeper.posZ) / 18D;
    			        this.creeper.getEntityWorld().spawnEntity(tnt);
    		        }
    		        this.creeper.playSound(SoundEvents.ENTITY_TNT_PRIMED, this.creeper.getSoundVolume(), this.creeper.getSoundPitch());
    		        tntAttackCooldown = tntAttackCooldownMax;
                }
//If TNT extra decremented cooldown is 0
                if(--tntAttackExtraCooldown <= 0) 
                {
//And server side
                    if(!getEntityWorld().isRemote) 
                    {
//If possible extra attacks list not empty
                        if(!tntAttackPossible.isEmpty())
                        {
//Do random one
                            int randomIndex = ThreadLocalRandom.current().nextInt(tntAttackPossible.size());
                            String attackToDo = tntAttackPossible.get(randomIndex);

                            switch (attackToDo) 
                            {
                                case "ring":
                                    this.extraAttackRing();
                                    break; 
                                case "cross":
                                    this.extraAttackCross();
                                    break; 
                                case "line":
                                    this.extraAttackLine();
                                    break; 
                            }
                        }
                    }
	    		    this.creeper.playSound(SoundEvents.ENTITY_TNT_PRIMED, this.creeper.getSoundVolume(), this.creeper.getSoundPitch());
                    tntAttackExtraCooldown = tntAttackExtraCooldownMax;
                } 
	    	}
	    }

        public void extraAttackRing()
        {
//Get hypotenuse to the target
            double horizontalDistance = Math.sqrt(Math.pow(this.target.posX - creeper.posX, 2) + Math.pow(this.target.posZ - creeper.posZ, 2));
//TNTs at the target's horizontal distance for each angle
            int angles = this.tntRingAttackAmount;
            float angleVal = (360 / angles);
//For each angle
            for(int angleAt = 0; angleAt < angles; angleAt++) 
            {
//Prepare TNT
		        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.creeper.getEntityWorld(), creeper.posX, creeper.posY, creeper.posZ, this.creeper, this.tntRingAttackDestructive, this.tntRingAttackPower, 30);
		        tnt.setLocationAndAngles(this.creeper.posX, this.creeper.posY, this.creeper.posZ, this.creeper.rotationYaw, 0.0F);
//Get radians from angle
                double radians = Math.toRadians(angleVal * angleAt);
//Use cos and sine to rotate horizontal aim vectors
		        tnt.motionX = (horizontalDistance * Math.sin(radians)) / 18D;
		        tnt.motionY = (this.target.posY - tnt.posY) / 18D + 0.5D;
		        tnt.motionZ = (horizontalDistance * Math.cos(radians)) / 18D;
//Spawn TNT
		        this.creeper.getEntityWorld().spawnEntity(tnt);
            }  
        }

        public void extraAttackCross()
        {
//Get hypotenuse to the target
            double horizontalDistance = Math.sqrt(Math.pow(this.target.posX - creeper.posX, 2) + Math.pow(this.target.posZ - creeper.posZ, 2));
//Get radians of base direction
            double baseRadians = Math.atan2(this.target.posX - this.creeper.posX, this.target.posZ - this.creeper.posZ);
//4 directions
            for(int directionAt = 1; directionAt <= 4; directionAt++) 
            {
//Get radians of this specific direction
                double radians = baseRadians + (Math.PI / 2 * (directionAt - 1));
//Will shoot TNT at farthest extent then iterate backwards
                for(int amountAtBackwards = tntCrossAttackAmount; amountAtBackwards > 0; amountAtBackwards--)
                {
//Get distance fraction (can be more than 1)
                    double distanceFraction = tntCrossAttackExtent * ((double) amountAtBackwards / (double) tntCrossAttackAmount); 
//Prepare TNT
			        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.creeper.getEntityWorld(), creeper.posX, creeper.posY, creeper.posZ, this.creeper, this.tntCrossAttackDestructive, this.tntCrossAttackPower, 30);
			        tnt.setLocationAndAngles(this.creeper.posX, this.creeper.posY, this.creeper.posZ, this.creeper.rotationYaw, 0.0F);
//Use radians sin and cos, and adjusted distance
			        tnt.motionX = distanceFraction * Math.sin(radians) * horizontalDistance / 18D;
			        tnt.motionY = (this.target.posY - tnt.posY) / 18D + 0.5D;
			        tnt.motionZ = distanceFraction * Math.cos(radians) * horizontalDistance / 18D;
//Spawn TNT
		            this.creeper.getEntityWorld().spawnEntity(tnt);
                }
            }  
        }

        public void extraAttackLine()
        {       
//Will shoot TNT at farthest extent then iterate backwards
            for(int amountAtBackwards = tntLineAttackAmount; amountAtBackwards > 0; amountAtBackwards--)
            {
//Get distance fraction (can be more than 1)
                double distanceFraction = tntLineAttackExtent * ((double) amountAtBackwards / (double) tntLineAttackAmount); 
//Prepare TNT
		        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.creeper.getEntityWorld(), creeper.posX, creeper.posY, creeper.posZ, this.creeper, this.tntLineAttackDestructive, this.tntLineAttackPower, 30);
		        tnt.setLocationAndAngles(this.creeper.posX, this.creeper.posY, this.creeper.posZ, this.creeper.rotationYaw, 0.0F);
		        tnt.motionX = distanceFraction * (this.target.posX - tnt.posX) / 18D;
		        tnt.motionY = (this.target.posY - tnt.posY) / 18D + 0.5D;
		        tnt.motionZ = distanceFraction * (this.target.posZ - creeper.posZ) / 18D;
//Spawn TNT
		        this.creeper.getEntityWorld().spawnEntity(tnt);
            }  
        }
    }


    public boolean getCreeperPartying()
    {
        return ((Boolean)this.dataManager.get(IS_PARTYING)).booleanValue();
    }

    public void setCreeperPartying(boolean partying)
    {
        this.getDataManager().set(IS_PARTYING, Boolean.valueOf(partying));
    }


//Reset all special attack logic except cooldown
    public void resetCreeperSpecial()
    {
        festiveCreeperMixin.setCreeperSpecialIgnitedTime(0);
        festiveCreeperMixin.setCreeperStateSpecial(-1);
        this.setCreeperPartying(false);

        specialCurrentDuration = 0;
        specialCurrentInterval = 0;
        specialInitialRadians = 69420;
        specialCurrentRadians = 69420;
    }
    

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_FESTIVECREEPER;
    }
    
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	if(type == EnumCreatureType.MONSTER){return false;}
    	return super.isCreatureType(type, forSpawnCount);
    }
}
