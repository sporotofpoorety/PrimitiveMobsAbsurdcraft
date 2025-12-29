package net.daveyx0.primitivemobs.entity.monster;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.entity.ai.EntityAIBackOffFromEntity;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellSpecial;
import net.daveyx0.primitivemobs.entity.item.EntityPrimitiveTNTPrimed;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class EntityFestiveCreeper extends EntityPrimitiveCreeper implements IMultiMob {

	protected double festiveCreeperRetreatDistance;

	protected boolean festiveCreeperDestructive;
    protected int festiveCreeperCooldownMax;
	protected double festiveCreeperPowerBase;
	protected double festiveCreeperPowerCharged;
    protected double festiveCreeperRange;
	protected boolean festiveCreeperRangeIgnore;
    protected int festiveCreeperExtraCooldownMax;

    protected boolean festiveCreeperRingAttack;
    protected boolean festiveCreeperRingAttackDestructive;
    protected double festiveCreeperRingAttackPower;
    protected int festiveCreeperRingAttackAmount;
    protected boolean festiveCreeperRingAttackCharged;
    protected boolean festiveCreeperRingAttackDestructiveCharged;
    protected double festiveCreeperRingAttackPowerCharged;
    protected int festiveCreeperRingAttackAmountCharged;

    protected boolean festiveCreeperCrossAttack;
    protected boolean festiveCreeperCrossAttackDestructive;
    protected double festiveCreeperCrossAttackPower;
    protected int festiveCreeperCrossAttackAmount;
    protected double festiveCreeperCrossAttackExtent;
    protected boolean festiveCreeperCrossAttackCharged;
    protected boolean festiveCreeperCrossAttackDestructiveCharged;
    protected double festiveCreeperCrossAttackPowerCharged;
    protected int festiveCreeperCrossAttackAmountCharged;
    protected double festiveCreeperCrossAttackExtentCharged;

    protected boolean festiveCreeperLineAttack;
    protected boolean festiveCreeperLineAttackDestructive;
    protected double festiveCreeperLineAttackPower;
    protected int festiveCreeperLineAttackAmount;
    protected double festiveCreeperLineAttackExtent;
    protected boolean festiveCreeperLineAttackCharged;
    protected boolean festiveCreeperLineAttackDestructiveCharged;
    protected double festiveCreeperLineAttackPowerCharged;
    protected int festiveCreeperLineAttackAmountCharged;
    protected double festiveCreeperLineAttackExtentCharged;

//Access getters and setters of EntityCreeper mixin
    public IMixinEntityCreeper festiveCreeperMixin;

//Festive Creeper specific data parameters
    private static final DataParameter<Boolean> IS_PARTYING = EntityDataManager.<Boolean>createKey(EntityFestiveCreeper.class, DataSerializers.BOOLEAN);

//Purely state logic
    int specialCurrentDuration;
    int specialCurrentInterval;
    double specialInitialRadians;
    double specialCurrentRadians;

//Festive Creeper special attack specific
    boolean festiveCreeperSpecialEnabled;
    int festiveCreeperSpecialEndTime;
    int festiveCreeperSpecialInterval;
    double festiveCreeperSpecialRadianTurns;
    int festiveCreeperSpecialCooldownOver;

    boolean festiveCreeperSpecialAttackDestructive;
    float festiveCreeperSpecialAttackPower;
    double festiveCreeperSpecialAttackExtent;
    boolean festiveCreeperSpecialAttackDestructiveCharged;
    float festiveCreeperSpecialAttackPowerCharged;
    double festiveCreeperSpecialAttackExtentCharged;



//Constructor
	public EntityFestiveCreeper(World worldIn) {
		super(worldIn);
		isImmuneToFire = true;

        festiveCreeperRetreatDistance = PrimitiveMobsConfigSpecial.getFestiveCreeperRetreatDistance();

	    festiveCreeperDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperDestructive();
	    festiveCreeperCooldownMax = PrimitiveMobsConfigSpecial.getFestiveCreeperCooldownMax();
	    festiveCreeperPowerBase = PrimitiveMobsConfigSpecial.getFestiveCreeperPowerBase();
	    festiveCreeperPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperPowerCharged();
        festiveCreeperRange = PrimitiveMobsConfigSpecial.getFestiveCreeperRange();
	    festiveCreeperRangeIgnore = PrimitiveMobsConfigSpecial.getFestiveCreeperRangeIgnore();
	    festiveCreeperExtraCooldownMax = PrimitiveMobsConfigSpecial.getFestiveCreeperExtraCooldownMax();

        festiveCreeperRingAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttack();
        festiveCreeperRingAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackDestructive();
        festiveCreeperRingAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackPower();
        festiveCreeperRingAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackAmount();
        festiveCreeperRingAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackCharged();
        festiveCreeperRingAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackDestructiveCharged();
        festiveCreeperRingAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackPowerCharged();
        festiveCreeperRingAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperRingAttackAmountCharged();

        festiveCreeperCrossAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttack();
        festiveCreeperCrossAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackDestructive();
        festiveCreeperCrossAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackPower();
        festiveCreeperCrossAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackAmount();
        festiveCreeperCrossAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackExtent();
        festiveCreeperCrossAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackCharged();
        festiveCreeperCrossAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackDestructiveCharged();
        festiveCreeperCrossAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackPowerCharged();
        festiveCreeperCrossAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackAmountCharged();
        festiveCreeperCrossAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperCrossAttackExtentCharged();

        festiveCreeperLineAttack = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttack();
        festiveCreeperLineAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackDestructive();
        festiveCreeperLineAttackPower = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackPower();
        festiveCreeperLineAttackAmount = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackAmount();
        festiveCreeperLineAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackExtent();
        festiveCreeperLineAttackCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackCharged();
        festiveCreeperLineAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackDestructiveCharged();
        festiveCreeperLineAttackPowerCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackPowerCharged();
        festiveCreeperLineAttackAmountCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackAmountCharged();
        festiveCreeperLineAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperLineAttackExtentCharged();

        this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.festiveCreeperRetreatDistance, true));
        this.tasks.addTask(2, 
        new EntityFestiveCreeper.EntityAIThrowTNT(this, festiveCreeperDestructive, festiveCreeperCooldownMax, 
        festiveCreeperPowerBase, festiveCreeperPowerCharged, festiveCreeperRange, festiveCreeperRangeIgnore, festiveCreeperExtraCooldownMax,

        festiveCreeperRingAttack, festiveCreeperRingAttackDestructive, festiveCreeperRingAttackPower, festiveCreeperRingAttackAmount,
        festiveCreeperRingAttackCharged, festiveCreeperRingAttackDestructiveCharged, festiveCreeperRingAttackPowerCharged, festiveCreeperRingAttackAmountCharged,

        festiveCreeperCrossAttack, festiveCreeperCrossAttackDestructive, festiveCreeperCrossAttackPower, festiveCreeperCrossAttackAmount, festiveCreeperCrossAttackExtent, 
        festiveCreeperCrossAttackCharged, festiveCreeperCrossAttackDestructiveCharged, festiveCreeperCrossAttackPowerCharged, festiveCreeperCrossAttackAmountCharged,
        festiveCreeperCrossAttackExtentCharged,

        festiveCreeperLineAttack, festiveCreeperLineAttackDestructive, festiveCreeperLineAttackPower, festiveCreeperLineAttackAmount, festiveCreeperLineAttackExtent, 
        festiveCreeperLineAttackCharged, festiveCreeperLineAttackDestructiveCharged, festiveCreeperLineAttackPowerCharged, festiveCreeperLineAttackAmountCharged,
        festiveCreeperLineAttackExtentCharged));



//Festive Creeper specific data parameters
		setCreeperPartying(false);

//Purely state logic
        specialCurrentDuration = 0;
        specialCurrentInterval = 0;
        specialInitialRadians = 69420;
        specialCurrentRadians = 69420;


//Access getters and setters of EntityCreeper mixin
        festiveCreeperMixin = (IMixinEntityCreeper) this;

        festiveCreeperMixin.setCreeperSpecialCooldown(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownInitial());
        festiveCreeperMixin.setCreeperSpecialCooldownInterrupted(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownInterrupted());
        festiveCreeperMixin.setCreeperSpecialCooldownAttacked(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownAttacked());
        festiveCreeperMixin.setCreeperSpecialCooldownFrustrated(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownFrustrated());
        festiveCreeperMixin.setCreeperSpecialCooldownStunned(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownStunned());
        festiveCreeperMixin.setCreeperSpecialStunnedDuration(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialStunnedDuration());

        festiveCreeperMixin.setCreeperSpecialIgnitedTimeMax(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialIgnitedTimeMax());
        festiveCreeperMixin.setCreeperSpecialInterruptedMax(PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterruptedMax());
        festiveCreeperMixin.setCreeperSpecialInterruptedDamage((float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterruptedDamage());


//Festive Creeper special attack specific
        festiveCreeperSpecialEnabled = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialEnabled();
        festiveCreeperSpecialEndTime = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialEndTime();
        festiveCreeperSpecialInterval = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialInterval();
        festiveCreeperSpecialRadianTurns = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialRadianTurns();
        festiveCreeperSpecialCooldownOver = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialCooldownOver();

        festiveCreeperSpecialAttackDestructive = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackDestructive();
        festiveCreeperSpecialAttackPower = (float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackPower();
        festiveCreeperSpecialAttackExtent = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackExtent();
        festiveCreeperSpecialAttackDestructiveCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackDestructiveCharged();
        festiveCreeperSpecialAttackPowerCharged = (float) PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackPowerCharged();
        festiveCreeperSpecialAttackExtentCharged = PrimitiveMobsConfigSpecial.getFestiveCreeperSpecialAttackExtentCharged();



//If task absent
        if(!TaskUtils.mobHasTask(this, EntityAICreeperSwellSpecial.class))
        {
//And task enabled in config
            if (this.festiveCreeperSpecialEnabled)
            {
//Add the task
                this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
            }
        }
//If task is here...
        else
        {
//And task disabled in config
            if (!this.festiveCreeperSpecialEnabled)
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
                        specialCurrentInterval = festiveCreeperSpecialInterval;
                        specialInitialRadians = Math.atan2(festiveCreeperAttackTarget.posX - this.posX, festiveCreeperAttackTarget.posZ - this.posZ);
                        specialCurrentRadians = specialInitialRadians;
//Then play sound and start attack
                        this.world.playSound(null, festiveCreeperAttackTarget.posX, festiveCreeperAttackTarget.posY, festiveCreeperAttackTarget.posZ,
                        PrimitiveMobsSoundEvents.ENTITY_CREEPER_PARTY, SoundCategory.NEUTRAL, 3.0F, 1.0F);
                        this.setCreeperPartying(true);
                    }
                }
//This is the initial jump of the special attack
                else if(this.getCreeperPartying() == true)
                {
                    --specialCurrentInterval;

                    if(specialCurrentInterval <= 0)
                    {
//Get hypotenuse to the target
                        double horizontalDistance = 
                        Math.sqrt(Math.pow(festiveCreeperAttackTarget.posX - this.posX, 2) + Math.pow(festiveCreeperAttackTarget.posZ - this.posZ, 2));

//Get distance fraction (half to full)
                        double distanceFraction = festiveCreeperSpecialAttackExtent * (double) ((float) 1 - (rand.nextFloat() / 2));
//Prepare TNT
                        EntityPrimitiveTNTPrimed tnt = new EntityPrimitiveTNTPrimed(this.getEntityWorld(), this.posX, this.posY, this.posZ, this,
                            this.festiveCreeperSpecialAttackDestructive, this.festiveCreeperSpecialAttackPower, 30);
                        tnt.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
//Use radians sin and cos, and adjusted distance
                        tnt.motionX = distanceFraction * Math.sin(specialCurrentRadians) * horizontalDistance / 18D;
                        tnt.motionY = (festiveCreeperAttackTarget.posY - tnt.posY) / 18D + 0.5D;
                        tnt.motionZ = distanceFraction * Math.cos(specialCurrentRadians) * horizontalDistance / 18D;
//Spawn TNT
                        this.getEntityWorld().spawnEntity(tnt);

//Adjust next throw radians (slightly randomized)
                        specialCurrentRadians += festiveCreeperSpecialRadianTurns * (double) ((float) 1 + (0.25F * (rand.nextFloat() - rand.nextFloat())));
//Reset interval
                        specialCurrentInterval = festiveCreeperSpecialInterval;
                    }

//If duration over
                    specialCurrentDuration++;
//Stop attack, reset and apply cooldown
                    if(specialCurrentDuration >= festiveCreeperSpecialEndTime)
                    {
                        this.resetCreeperSpecial();
                        festiveCreeperMixin.setCreeperSpecialCooldown(festiveCreeperSpecialCooldownOver);
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
        PrimitiveMobsSoundEvents.ENTITY_CREEPER_ITEMBOX, SoundCategory.NEUTRAL, 3.0F, 1.0F);
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


	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        compound.setDouble("FestiveCreeperRetreatDistance", this.festiveCreeperRetreatDistance);

	    compound.setBoolean("FestiveCreeperDestructive", this.festiveCreeperDestructive);
        compound.setInteger("FestiveCreeperCooldownMax", this.festiveCreeperCooldownMax);
        compound.setDouble("FestiveCreeperPowerBase", this.festiveCreeperPowerBase);
        compound.setDouble("FestiveCreeperPowerCharged", this.festiveCreeperPowerCharged);
        compound.setDouble("FestiveCreeperRange", this.festiveCreeperRange);
	    compound.setBoolean("FestiveCreeperRangeIgnore", this.festiveCreeperRangeIgnore);
        compound.setInteger("FestiveCreeperExtraCooldownMax", this.festiveCreeperExtraCooldownMax);

	    compound.setBoolean("FestiveCreeperRingAttack", this.festiveCreeperRingAttack);
	    compound.setBoolean("FestiveCreeperRingAttackDestructive", this.festiveCreeperRingAttackDestructive);
	    compound.setDouble("FestiveCreeperRingAttackPower", this.festiveCreeperRingAttackPower);
	    compound.setInteger("FestiveCreeperRingAttackAmount", this.festiveCreeperRingAttackAmount);
	    compound.setBoolean("FestiveCreeperRingAttackCharged", this.festiveCreeperRingAttackCharged);
	    compound.setBoolean("FestiveCreeperRingAttackDestructiveCharged", this.festiveCreeperRingAttackDestructiveCharged);
	    compound.setDouble("FestiveCreeperRingAttackPowerCharged", this.festiveCreeperRingAttackPowerCharged);
	    compound.setInteger("FestiveCreeperRingAttackAmountCharged", this.festiveCreeperRingAttackAmountCharged);

	    compound.setBoolean("FestiveCreeperCrossAttack", this.festiveCreeperCrossAttack);
	    compound.setBoolean("FestiveCreeperCrossAttackDestructive", this.festiveCreeperCrossAttackDestructive);
	    compound.setDouble("FestiveCreeperCrossAttackPower", this.festiveCreeperCrossAttackPower);
	    compound.setInteger("FestiveCreeperCrossAttackAmount", this.festiveCreeperCrossAttackAmount);
	    compound.setDouble("FestiveCreeperCrossAttackExtent", this.festiveCreeperCrossAttackExtent);
	    compound.setBoolean("FestiveCreeperCrossAttackCharged", this.festiveCreeperCrossAttackCharged);
	    compound.setBoolean("FestiveCreeperCrossAttackDestructiveCharged", this.festiveCreeperCrossAttackDestructiveCharged);
	    compound.setDouble("FestiveCreeperCrossAttackPowerCharged", this.festiveCreeperCrossAttackPowerCharged);
	    compound.setInteger("FestiveCreeperCrossAttackAmountCharged", this.festiveCreeperCrossAttackAmountCharged);
	    compound.setDouble("FestiveCreeperCrossAttackExtentCharged", this.festiveCreeperCrossAttackExtentCharged);

	    compound.setBoolean("FestiveCreeperLineAttack", this.festiveCreeperLineAttack);
	    compound.setBoolean("FestiveCreeperLineAttackDestructive", this.festiveCreeperLineAttackDestructive);
	    compound.setDouble("FestiveCreeperLineAttackPower", this.festiveCreeperLineAttackPower);
	    compound.setInteger("FestiveCreeperLineAttackAmount", this.festiveCreeperLineAttackAmount);
	    compound.setDouble("FestiveCreeperLineAttackExtent", this.festiveCreeperLineAttackExtent);
	    compound.setBoolean("FestiveCreeperLineAttackCharged", this.festiveCreeperLineAttackCharged);
	    compound.setBoolean("FestiveCreeperLineAttackDestructiveCharged", this.festiveCreeperLineAttackDestructiveCharged);
	    compound.setDouble("FestiveCreeperLineAttackPowerCharged", this.festiveCreeperLineAttackPowerCharged);
	    compound.setInteger("FestiveCreeperLineAttackAmountCharged", this.festiveCreeperLineAttackAmountCharged);
	    compound.setDouble("FestiveCreeperLineAttackExtentCharged", this.festiveCreeperLineAttackExtentCharged);



        compound.setBoolean("Partying", this.getCreeperPartying());

	    compound.setInteger("FestiveCreeperSpecialCurrentDuration", this.specialCurrentDuration);
	    compound.setInteger("FestiveCreeperSpecialCurrentInterval", this.specialCurrentInterval);
	    compound.setDouble("FestiveCreeperSpecialInitialRadians", this.specialInitialRadians);
	    compound.setDouble("FestiveCreeperSpecialCurrentRadians", this.specialCurrentRadians);

        compound.setInteger("SpecialCooldown", festiveCreeperMixin.getCreeperSpecialCooldown());
        compound.setInteger("SpecialCooldownInterrupted", festiveCreeperMixin.getCreeperSpecialCooldownInterrupted());
        compound.setInteger("SpecialCooldownAttacked", festiveCreeperMixin.getCreeperSpecialCooldownAttacked());
        compound.setInteger("SpecialCooldownFrustrated", festiveCreeperMixin.getCreeperSpecialCooldownFrustrated());
        compound.setInteger("SpecialCooldownStunned", festiveCreeperMixin.getCreeperSpecialCooldownStunned());
        compound.setInteger("SpecialStunnedDuration", festiveCreeperMixin.getCreeperSpecialStunnedDuration());

        compound.setInteger("SpecialIgniteMax", festiveCreeperMixin.getCreeperSpecialIgnitedTimeMax());
        compound.setInteger("SpecialInterruptedMax", festiveCreeperMixin.getCreeperSpecialInterruptedMax());
        compound.setFloat("SpecialInterruptedDamage", festiveCreeperMixin.getCreeperSpecialInterruptedDamage());

        compound.setBoolean("FestiveCreeperSpecialEnabled", this.festiveCreeperSpecialEnabled);
	    compound.setInteger("FestiveCreeperSpecialEndTime", this.festiveCreeperSpecialEndTime);
	    compound.setInteger("FestiveCreeperSpecialInterval", this.festiveCreeperSpecialInterval);
	    compound.setDouble("FestiveCreeperSpecialRadianTurns", this.festiveCreeperSpecialRadianTurns);
	    compound.setInteger("FestiveCreeperSpecialCooldownOver", this.festiveCreeperSpecialCooldownOver);

	    compound.setBoolean("FestiveCreeperSpecialAttackDestructive", this.festiveCreeperSpecialAttackDestructive);
	    compound.setDouble("FestiveCreeperSpecialAttackPower", (double) this.festiveCreeperSpecialAttackPower);
	    compound.setDouble("FestiveCreeperSpecialAttackExtent", this.festiveCreeperSpecialAttackExtent);
	    compound.setBoolean("FestiveCreeperSpecialAttackDestructiveCharged", this.festiveCreeperSpecialAttackDestructiveCharged);
	    compound.setDouble("FestiveCreeperSpecialAttackPowerCharged", (double) this.festiveCreeperSpecialAttackPowerCharged);
	    compound.setDouble("FestiveCreeperSpecialAttackExtentCharged", this.festiveCreeperSpecialAttackExtentCharged);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("FestiveCreeperRetreatDistance")) { this.festiveCreeperRetreatDistance = compound.getDouble("FestiveCreeperRetreatDistance"); }

        if (compound.hasKey("FestiveCreeperDestructive")) { this.festiveCreeperDestructive = compound.getBoolean("FestiveCreeperDestructive"); }
        if (compound.hasKey("FestiveCreeperCooldownMax")) { this.festiveCreeperCooldownMax = compound.getInteger("FestiveCreeperCooldownMax"); }
        if (compound.hasKey("FestiveCreeperPowerBase")) { this.festiveCreeperPowerBase = compound.getDouble("FestiveCreeperPowerBase"); }
        if (compound.hasKey("FestiveCreeperPowerCharged")) { this.festiveCreeperPowerCharged = compound.getDouble("FestiveCreeperPowerCharged"); }
        if (compound.hasKey("FestiveCreeperRange")) { this.festiveCreeperRange = compound.getDouble("FestiveCreeperRange"); }
        if (compound.hasKey("FestiveCreeperRangeIgnore")) { this.festiveCreeperRangeIgnore = compound.getBoolean("FestiveCreeperRangeIgnore"); }
        if (compound.hasKey("FestiveCreeperExtraCooldownMax")) { this.festiveCreeperExtraCooldownMax = compound.getInteger("FestiveCreeperExtraCooldownMax"); }

        if (compound.hasKey("FestiveCreeperRingAttack")) { this.festiveCreeperRingAttack = compound.getBoolean("FestiveCreeperRingAttack"); }
        if (compound.hasKey("FestiveCreeperRingAttackDestructive")) { this.festiveCreeperRingAttackDestructive = compound.getBoolean("FestiveCreeperRingAttackDestructive"); }
        if (compound.hasKey("FestiveCreeperRingAttackPower")) { this.festiveCreeperRingAttackPower = compound.getDouble("FestiveCreeperRingAttackPower"); }
        if (compound.hasKey("FestiveCreeperRingAttackAmount")) { this.festiveCreeperRingAttackAmount = compound.getInteger("FestiveCreeperRingAttackAmount"); }
        if (compound.hasKey("FestiveCreeperRingAttackCharged")) { this.festiveCreeperRingAttackCharged = compound.getBoolean("FestiveCreeperRingAttackCharged"); }
        if (compound.hasKey("FestiveCreeperRingAttackDestructiveCharged")) { this.festiveCreeperRingAttackDestructiveCharged 
            = compound.getBoolean("FestiveCreeperRingAttackDestructiveCharged"); }
        if (compound.hasKey("FestiveCreeperRingAttackPowerCharged")) { this.festiveCreeperRingAttackPowerCharged 
            = compound.getDouble("FestiveCreeperRingAttackPowerCharged"); }
        if (compound.hasKey("FestiveCreeperRingAttackAmountCharged")) { this.festiveCreeperRingAttackAmountCharged 
            = compound.getInteger("FestiveCreeperRingAttackAmountCharged"); }

        if (compound.hasKey("FestiveCreeperCrossAttack")) { this.festiveCreeperCrossAttack = compound.getBoolean("FestiveCreeperCrossAttack"); }
        if (compound.hasKey("FestiveCreeperCrossAttackDestructive")) { this.festiveCreeperCrossAttackDestructive = compound.getBoolean("FestiveCreeperCrossAttackDestructive"); }
        if (compound.hasKey("FestiveCreeperCrossAttackPower")) { this.festiveCreeperCrossAttackPower = compound.getDouble("FestiveCreeperCrossAttackPower"); }
        if (compound.hasKey("FestiveCreeperCrossAttackAmount")) { this.festiveCreeperCrossAttackAmount = compound.getInteger("FestiveCreeperCrossAttackAmount"); }
        if (compound.hasKey("FestiveCreeperCrossAttackExtent")) { this.festiveCreeperCrossAttackExtent = compound.getDouble("FestiveCreeperCrossAttackExtent"); }
        if (compound.hasKey("FestiveCreeperCrossAttackCharged")) { this.festiveCreeperCrossAttackCharged = compound.getBoolean("FestiveCreeperCrossAttackCharged"); }
        if (compound.hasKey("FestiveCreeperCrossAttackDestructiveCharged")) { this.festiveCreeperCrossAttackDestructiveCharged 
            = compound.getBoolean("FestiveCreeperCrossAttackDestructiveCharged"); }
        if (compound.hasKey("FestiveCreeperCrossAttackPowerCharged")) { this.festiveCreeperCrossAttackPowerCharged 
            = compound.getDouble("FestiveCreeperCrossAttackPowerCharged"); }
        if (compound.hasKey("FestiveCreeperCrossAttackAmountCharged")) { this.festiveCreeperCrossAttackAmountCharged 
            = compound.getInteger("FestiveCreeperCrossAttackAmountCharged"); }
        if (compound.hasKey("FestiveCreeperCrossAttackExtentCharged")) { this.festiveCreeperCrossAttackExtentCharged 
            = compound.getDouble("FestiveCreeperCrossAttackExtentCharged"); }

        if (compound.hasKey("FestiveCreeperLineAttack")) { this.festiveCreeperLineAttack = compound.getBoolean("FestiveCreeperLineAttack"); }
        if (compound.hasKey("FestiveCreeperLineAttackDestructive")) { this.festiveCreeperLineAttackDestructive = compound.getBoolean("FestiveCreeperLineAttackDestructive"); }
        if (compound.hasKey("FestiveCreeperLineAttackPower")) { this.festiveCreeperLineAttackPower = compound.getDouble("FestiveCreeperLineAttackPower"); }
        if (compound.hasKey("FestiveCreeperLineAttackAmount")) { this.festiveCreeperLineAttackAmount = compound.getInteger("FestiveCreeperLineAttackAmount"); }
        if (compound.hasKey("FestiveCreeperLineAttackExtent")) { this.festiveCreeperLineAttackExtent = compound.getDouble("FestiveCreeperLineAttackExtent"); }
        if (compound.hasKey("FestiveCreeperLineAttackCharged")) { this.festiveCreeperLineAttackCharged = compound.getBoolean("FestiveCreeperLineAttackCharged"); }
        if (compound.hasKey("FestiveCreeperLineAttackDestructiveCharged")) { this.festiveCreeperLineAttackDestructiveCharged 
            = compound.getBoolean("FestiveCreeperLineAttackDestructiveCharged"); }
        if (compound.hasKey("FestiveCreeperLineAttackPowerCharged")) { this.festiveCreeperLineAttackPowerCharged 
            = compound.getDouble("FestiveCreeperLineAttackPowerCharged"); }
        if (compound.hasKey("FestiveCreeperLineAttackAmountCharged")) { this.festiveCreeperLineAttackAmountCharged 
            = compound.getInteger("FestiveCreeperLineAttackAmountCharged"); }
        if (compound.hasKey("FestiveCreeperLineAttackExtentCharged")) { this.festiveCreeperLineAttackExtentCharged 
            = compound.getDouble("FestiveCreeperLineAttackExtentCharged"); }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIBackOffFromEntity.class))
        {
            this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.festiveCreeperRetreatDistance, true));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIBackOffFromEntity.class);

            this.tasks.addTask(3, new EntityAIBackOffFromEntity(this, this.festiveCreeperRetreatDistance, true));
        }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIThrowTNT.class))
        {
            this.tasks.addTask(2, 
            new EntityFestiveCreeper.EntityAIThrowTNT(this, festiveCreeperDestructive, festiveCreeperCooldownMax, 
            festiveCreeperPowerBase, festiveCreeperPowerCharged, festiveCreeperRange, festiveCreeperRangeIgnore, festiveCreeperExtraCooldownMax,

            festiveCreeperRingAttack, festiveCreeperRingAttackDestructive, festiveCreeperRingAttackPower, festiveCreeperRingAttackAmount,
            festiveCreeperRingAttackCharged, festiveCreeperRingAttackDestructiveCharged, festiveCreeperRingAttackPowerCharged, festiveCreeperRingAttackAmountCharged,

            festiveCreeperCrossAttack, festiveCreeperCrossAttackDestructive, festiveCreeperCrossAttackPower, festiveCreeperCrossAttackAmount, festiveCreeperCrossAttackExtent, 
            festiveCreeperCrossAttackCharged, festiveCreeperCrossAttackDestructiveCharged, festiveCreeperCrossAttackPowerCharged, festiveCreeperCrossAttackAmountCharged,
            festiveCreeperCrossAttackExtentCharged,

            festiveCreeperLineAttack, festiveCreeperLineAttackDestructive, festiveCreeperLineAttackPower, festiveCreeperLineAttackAmount, festiveCreeperLineAttackExtent, 
            festiveCreeperLineAttackCharged, festiveCreeperLineAttackDestructiveCharged, festiveCreeperLineAttackPowerCharged, festiveCreeperLineAttackAmountCharged,
            festiveCreeperLineAttackExtentCharged));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIThrowTNT.class);

            this.tasks.addTask(2, 
            new EntityFestiveCreeper.EntityAIThrowTNT(this, festiveCreeperDestructive, festiveCreeperCooldownMax, 
            festiveCreeperPowerBase, festiveCreeperPowerCharged, festiveCreeperRange, festiveCreeperRangeIgnore, festiveCreeperExtraCooldownMax,

            festiveCreeperRingAttack, festiveCreeperRingAttackDestructive, festiveCreeperRingAttackPower, festiveCreeperRingAttackAmount,
            festiveCreeperRingAttackCharged, festiveCreeperRingAttackDestructiveCharged, festiveCreeperRingAttackPowerCharged, festiveCreeperRingAttackAmountCharged,

            festiveCreeperCrossAttack, festiveCreeperCrossAttackDestructive, festiveCreeperCrossAttackPower, festiveCreeperCrossAttackAmount, festiveCreeperCrossAttackExtent, 
            festiveCreeperCrossAttackCharged, festiveCreeperCrossAttackDestructiveCharged, festiveCreeperCrossAttackPowerCharged, festiveCreeperCrossAttackAmountCharged,
            festiveCreeperCrossAttackExtentCharged,

            festiveCreeperLineAttack, festiveCreeperLineAttackDestructive, festiveCreeperLineAttackPower, festiveCreeperLineAttackAmount, festiveCreeperLineAttackExtent, 
            festiveCreeperLineAttackCharged, festiveCreeperLineAttackDestructiveCharged, festiveCreeperLineAttackPowerCharged, festiveCreeperLineAttackAmountCharged,
            festiveCreeperLineAttackExtentCharged));
        }


        if (compound.hasKey("Partying")) { this.setCreeperPartying(compound.getBoolean("Partying")); }


        if (compound.hasKey("FestiveCreeperSpecialCurrentDuration")) { this.specialCurrentDuration = compound.getInteger("FestiveCreeperSpecialCurrentDuration"); }
        if (compound.hasKey("FestiveCreeperSpecialCurrentInterval")) { this.specialCurrentInterval = compound.getInteger("FestiveCreeperSpecialCurrentInterval"); }
        if (compound.hasKey("FestiveCreeperSpecialInitialRadians")) { this.specialInitialRadians = compound.getDouble("FestiveCreeperSpecialInitialRadians"); }
        if (compound.hasKey("FestiveCreeperSpecialCurrentRadians")) { this.specialCurrentRadians = compound.getDouble("FestiveCreeperSpecialCurrentRadians"); }

        if (compound.hasKey("SpecialCooldown")) { festiveCreeperMixin.setCreeperSpecialCooldown(compound.getInteger("SpecialCooldown")); }
        if (compound.hasKey("SpecialCooldownInterrupted")) { festiveCreeperMixin.setCreeperSpecialCooldownInterrupted(compound.getInteger("SpecialCooldownInterrupted")); }
        if (compound.hasKey("SpecialCooldownAttacked")) { festiveCreeperMixin.setCreeperSpecialCooldownAttacked(compound.getInteger("SpecialCooldownAttacked")); }
        if (compound.hasKey("SpecialCooldownFrustrated")) { festiveCreeperMixin.setCreeperSpecialCooldownFrustrated(compound.getInteger("SpecialCooldownFrustrated")); }
        if (compound.hasKey("SpecialCooldownStunned")) { festiveCreeperMixin.setCreeperSpecialCooldownStunned(compound.getInteger("SpecialCooldownStunned")); }
        if (compound.hasKey("SpecialStunnedDuration")) { festiveCreeperMixin.setCreeperSpecialStunnedDuration(compound.getInteger("SpecialStunnedDuration")); }

        if (compound.hasKey("SpecialIgniteMax")) { festiveCreeperMixin.setCreeperSpecialIgnitedTimeMax(compound.getInteger("SpecialIgniteMax")); }
        if (compound.hasKey("SpecialInterruptedDamage")) { festiveCreeperMixin.setCreeperSpecialInterruptedDamage(compound.getFloat("SpecialInterruptedDamage")); }
        if (compound.hasKey("SpecialInterruptedMax")) { festiveCreeperMixin.setCreeperSpecialInterruptedMax(compound.getInteger("SpecialInterruptedMax")); }

        if (compound.hasKey("FestiveCreeperSpecialEnabled")) { this.festiveCreeperSpecialEnabled = compound.getBoolean("FestiveCreeperSpecialEnabled"); }
        if (compound.hasKey("FestiveCreeperSpecialEndTime")) { this.festiveCreeperSpecialEndTime = compound.getInteger("FestiveCreeperSpecialEndTime"); }
        if (compound.hasKey("FestiveCreeperSpecialInterval")) { this.festiveCreeperSpecialInterval = compound.getInteger("FestiveCreeperSpecialInterval"); }
        if (compound.hasKey("FestiveCreeperSpecialRadianTurns")) { this.festiveCreeperSpecialRadianTurns = compound.getDouble("FestiveCreeperSpecialRadianTurns"); }
        if (compound.hasKey("FestiveCreeperSpecialCooldownOver")) { this.festiveCreeperSpecialCooldownOver 
            = compound.getInteger("FestiveCreeperSpecialCooldownOver"); }

        if (compound.hasKey("FestiveCreeperSpecialAttackDestructive")) 
            { this.festiveCreeperSpecialAttackDestructive = compound.getBoolean("FestiveCreeperSpecialAttackDestructive"); }
        if (compound.hasKey("FestiveCreeperSpecialAttackPower")) { this.festiveCreeperSpecialAttackPower = (float) compound.getDouble("FestiveCreeperSpecialAttackPower"); }
        if (compound.hasKey("FestiveCreeperSpecialAttackExtent")) { this.festiveCreeperSpecialAttackExtent = compound.getDouble("FestiveCreeperSpecialAttackExtent"); }
        if (compound.hasKey("FestiveCreeperSpecialAttackDestructiveCharged")) 
            { this.festiveCreeperSpecialAttackDestructiveCharged = compound.getBoolean("FestiveCreeperSpecialAttackDestructiveCharged"); }
        if (compound.hasKey("FestiveCreeperSpecialAttackPowerCharged")) 
            { this.festiveCreeperSpecialAttackPowerCharged = (float) compound.getDouble("FestiveCreeperSpecialAttackPowerCharged"); }
        if (compound.hasKey("FestiveCreeperSpecialAttackExtentCharged")) 
            { this.festiveCreeperSpecialAttackExtentCharged = compound.getDouble("FestiveCreeperSpecialAttackExtentCharged"); }



//Remove task if present, and 
        TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
        if (this.festiveCreeperSpecialEnabled) 
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

                            switch (attackToDo) {
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
