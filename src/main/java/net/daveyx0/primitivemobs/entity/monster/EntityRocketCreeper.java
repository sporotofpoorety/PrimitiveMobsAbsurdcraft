package net.daveyx0.primitivemobs.entity.monster;

import java.util.Collection;

import javax.annotation.Nullable;

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

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.util.EntityUtil;

import org.sporotofpoorety.eternitymode.client.ExplosiveHandler;
import org.sporotofpoorety.eternitymode.core.EternityModeSoundEvents;

import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAICreeperSwellSpecial;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;




public class EntityRocketCreeper extends EntityPrimitiveCreeper implements IMultiMob {

//Access getters and setters of these mixins
    public IMixinEntityCreeper rocketCreeperMixin;


//Non-special handlers
	int timeBeforeJumping;
	private static final DataParameter<Boolean> IS_ROCKET = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);


//Non-special configs
    int detonationTimerMax;
    double detonationDistance;
    boolean alwaysJumps;


//Both configs
    protected float chargedMultiplier;


//Special handlers
    private static final DataParameter<Boolean> IS_PREPARING = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_PREPARING_PAST_FIRST_TICK = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private int prepareTicks;
    private static final DataParameter<Boolean> IS_HOMING = EntityDataManager.<Boolean>createKey(EntityRocketCreeper.class, DataSerializers.BOOLEAN);
    private int specialHomingTicksStart;
    private double specialCurrentSpeed;


//Special configs specific to this mob
    private double specialStartSpeed;
    private double specialAcceleration; 
    private double specialExplodeDistance;
    private int specialHomingTicksMax;
	protected int creeperSpecialExplosionRadius;



//Constructor
	public EntityRocketCreeper(World worldIn) {
		super(worldIn);


//Access getters and setters of these mixins
        rocketCreeperMixin = (IMixinEntityCreeper) this;


//Non-special handlers
        timeBeforeJumping = 0;
		setCreeperRocket(false);


//Non-special configs
        detonationTimerMax = PrimitiveMobsConfigSpecial.getRocketCreeperDetonationTimerMax();
        detonationDistance = PrimitiveMobsConfigSpecial.getRocketCreeperDetonationDistance();
        alwaysJumps = PrimitiveMobsConfigSpecial.getRocketCreeperAlwaysJump();
//Already handled by vanilla NBT
        rocketCreeperMixin.setCreeperExplosionRadius(PrimitiveMobsConfigSpecial.getRocketCreeperExplosionPower());


//Both configs
        chargedMultiplier = (float) PrimitiveMobsConfigSpecial.getRocketCreeperChargedMultiplier();


//Special handlers
        setCreeperPreparing(false);
        setCreeperPreparingIsPastFirstTick(false);
        prepareTicks = 0;
        setCreeperHoming(false);
        specialHomingTicksStart = 0;
        specialCurrentSpeed = 69420.0D;


//Base special configs
        rocketCreeperMixin.setCreeperSpecialEnabled(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialEnabled());
        rocketCreeperMixin.setCreeperSpecialCooldownInterrupted(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownInterrupted());
        rocketCreeperMixin.setCreeperSpecialCooldownAttacked(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownAttacked());
        rocketCreeperMixin.setCreeperSpecialCooldownFrustrated(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownFrustrated());
        rocketCreeperMixin.setCreeperSpecialCooldownOver(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownOver());
        rocketCreeperMixin.setCreeperSpecialCooldownStunned(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialCooldownStunned());
        rocketCreeperMixin.setCreeperSpecialStunnedDuration(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialStunnedDuration());
        rocketCreeperMixin.setCreeperSpecialIgnitedTimeMax(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialIgnitedTimeMax());
        rocketCreeperMixin.setCreeperSpecialInterruptedMax(PrimitiveMobsConfigSpecial.getRocketCreeperSpecialInterruptedMax());
        rocketCreeperMixin.setCreeperSpecialInterruptedDamage((float) PrimitiveMobsConfigSpecial.getRocketCreeperSpecialInterruptedDamage());


//Specific to this mob
        specialStartSpeed = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialStartSpeed();
//Now give current speed initial value
        specialCurrentSpeed = specialStartSpeed;
        specialAcceleration = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialAcceleration();
        specialExplodeDistance = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialExplodeDistance();
        specialHomingTicksMax = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialMaxTicks();
        creeperSpecialExplosionRadius = PrimitiveMobsConfigSpecial.getRocketCreeperSpecialExplosionPower();



//If task absent
        if(!TaskUtils.mobHasTask(this, EntityAICreeperSwellSpecial.class))
        {
//And task enabled in config
            if (rocketCreeperMixin.getCreeperSpecialEnabled())
            {
//Add the task
                this.tasks.addTask(2, new EntityAICreeperSwellSpecial(this));
            }
        }
//If task is here...
        else
        {
//And task disabled in config
            if (!rocketCreeperMixin.getCreeperSpecialEnabled())
            {
//Remove the task
                TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
            }                
        }
    }
	


	
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


//Non-special handlers
        compound.setInteger("CreeperTimeBeforeJumping", this.timeBeforeJumping);
        compound.setBoolean("CreeperRocket", this.isRocket());


//Non-special configs
        compound.setInteger("CreeperDetonationTimerMax", this.detonationTimerMax);
        compound.setDouble("CreeperDetonationDistance", this.detonationDistance);
        compound.setBoolean("CreeperAlwaysJumps", this.alwaysJumps);


//Both configs
        compound.setFloat("CreeperChargedMultiplier", this.chargedMultiplier);


//Special handlers
        compound.setBoolean("CreeperPreparing", this.getCreeperPreparing());
        compound.setBoolean("CreeperPreparingIsPastFirstTick", this.getCreeperPreparingIsPastFirstTick());
        compound.setInteger("CreeperPrepareTicks", this.prepareTicks);
        compound.setBoolean("CreeperHoming", this.getCreeperHoming());
        compound.setInteger("CreeperSpecialHomingTicksStart", this.specialHomingTicksStart);
        compound.setDouble("CreeperSpecialCurrentSpeed", this.specialCurrentSpeed);


//Special configs specific to this mob
        compound.setDouble("CreeperSpecialStartSpeed", this.specialStartSpeed);
        compound.setDouble("CreeperSpecialAcceleration", this.specialAcceleration);
        compound.setDouble("CreeperSpecialExplodeDistance", this.specialExplodeDistance);
        compound.setInteger("CreeperSpecialHomingTicksMax", this.specialHomingTicksMax);
        compound.setInteger("CreeperSpecialExplosionRadius", this.creeperSpecialExplosionRadius);
    }




    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


//Non-special handlers
        if (compound.hasKey("CreeperTimeBeforeJumping")) { this.timeBeforeJumping = compound.getInteger("CreeperTimeBeforeJumping"); }
        if (compound.hasKey("CreeperRocket")) { this.setCreeperRocket(compound.getBoolean("CreeperRocket")); }


//Non-special configs
        if (compound.hasKey("CreeperDetonationTimerMax")) { this.detonationTimerMax = compound.getInteger("CreeperDetonationTimerMax"); }
        if (compound.hasKey("CreeperDetonationDistance")) { this.detonationDistance = compound.getDouble("CreeperDetonationDistance"); }
        if (compound.hasKey("CreeperAlwaysJumps")) { this.alwaysJumps = compound.getBoolean("CreeperAlwaysJumps"); }


//Both configs
        if (compound.hasKey("CreeperChargedMultiplier")) { this.chargedMultiplier = compound.getFloat("CreeperChargedMultiplier"); }


//Special handlers
        if (compound.hasKey("CreeperPreparing")) { this.setCreeperPreparing(compound.getBoolean("CreeperPreparing")); }
        if (compound.hasKey("CreeperPreparingIsPastFirstTick")) { this.setCreeperPreparingIsPastFirstTick(compound.getBoolean("CreeperPreparingIsPastFirstTick")); }
        if (compound.hasKey("CreeperPrepareTicks")) { this.prepareTicks = compound.getInteger("CreeperPrepareTicks"); }
        if (compound.hasKey("CreeperHoming")) { this.setCreeperHoming(compound.getBoolean("CreeperHoming")); }
        if (compound.hasKey("CreeperSpecialHomingTicksStart")) { this.specialHomingTicksStart = compound.getInteger("CreeperSpecialHomingTicksStart"); }
        if (compound.hasKey("CreeperSpecialCurrentSpeed")) { this.specialCurrentSpeed = compound.getDouble("CreeperSpecialCurrentSpeed"); }


//Special configs specific to this mob
        if (compound.hasKey("CreeperSpecialStartSpeed")) { this.specialStartSpeed = compound.getDouble("CreeperSpecialStartSpeed"); }
        if (compound.hasKey("CreeperSpecialAcceleration")) { this.specialAcceleration = compound.getDouble("CreeperSpecialAcceleration"); }
        if (compound.hasKey("CreeperSpecialExplodeDistance")) { this.specialExplodeDistance = compound.getDouble("CreeperSpecialExplodeDistance"); }
        if (compound.hasKey("CreeperSpecialHomingTicksMax")) { this.specialHomingTicksMax = compound.getInteger("CreeperSpecialHomingTicksMax"); }
        if (compound.hasKey("CreeperSpecialExplosionRadius")) { this.creeperSpecialExplosionRadius = compound.getInteger("CreeperSpecialExplosionRadius"); }


//Remove task if present, and 
        TaskUtils.mobRemoveTaskIfPresent(this, EntityAICreeperSwellSpecial.class);
//Only reassign if enabled in NBT, NBT values can also override config ones
        if (rocketCreeperMixin.getCreeperSpecialEnabled()) 
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
    	boolean fromAllHeights = true;

    	if (!this.alwaysJumps) 
        {
	    	for(int rayElevation = 0; rayElevation < 5; rayElevation++)
	    	{
	    		fromAllHeights = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight() + rayElevation, this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false) == null;
	    	}
    	}

        return fromAllHeights;
    }
    
    public void onUpdate()
    {
        super.onUpdate();
//Get target
        EntityLivingBase rocketCreeperAttackTarget = this.getAttackTarget();

//Rocket Creeper specific logic for special attack
        if(rocketCreeperMixin.getCreeperStateSpecial() > 0
//Special swell task itself already is
//specified to be mutually exclusive with normal swell, but not with isRocket
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
                        SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.HOSTILE, 3.0F, 0.5F);

//Adjust to target height
                        double targetHeightFactor = 1.0D;
//If target above and above Y 100
                        if(rocketCreeperAttackTarget.posY > this.posY
                        && (rocketCreeperAttackTarget.posY > 100.0D))
                        {
//Scale up to 150
                            targetHeightFactor += ((rocketCreeperAttackTarget.posY - 100.0D) / 50.0D); 
                        }
//Up to power of 2
                        Math.pow(5.0D, Math.min(2.0D, targetHeightFactor));
//Jump extremely fast
                        this.motionY = 5.0D * targetHeightFactor;
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
                        SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.HOSTILE, 3.0F, 0.5F);

                        this.prepareTicks = 0;
                        this.setCreeperPreparing(false);
                    } 
                }
//This is the homing logic of the special attack 
                else if (this.getCreeperHoming() == true)
                {
//If homing too long
                    if((this.specialHomingTicksStart++ >= specialHomingTicksMax)
//If collided
                    || (this.collidedHorizontally || this.collidedVertically) 
//Or if close enough to target
                    || Math.pow(this.specialExplodeDistance, 2) >= this.getDistanceSq(rocketCreeperAttackTarget))
                    {
//Explode
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
//Immediately reset if target is far away
            if(rocketCreeperAttackTarget != null)
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
//           	this.resetCreeper();
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
        rocketCreeperMixin.setCreeperIgnitedTime(0);
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
        EternityModeSoundEvents.ENTITY_CREEPER_NUKE, SoundCategory.HOSTILE, 3.0F, 1.0F);
    }

    public void creeperSpecialParticles()
    {
        if(this.getCreeperHoming())
        {
            if(this.ticksExisted % 2 == 0)
            {
//Particles behind homing direction
                double atX = this.posX + (this.motionX * -0.2D);
                double atY = this.posY + (this.height / 2.0D) + (this.motionY * -0.2D);
                double atZ = this.posZ + (this.motionZ * -0.2D);

                ExplosiveHandler.spawnParticles(this.world, atX, atY, atZ,
                    1.0F, false, false);
            }
        }
        else if(this.getCreeperPreparing())
        {
            if(this.ticksExisted % 2 == 0)
            {
//Particles below when jumping
                ExplosiveHandler.spawnParticles(this.world, this.posX, this.posY, this.posZ,
                    1.0F, false, false);
            }

//Inital burst when jumping
            if(!(this.getCreeperPreparingIsPastFirstTick()))
            {
                ExplosiveHandler.spawnParticles(this.world, this.posX, this.posY, this.posZ,
                    6.0F, false, false);
            }
        }
        else
        {
//Particles around for clear warning sign
            if(this.ticksExisted % 2 == 0)
            {
                double atX = this.posX + (3.0D * (rand.nextDouble() - rand.nextDouble()));
                double atY = this.posY + (22.0D * rand.nextDouble());
                double atZ = this.posZ + (3.0D * (rand.nextDouble() - rand.nextDouble()));

                ExplosiveHandler.spawnParticles(this.world, atX, atY, atZ,
                    1.0F, false, false);
            }

//And below as a small touch
            if(this.ticksExisted % 10 == 0)
            {
                ExplosiveHandler.spawnParticles(this.world, this.posX, this.posY, this.posZ,
                    1.0F, false, false);
            }             
        }
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
