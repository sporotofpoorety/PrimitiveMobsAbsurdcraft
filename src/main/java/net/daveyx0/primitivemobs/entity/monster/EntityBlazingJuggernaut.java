package net.daveyx0.primitivemobs.entity.monster;

import javax.annotation.Nullable;

import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBlazingJuggernaut extends EntityMob implements IMultiMob {

	 /** Random offset used in floating behaviour */
    private float heightOffset = 0.5F;
    /** ticks until heightOffset is randomized */
    private int heightOffsetUpdateTime;

    private int meleeCooldown;
    private int chargeCooldown;

    private int meleeCooldownMax;
    private double meleeDistanceMax;
    private int chargeCooldownMax;
    private double chargeDistanceMax;
    private boolean chargeDistanceIgnore;
    private double chargePropelFactor;
    private float chargeSpeedFactor;

    public EntityBlazingJuggernaut(World worldIn)
    {
        super(worldIn);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 10;

        this.meleeCooldown = 0;
        this.chargeCooldown = 0;

        this.meleeCooldownMax = PrimitiveMobsConfigSpecial.getBlazingJuggernautMeleeCooldownMax();
        this.meleeDistanceMax = PrimitiveMobsConfigSpecial.getBlazingJuggernautMeleeDistanceMax();
        this.chargeCooldownMax = PrimitiveMobsConfigSpecial.getBlazingJuggernautChargeCooldownMax();
        this.chargeDistanceMax = PrimitiveMobsConfigSpecial.getBlazingJuggernautChargeDistanceMax();
        this.chargeDistanceIgnore = PrimitiveMobsConfigSpecial.getBlazingJuggernautChargeDistanceIgnore();
        this.chargePropelFactor = PrimitiveMobsConfigSpecial.getBlazingJuggernautChargePropelFactor();
        this.chargeSpeedFactor = (float) PrimitiveMobsConfigSpecial.getBlazingJuggernautChargeSpeedFactor();

        this.tasks.addTask(4, new EntityBlazingJuggernaut.EntityAIAerialChargeMovement(this, this.meleeCooldown, this.meleeCooldownMax,
        (this.meleeDistanceMax * this.meleeDistanceMax), this.chargeCooldown, this.chargeCooldownMax, this.chargeDistanceMax, 
        this.chargeDistanceIgnore, this.chargePropelFactor, this.chargeSpeedFactor));
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.16);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    }

    public static void registerFixesBlaze(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityBlazingJuggernaut.class);
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override 
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        super.onInitialSpawn(difficulty, livingdata);

        return livingdata;
    }

 /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Preserves field values including assigned by NBT
        compound.setInteger("MeleeCooldownMax", meleeCooldownMax);
        compound.setDouble("MeleeDistanceMax", meleeDistanceMax);
        compound.setInteger("ChargeCooldownMax", chargeCooldownMax);
        compound.setDouble("ChargeDistanceMax", chargeDistanceMax);
        compound.setBoolean("ChargeDistanceIgnore", chargeDistanceIgnore);
        compound.setDouble("ChargePropelFactor", chargePropelFactor);
        compound.setFloat("ChargeSpeedFactor", chargeSpeedFactor);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("MeleeCooldownMax")) { this.meleeCooldownMax = compound.getInteger("MeleeCooldownMax"); }
        if (compound.hasKey("MeleeDistanceMax")) { this.meleeDistanceMax = compound.getDouble("MeleeDistanceMax"); }
        if (compound.hasKey("ChargeCooldownMax")) { this.chargeCooldownMax = compound.getInteger("ChargeCooldownMax"); }
        if (compound.hasKey("ChargeDistanceMax")) { this.chargeDistanceMax = compound.getDouble("ChargeDistanceMax"); }
        if (compound.hasKey("ChargeDistanceIgnore")) { this.chargeDistanceIgnore = compound.getBoolean("ChargeDistanceIgnore"); } 
        if (compound.hasKey("ChargePropelFactor")) { this.chargePropelFactor = compound.getDouble("ChargePropelFactor"); }
        if (compound.hasKey("ChargeSpeedFactor")) { this.chargeSpeedFactor = compound.getFloat("ChargeSpeedFactor"); }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIAerialChargeMovement.class))
        {
            this.tasks.addTask(4, new EntityBlazingJuggernaut.EntityAIAerialChargeMovement(this, this.meleeCooldown, this.meleeCooldownMax,
            (this.meleeDistanceMax * this.meleeDistanceMax), this.chargeCooldown, this.chargeCooldownMax, this.chargeDistanceMax, 
            this.chargeDistanceIgnore, this.chargePropelFactor, this.chargeSpeedFactor));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIAerialChargeMovement.class);

            this.tasks.addTask(4, new EntityBlazingJuggernaut.EntityAIAerialChargeMovement(this, this.meleeCooldown, this.meleeCooldownMax,
            (this.meleeDistanceMax * this.meleeDistanceMax), this.chargeCooldown, this.chargeCooldownMax, this.chargeDistanceMax, 
            this.chargeDistanceIgnore, this.chargePropelFactor, this.chargeSpeedFactor));        
        }
    } 

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }
    
    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
    }
    
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender()
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        return 1.0F;
    }
    
    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
    	
        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        if (this.getEntityWorld().isRemote)
        {
            if (this.rand.nextInt(24) == 0 && !this.isSilent())
            {
                this.getEntityWorld().playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i)
            {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

        super.onLivingUpdate();
    }

    protected void updateAITasks()
    {
        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }

        --this.heightOffsetUpdateTime;

        if (this.heightOffsetUpdateTime <= 0)
        {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
        }

        EntityLivingBase entitylivingbase = this.getAttackTarget();

//If has target
        if (entitylivingbase != null)
        {
//Become airborne
            this.isAirBorne = true;
//If target is above
            if(entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() > this.posY + (double)this.getEyeHeight() + (double)this.heightOffset)
            {
//Go up
                this.motionY += (0.30000001192092896D - this.motionY) * 0.30000001192092896D;
            }
        }

        super.updateAITasks();
    }
    
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_BLAZINGJUGGERNAUT;
    }
    

    public void fall(float distance, float damageMultiplier)
    {
    }
    
    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }
    
    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }
    
    static class EntityAIAerialChargeMovement extends EntityAIBase
    {
        private final EntityBlazingJuggernaut blaze;
        private int meleeCooldown;
        private int meleeCooldownMax;
        private double meleeDistanceMaxSq;
        private int chargeCooldown;
        private int chargeCooldownMax;
        private double chargeDistanceMaxSq;
        private boolean chargeDistanceIgnore;
        private double chargePropelFactor;
        private float chargeSpeedFactor;

        public EntityAIAerialChargeMovement(EntityBlazingJuggernaut blazeIn, int meleeCooldown, int meleeCooldownMax, 
        double meleeDistanceMaxSq, int chargeCooldown, int chargeCooldownMax, double chargeDistanceMaxSq, 
        boolean chargeDistanceIgnore, double chargePropelFactor, float chargeSpeedFactor)
        {
            this.blaze = blazeIn;
            this.setMutexBits(3);
            this.meleeCooldown = meleeCooldown;
            this.meleeCooldownMax = meleeCooldownMax;
            this.meleeDistanceMaxSq = meleeDistanceMaxSq;
            this.chargeCooldown = chargeCooldown;
            this.chargeCooldownMax = chargeCooldownMax;
            this.chargeDistanceMaxSq = chargeDistanceMaxSq;
            this.chargeDistanceIgnore = chargeDistanceIgnore;
            this.chargePropelFactor = chargePropelFactor;
            this.chargeSpeedFactor = chargeSpeedFactor;
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            EntityLivingBase entitylivingbase = this.blaze.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            this.meleeCooldown = 0;
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
            
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = this.blaze.getAttackTarget();
            double d0 = this.blaze.getDistanceSq(entitylivingbase);

            ++this.meleeCooldown;
            ++this.chargeCooldown;
            if (d0 < this.meleeDistanceMaxSq)
            {
                if (this.meleeCooldown > this.meleeCooldownMax)
                {
                    this.meleeCooldown = 0;
                    this.blaze.attackEntityAsMob(entitylivingbase);
                }

                this.blaze.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 1.0D);
                
                if(entitylivingbase.posY < this.blaze.posY)
                {
                	this.blaze.motionY -= 0.1D;
                }
            }
            else if (this.chargeDistanceIgnore || d0 < (this.chargeDistanceMaxSq))
            {
                double d1 = entitylivingbase.posX - this.blaze.posX;
//              double d2 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (this.blaze.posY + (double)(this.blaze.height / 2.0F));
                double d2 = entitylivingbase.getEntityBoundingBox().maxY - (this.blaze.posY + (double)(this.blaze.height / 2.0F));
                double d3 = entitylivingbase.posZ - this.blaze.posZ;

                    if (this.chargeCooldown > this.chargeCooldownMax)
                    {
                        //this.getEntityWorld().playAuxSFXAtEntity((EntityPlayer)null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                        
                        this.blaze.motionX += (Math.signum(d1) * 0.5D - this.blaze.motionX) * this.chargePropelFactor;
                        this.blaze.motionY += (Math.signum(d2) * 0.699999988079071D - this.blaze.motionY) * this.chargePropelFactor;
                        this.blaze.motionZ += (Math.signum(d3) * 0.5D - this.blaze.motionZ) * this.chargePropelFactor;
                        //float f = (float)(Math.atan2(this.blaze.motionZ, this.blaze.motionX) * 180.0D / Math.PI) - 90.0F;
                        //float f1 = MathHelper.wrapDegrees(f - this.blaze.rotationYaw);
                        this.blaze.moveForward = this.chargeSpeedFactor;
                        
                        this.chargeCooldown = 0;
                    }

                this.blaze.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0F, 10.0F);
            }
            else
            {
                this.blaze.getNavigator().clearPath();
                this.blaze.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 1.0D);
            }

            super.updateTask();
        }
    }
    
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	if(type == EnumCreatureType.MONSTER){return false;}
    	return super.isCreatureType(type, forSpawnCount);
    }
}
