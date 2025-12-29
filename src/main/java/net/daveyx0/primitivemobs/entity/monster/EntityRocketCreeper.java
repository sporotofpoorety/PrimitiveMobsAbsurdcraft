package net.daveyx0.primitivemobs.entity.monster;

import java.util.Collection;

import javax.annotation.Nullable;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellSpecial;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityRocketCreeper extends EntityPrimitiveCreeper implements IMultiMob {

//Access getters and setters of these mixins
    public IMixinEntityMob entityMobMixin;
    public IMixinEntityCreeper rocketCreeperMixin;

	int timeBeforeJumping;

    private int prepareTicks;
    private int specialHomingTicksStart;

    protected float chargedMultiplier;

    int detonationTimerMax;
    double detonationDistance;
    boolean alwaysJumps;

	protected int creeperSpecialExplosionRadius;
    private boolean creeperSpecialEnabled;

    private double specialStartSpeed;
    private double specialCurrentSpeed;
    private double specialAcceleration; 
    private double specialExplodeDistance;
    private int specialHomingTicksMax;

	public EntityRocketCreeper(World worldIn) {
		super(worldIn);
//Rocket Creeper specific data parameters
		setCreeperRocket(false);
        setCreeperPreparing(false);
        setCreeperPreparingIsPastFirstTick(false);
        setCreeperHoming(false);

//Purely state logic
        prepareTicks = 0;
        specialHomingTicksStart = 0;


//Access getters and setters of these mixins
        entityMobMixin = (IMixinEntityMob) this;
        rocketCreeperMixin = (IMixinEntityCreeper) this;

        rocketCreeperMixin.setCreeperExplosionRadius(PrimitiveMobsConfigSpecial.getRocketCreeperExplosionPower());
        this.detonationTimerMax = PrimitiveMobsConfigSpecial.getRocketCreeperDetonationTimerMax();
        this.detonationDistance = PrimitiveMobsConfigSpecial.getRocketCreeperDetonationDistance();
        this.alwaysJumps = PrimitiveMobsConfigSpecial.getRocketCreeperAlwaysJump();

        rocketCreeperMixin.setCreeperSpecialCooldown(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownInitial());
        rocketCreeperMixin.setCreeperSpecialCooldownInterrupted(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownInterrupted());
        rocketCreeperMixin.setCreeperSpecialCooldownAttacked(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownAttacked());
        rocketCreeperMixin.setCreeperSpecialCooldownFrustrated(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownFrustrated());
        rocketCreeperMixin.setCreeperSpecialCooldownStunned(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownStunned());
        rocketCreeperMixin.setCreeperSpecialStunnedDuration(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialStunnedDuration());

        rocketCreeperMixin.setCreeperSpecialIgnitedTimeMax(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialIgnitedTimeMax());
        rocketCreeperMixin.setCreeperSpecialInterruptedMax(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialInterruptedMax());
        rocketCreeperMixin.setCreeperSpecialInterruptedDamage((float) PrimitiveMobsConfigSpecial.getRocketCreeperSpecialInterruptedDamage());


//Rocket Creeper specific
        chargedMultiplier = (float) PrimitiveMobsConfigSpecial.getRocketCreeperChargedMultiplier();

        creeperSpecialEnabled = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialEnabled();
        creeperSpecialExplosionRadius = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialExplosionPower();

        specialStartSpeed = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialStartSpeed();
        specialCurrentSpeed = specialStartSpeed;
        specialAcceleration = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialAcceleration();
        specialExplodeDistance = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialExplodeDistance();
        specialHomingTicksMax = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialMaxTicks();


//If task absent
        if(!TaskUtils.mobHasTask(this, EntityAICreeperSwellSpecial.class))
        {
//And task enabled in config
            if (this.creeperSpecialEnabled)
            {
//Add the task
                this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
            }
        }
//If task is here...
        else
        {
//And task disabled in config
            if (!this.creeperSpecialEnabled)
            {
//Remove the task
                TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
            }                
        }
    }
	
	private static final DataParameter<Boolean> IS_ROCKET = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_PREPARING = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_PREPARING_PAST_FIRST_TICK = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_HOMING = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
	
    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAICreeperSwell(this));
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
//Data parameters
        compound.setBoolean("Rocket", this.isRocket());
        compound.setBoolean("Preparing", this.getCreeperPreparing());
        compound.setBoolean("Homing", this.getCreeperHoming());

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        compound.setInteger("ExplosionRadius", rocketCreeperMixin.getCreeperExplosionRadius());
        compound.setInteger("DetonationTimerMax", this.detonationTimerMax);
        compound.setDouble("DetonationDistance", this.detonationDistance);
        compound.setBoolean("AlwaysJumps", this.alwaysJumps);

        compound.setInteger("SpecialCooldown", rocketCreeperMixin.getCreeperSpecialCooldown());
        compound.setInteger("SpecialCooldownInterrupted", rocketCreeperMixin.getCreeperSpecialCooldownInterrupted());
        compound.setInteger("SpecialCooldownAttacked", rocketCreeperMixin.getCreeperSpecialCooldownAttacked());
        compound.setInteger("SpecialCooldownFrustrated", rocketCreeperMixin.getCreeperSpecialCooldownFrustrated());
        compound.setInteger("SpecialCooldownStunned", rocketCreeperMixin.getCreeperSpecialCooldownStunned());
        compound.setInteger("SpecialStunnedDuration", rocketCreeperMixin.getCreeperSpecialStunnedDuration());

        compound.setInteger("SpecialIgniteMax", rocketCreeperMixin.getCreeperSpecialIgnitedTimeMax());
        compound.setInteger("SpecialInterruptedMax", rocketCreeperMixin.getCreeperSpecialInterruptedMax());
        compound.setFloat("SpecialInterruptedDamage", rocketCreeperMixin.getCreeperSpecialInterruptedDamage());

        compound.setFloat("ChargedMultiplier", this.chargedMultiplier);

        compound.setBoolean("SpecialEnabled", this.creeperSpecialEnabled);
        compound.setInteger("SpecialExplosionRadius", this.creeperSpecialExplosionRadius);

        compound.setDouble("SpecialStartSpeed", this.specialStartSpeed);
        compound.setDouble("SpecialCurrentSpeed", this.specialCurrentSpeed);
        compound.setDouble("SpecialAcceleration", this.specialAcceleration);
        compound.setDouble("SpecialExplodeDistance", this.specialExplodeDistance);
        compound.setInteger("SpecialHomingMax", this.specialHomingTicksMax);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
//Data parameters
        this.setCreeperRocket(compound.getBoolean("Rocket"));
        if (compound.hasKey("Preparing")) { this.setCreeperPreparing(compound.getBoolean("Preparing")); }
        if (compound.hasKey("Homing")) { this.setCreeperHoming(compound.getBoolean("Homing")); }

        if (compound.hasKey("ExplosionRadius")) { rocketCreeperMixin.setCreeperExplosionRadius(compound.getInteger("ExplosionRadius")); }
        if (compound.hasKey("DetonationTimerMax")) { this.detonationTimerMax = compound.getInteger("DetonationTimerMax"); }
        if (compound.hasKey("DetonationDistance")) { this.detonationDistance = compound.getDouble("DetonationDistance"); }
        if (compound.hasKey("AlwaysJumps")) { this.alwaysJumps = compound.getBoolean("AlwaysJumps"); }

        if (compound.hasKey("SpecialCooldown")) { rocketCreeperMixin.setCreeperSpecialCooldown(compound.getInteger("SpecialCooldown")); }
        if (compound.hasKey("SpecialCooldownInterrupted")) { rocketCreeperMixin.setCreeperSpecialCooldownInterrupted(compound.getInteger("SpecialCooldownInterrupted")); }
        if (compound.hasKey("SpecialCooldownAttacked")) { rocketCreeperMixin.setCreeperSpecialCooldownAttacked(compound.getInteger("SpecialCooldownAttacked")); }
        if (compound.hasKey("SpecialCooldownFrustrated")) { rocketCreeperMixin.setCreeperSpecialCooldownFrustrated(compound.getInteger("SpecialCooldownFrustrated")); }
        if (compound.hasKey("SpecialCooldownStunned")) { rocketCreeperMixin.setCreeperSpecialCooldownStunned(compound.getInteger("SpecialCooldownStunned")); }
        if (compound.hasKey("SpecialStunnedDuration")) { rocketCreeperMixin.setCreeperSpecialStunnedDuration(compound.getInteger("SpecialStunnedDuration")); }

        if (compound.hasKey("SpecialIgniteMax")) { rocketCreeperMixin.setCreeperSpecialIgnitedTimeMax(compound.getInteger("SpecialIgniteMax")); }
        if (compound.hasKey("SpecialInterruptedDamage")) { rocketCreeperMixin.setCreeperSpecialInterruptedDamage(compound.getFloat("SpecialInterruptedDamage")); }
        if (compound.hasKey("SpecialInterruptedMax")) { rocketCreeperMixin.setCreeperSpecialInterruptedMax(compound.getInteger("SpecialInterruptedMax")); }

        if (compound.hasKey("ChargedMultiplier")) { this.chargedMultiplier = compound.getFloat("ChargedMultiplier"); }

        if (compound.hasKey("SpecialEnabled")) { this.creeperSpecialEnabled = compound.getBoolean("SpecialEnabled"); }
        if (compound.hasKey("SpecialExplosionRadius")) { this.creeperSpecialExplosionRadius = compound.getInteger("SpecialExplosionRadius"); }

        if (compound.hasKey("SpecialStartSpeed")) { this.specialStartSpeed = compound.getDouble("SpecialStartSpeed"); }
        if (compound.hasKey("SpecialCurrentSpeed")) { this.specialCurrentSpeed = compound.getDouble("SpecialCurrentSpeed"); }
        if (compound.hasKey("SpecialAcceleration")) { this.specialAcceleration = compound.getDouble("SpecialAcceleration"); }
        if (compound.hasKey("SpecialExplodeDistance")) { this.specialExplodeDistance = compound.getDouble("SpecialExplodeDistance"); }
        if (compound.hasKey("SpecialHomingMax")) { this.specialHomingTicksMax = compound.getInteger("SpecialHomingMax"); }


//Remove task if present, and 
        TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
        if (this.creeperSpecialEnabled) 
        {
            this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
        }
    }
    
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (onGroundIn)
        {
            if (this.fallDistance > 0.0F)
            {
                state.getBlock().onFallenUpon(this.world, pos, this, this.fallDistance);
            }

            this.fallDistance = 0.0F;
        }
        else if (y < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - y);
        }
    }
    
    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
    	if(this.isRocket())
    	{
    		return;
    	}
    	else
    	{
    		super.playStepSound(pos, blockIn);
    	}
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
//Was originally 0.35D but it felt a bit too fast
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
//Special attack did nothing with 16 follow range
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
    }
    
    protected void entityInit()
    {
        super.entityInit();
        this.getDataManager().register(IS_ROCKET, Boolean.valueOf(false));
        this.getDataManager().register(IS_PREPARING, Boolean.valueOf(false));
        this.getDataManager().register(IS_PREPARING_PAST_FIRST_TICK, Boolean.valueOf(false));
        this.getDataManager().register(IS_HOMING, Boolean.valueOf(false));
    }
    
    @Override
    public void fall(float distance, float damageMultiplier)
    {
    	if(this.isRocket())
    	{
    		this.explode(rocketCreeperMixin.getCreeperExplosionRadius(), this.chargedMultiplier, true);
    	}
    }
    
    private void explode(int blastRadius, float chargedMult, boolean doesSetDead)
    {
        if (!this.getEntityWorld().isRemote)
        {
            boolean flag = this.getEntityWorld().getGameRules().getBoolean("mobGriefing");
            float f = (float) (this.getPowered() ? chargedMult : 1.0F);
            
			ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
			if(tameable != null && tameable.isTamed())
			{
	            this.attackEntityFrom(DamageSource.GENERIC, 1);
	            this.setCreeperRocket(false);
			}
			else
			{
                if(doesSetDead)
                {
				    this.dead = true;
	                this.setDead();
                }
			}

            this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, ((float) blastRadius) * f, flag);

            this.spawnLingeringCloud();
        }
    }

    public boolean hasEnoughSpaceToJump(Entity entityIn)
    {
    	boolean flag = true;
    	if (!this.alwaysJumps) {
	    	for(int i = 0; i < 5; i++)
	    	{
	    		flag = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight() + i, this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false) == null;
	    	}
    	}
        return flag;
    }
    
    public void onUpdate()
    {
        super.onUpdate();
//Get target
        EntityLivingBase rocketCreeperAttackTarget = this.getAttackTarget();

//Rocket Creeper specific logic on special explosion
        if(rocketCreeperMixin.getCreeperStateSpecial() > 0
//Special swell task itself already is
//mutually exclusive with normal swell, but not with isRocket obviously
        && this.isRocket() == false)
        {
//Null protection and reset special attack state if target is gone 
            if(rocketCreeperAttackTarget != null)
            {
                if(this.getCreeperPreparing() == false && this.getCreeperHoming() == false)
                {
//Check if it's time to explode...
                    if(rocketCreeperMixin.getCreeperSpecialIgnitedTime() >= rocketCreeperMixin.getCreeperSpecialIgnitedTimeMax())
                    {
//Then do initial preparation jump
                        this.setCreeperPreparing(true);
                    }
                }
//This is the initial jump of the special attack
                else if(this.getCreeperPreparing() == true)
                {
                    ++prepareTicks;
//On initial jump
                    if(!this.getCreeperPreparingIsPastFirstTick())
                    {
//Cancel all motion and gravity
                        this.motionX = 0.0D; 
                        this.motionY = 0.0D; 
                        this.motionZ = 0.0D; 
                        this.setNoGravity(true);
//Jump start sound
                        this.world.playSound(null, 
                        rocketCreeperAttackTarget.posX, rocketCreeperAttackTarget.posY, rocketCreeperAttackTarget.posZ,
                        SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.NEUTRAL, 3.0F, 0.5F);
//Then jump extremely fast
                        this.motionY = 5.0D;
                        this.setCreeperPreparingIsPastFirstTick(true);
                    }
//End if already significantly above target or too many ticks
                    else if
                    (this.posY >= (rocketCreeperAttackTarget.posY + 10D) || this.prepareTicks >= 200)
                    {
                        this.motionX = 0;
                        this.motionY = 0;
                        this.motionZ = 0;

//Start homing
                        this.setCreeperHoming(true);

//Get target direction
                        double dx = rocketCreeperAttackTarget.posX - this.posX;
                        double dy = rocketCreeperAttackTarget.posY + (rocketCreeperAttackTarget.getEyeHeight() * 0.5D) - this.posY;
                        double dz = rocketCreeperAttackTarget.posZ - this.posZ;

//Get distance
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        
//Normalize vector
                        if (distance != 0) {
                            dx /= distance;
                            dy /= distance;
                            dz /= distance;
                        }
                    
//Multiply to get the speed
                        this.motionX = (dx * specialStartSpeed); 
                        this.motionY = (dy * specialStartSpeed);
                        this.motionZ = (dz * specialStartSpeed);

                        this.world.playSound(null, 
                        rocketCreeperAttackTarget.posX, rocketCreeperAttackTarget.posY, rocketCreeperAttackTarget.posZ,
                        SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.NEUTRAL, 3.0F, 0.5F);

                        this.prepareTicks = 0;
                        this.setCreeperPreparing(false);
                    } 
                }
//This is the homing logic of the special attack 
                else if (this.getCreeperHoming() == true)
                {
//Explodes if homing for too long,
//if collided, or if close enough to target
                    if((this.specialHomingTicksStart++ >= specialHomingTicksMax)
                    || (this.collidedHorizontally || this.collidedVertically) 
                    || Math.pow(this.specialExplodeDistance, 2) >= this.getDistanceSq(rocketCreeperAttackTarget))
                    {
    		            this.explode(this.creeperSpecialExplosionRadius, this.chargedMultiplier, true);
                    }
                    
//Same motion logic but with acceleration

//Get target direction
                    double dx = rocketCreeperAttackTarget.posX - this.posX;
                    double dy = (rocketCreeperAttackTarget.posY + rocketCreeperAttackTarget.getEyeHeight() * 0.5D) - this.posY;
                    double dz = rocketCreeperAttackTarget.posZ - this.posZ;

//Get distance
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    
//Normalize vector
                    if (distance != 0) {
                        dx /= distance;
                        dy /= distance;
                        dz /= distance;
                    }

//Apply acceleration factor
                    specialCurrentSpeed *= specialAcceleration;
                
//Multiply to get the speed
                    this.motionX = (dx * specialCurrentSpeed);
                    this.motionY = (dy * specialCurrentSpeed);
                    this.motionZ = (dz * specialCurrentSpeed);
                }
            }
            else
            {
                this.resetCreeperSpecial();
            }
        }
//Normal explosion logic
        else
        {
//Do not execute while stunned   
            if(!entityMobMixin.getAbsurdcraftStunned())
            {
//Immediately reset if target goes out of normal explosion range
                if(rocketCreeperAttackTarget != null )
                {
                	if(this.getDistanceSq(rocketCreeperAttackTarget) > Math.pow(this.detonationDistance, 2))
                	{
          		        this.resetCreeper();
                	}
                }
                
            	if(this.getCreeperState() > 0)
            	{
            		timeBeforeJumping++;
            	}
                else
            	{
            		timeBeforeJumping = 0;
            	}

                if (timeBeforeJumping > this.detonationTimerMax && (this.isEntityAlive() && getAttackTarget() != null && this.hasEnoughSpaceToJump(getAttackTarget())))
                {
//           	    this.resetCreeper();
                	rocketCreeperMixin.setCreeperIgnitedTime(0);
                    int var1 = this.getCreeperState();

                    if (var1 > 0 && onGround)
                    { 
                    	if(getEntityWorld().isRemote)
                    	{
                    		getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextFloat() - rand.nextFloat()), posY - (rand.nextFloat() - rand.nextFloat()) - 1F, posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
                    	}
                		this.playSound(SoundEvents.ENTITY_FIREWORK_LAUNCH, 2.0F, 0.5F);
                        motionY = 1.2000000476837158D;
                        motionX = (getAttackTarget().posX - posX) / 6D;
                        motionZ = (getAttackTarget().posZ - posZ) / 6D;
                        setCreeperRocket(true);
                    }
                }
            }
            else
            {
                this.resetCreeper();
            }  
        }
    }
    
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_ROCKETCREEPER;
    }
    
    public void setCreeperRocket(boolean rocket)
    {
        this.getDataManager().set(IS_ROCKET, Boolean.valueOf(rocket));
    }
    
    public boolean isRocket()
    {
        return ((Boolean)this.getDataManager().get(IS_ROCKET)).booleanValue();
    }
    
    private void spawnLingeringCloud()
    {
        Collection<PotionEffect> collection = this.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
            entityareaeffectcloud.setRadius(2.5F);
            entityareaeffectcloud.setRadiusOnUse(-0.5F);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());

            for (PotionEffect potioneffect : collection)
            {
                entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
            }

            this.world.spawnEntity(entityareaeffectcloud);
        }
    }
    
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	if(type == EnumCreatureType.MONSTER){return false;}
    	return super.isCreatureType(type, forSpawnCount);
    }

//Custom one just to make sure it also resets time before jumping
    public void resetCreeper()
    {
        rocketCreeperMixin.setCreeperIgnitedTime(-10);
        this.setCreeperState(-1);
        this.timeBeforeJumping = 0;       
    }


//Make sure it won't do special while rocketing
    public boolean creeperSpecialConditions()
    {
        return !(this.isRocket());
    }

//Reset all special attack logic except cooldown
    public void resetCreeperSpecial()
    {
        rocketCreeperMixin.setCreeperSpecialIgnitedTime(0);
        rocketCreeperMixin.setCreeperStateSpecial(-1);
        this.setCreeperPreparing(false);
        this.setNoGravity(false);
        this.setCreeperPreparingIsPastFirstTick(false);
        this.prepareTicks = 0;
        this.specialCurrentSpeed = this.specialStartSpeed;
        this.setCreeperHoming(false);
        this.specialHomingTicksStart = 0;
    }

    public void creeperSpecialAttemptSound(double atX, double atY, double atZ)
    {
        EntityLivingBase rocketCreeperAttackTarget = this.getAttackTarget();

        this.world.playSound(null, 
        rocketCreeperAttackTarget.posX, rocketCreeperAttackTarget.posY, rocketCreeperAttackTarget.posZ,
        PrimitiveMobsSoundEvents.ENTITY_CREEPER_NUKE, SoundCategory.NEUTRAL, 3.0F, 1.0F);
    }


    public boolean getCreeperPreparing()
    {
        return ((Boolean)this.dataManager.get(IS_PREPARING)).booleanValue();
    }
    public boolean getCreeperPreparingIsPastFirstTick()
    {
        return ((Boolean)this.dataManager.get(IS_PREPARING_PAST_FIRST_TICK)).booleanValue();
    }

    public boolean getCreeperHoming()
    {
        return ((Boolean)this.dataManager.get(IS_HOMING)).booleanValue();
    }

    public void setCreeperPreparing(boolean preparing)
    {
        this.getDataManager().set(IS_PREPARING, Boolean.valueOf(preparing));
    }

    public void setCreeperPreparingIsPastFirstTick(boolean preparing_is_past_first_tick)
    {
        this.getDataManager().set(IS_PREPARING_PAST_FIRST_TICK, Boolean.valueOf(preparing_is_past_first_tick));
    }

    public void setCreeperHoming(boolean homing)
    {
        this.getDataManager().set(IS_HOMING, Boolean.valueOf(homing));
    }
}
