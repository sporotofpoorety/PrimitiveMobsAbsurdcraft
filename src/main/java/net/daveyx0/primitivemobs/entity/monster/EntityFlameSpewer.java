package net.daveyx0.primitivemobs.entity.monster;

import javax.annotation.Nullable;

import net.daveyx0.multimob.entity.IMultiMob;
import net.daveyx0.multimob.entity.IMultiMobLava;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsLootTables;
import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAIFlameSpewAttack;
import net.daveyx0.primitivemobs.entity.item.EntityFlameSpit;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityFlameSpewer extends EntityMob implements IRangedAttackMob, IMultiMobLava {

    private static final DataParameter<Byte> ON_FIRE = EntityDataManager.<Byte>createKey(EntityFlameSpewer.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> IN_DANGER = EntityDataManager.<Byte>createKey(EntityFlameSpewer.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> NEXT_COUNTDOWN = EntityDataManager.<Integer>createKey(EntityFlameSpewer.class, DataSerializers.VARINT);
    private static final DataParameter<Float> VISUAL_STATE = EntityDataManager.<Float>createKey(EntityFlameSpewer.class, DataSerializers.FLOAT);

    private int preAttackCountdownMax;
    protected int shotLifetime;
    protected int shotParticles;
    private int goInvulnerableWhen;
    private int goVulnerableWhen;
    private int attackRapidfireShots;
    protected int attackRapidfireInterval;
    protected double attackRapidfireSpread;

    
	public EntityFlameSpewer(World worldIn) {
		super(worldIn);

		this.isImmuneToFire = true;
		this.setOnFire(false);
		this.setInDanger(false);
		this.setNextActionCountdown(10);
		this.setVisualState(0);
		this.setSize(1f, 1.25f);
		this.setPathPriority(PathNodeType.LAVA, 10);

        this.preAttackCountdownMax = PrimitiveMobsConfigSpecial.getFlameSpewerPreAttackCountdownMax();
        this.shotLifetime = PrimitiveMobsConfigSpecial.getFlameSpewerShotLifetime();
        this.shotParticles = PrimitiveMobsConfigSpecial.getFlameSpewerShotParticles();
        this.goInvulnerableWhen = PrimitiveMobsConfigSpecial.getFlameSpewerGoInvulnerableWhen();
        this.goVulnerableWhen = PrimitiveMobsConfigSpecial.getFlameSpewerGoVulnerableWhen();
        this.attackRapidfireShots = PrimitiveMobsConfigSpecial.getflameSpewerAttackRapidfireShots();
        this.attackRapidfireInterval = PrimitiveMobsConfigSpecial.getFlameSpewerAttackRapidfireInterval();
        this.attackRapidfireSpread = PrimitiveMobsConfigSpecial.getFlameSpewerAttackRapidfireSpread();

		this.tasks.addTask(4, new EntityAIFlameSpewAttack(this, this.preAttackCountdownMax, this.shotLifetime, this.shotParticles,
        this.goInvulnerableWhen, this.goVulnerableWhen, this.attackRapidfireShots, this.attackRapidfireInterval, this.attackRapidfireSpread));
	}

	protected void initEntityAI()
    {
        //this.tasks.addTask(++prio, new EntityAISwimming(this));
		this.tasks.addTask(3, new EntityFlameSpewer.AIGoToLava(this));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    @Override 
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        super.onInitialSpawn(difficulty, livingdata);

        return livingdata;
    }

	
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(ON_FIRE, Byte.valueOf((byte)0));
        this.dataManager.register(IN_DANGER, Byte.valueOf((byte)0));
        this.getDataManager().register(NEXT_COUNTDOWN, Integer.valueOf(0));
        this.getDataManager().register(VISUAL_STATE, Float.valueOf(0));
    }
    
    protected SoundEvent getAmbientSound()
    {
        return PrimitiveMobsSoundEvents.ENTITY_FLAMESPEWER_IDLE;
    }
    
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }
    
    /**
     * Returns whether this Entity is invulnerable to the given DamageSource.
     */
    public boolean isEntityInvulnerable(DamageSource source)
    {
//      return this.getNextActionCountdown() < 10 || this.getVisualState() > 0;
        return !this.isOnFire();
    }

    
    public void setNextActionCountdown(int time)
    {
        this.getDataManager().set(NEXT_COUNTDOWN, Integer.valueOf(time));
    }
    
    public void setVisualState(float visualState)
    {
        this.getDataManager().set(VISUAL_STATE, Float.valueOf(visualState));
    }
    
    public int getNextActionCountdown()
    {
        return ((Integer)this.getDataManager().get(NEXT_COUNTDOWN)).intValue();
    }
    
    public float getVisualState()
    {
        return ((Float)this.getDataManager().get(VISUAL_STATE)).floatValue();
    }
    
	
	public void onUpdate()
	{
		super.onUpdate();
	    if(this.isInLava() || this.isInWater())
	    {
	    	if(!this.getEntityWorld().isAirBlock(new BlockPos(this.posX, this.posY + 0.5D, this.posZ)))
	    	{	
	    		motionY = 0.1D;
	    	}
	    	else
	    	{
	    		motionY = 0.0D;
	    	}
	    	
	    	if(this.isInWater() && this.ticksExisted % 15 == 0)
	    	{
	    		this.attackEntityFrom(DamageSource.DROWN, 4);
	    	}
	    }
	    else
	    {
	    	if(this.ticksExisted % 25 == 0)
	    	{
	    		this.attackEntityFrom(DamageSource.DROWN, 1);
    			this.jump();
    			this.setMoveForward(1);
	    	}
	    }
	    
	    //MultiMob.LOGGER.info(this.getNextActionCountdown() + " " + this.getVisualState() + " " + this.isOnFire());
	}
	
    /**
     * Drops an item at the position of the entity.
     */
	@Override
    @Nullable
    public EntityItem entityDropItem(ItemStack stack, float offsetY)
    {
        if (stack.isEmpty())
        {
            return null;
        }
        else
        {
            EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY + 1.5D, this.posZ, stack);
            entityitem.setDefaultPickupDelay();
        	for(int i = 0; i < 50; i++)
        	{
        		Vec3d vec = RandomPositionGenerator.getLandPos(this, 10, 7);
        		if(vec != null)
        		{

            		entityitem.motionX = (vec.x - entityitem.posX) / 18D;
	    			entityitem.motionY = (vec.y - entityitem.posY) / 18D+ 0.5D;
	    			entityitem.motionZ = (vec.z - entityitem.posZ) / 18D;	
	    			break;
        		}
        	}
            if (captureDrops)
            {
                this.capturedDrops.add(entityitem);
            }
            else
            {
                this.world.spawnEntity(entityitem);
            }
            
            
            return entityitem;
        }
    }

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
	
		
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {

		
	}
	
    @Nullable
    protected ResourceLocation getLootTable()
    {
        return PrimitiveMobsLootTables.ENTITIES_FLAMESPEWER;
    }
	
	
    public boolean isOnFire()
    {
        return (((Byte)this.dataManager.get(ON_FIRE)).byteValue() & 1) != 0;
    }

    public void setOnFire(boolean onFire)
    {
        byte b0 = ((Byte)this.dataManager.get(ON_FIRE)).byteValue();

        if (onFire)
        {
            b0 = (byte)(b0 | 1);
        }
        else
        {
            b0 = (byte)(b0 & -2);
        }

        this.dataManager.set(ON_FIRE, Byte.valueOf(b0));
    }
    
    public boolean isInDanger()
    {
        return (((Byte)this.dataManager.get(IN_DANGER)).byteValue() & 1) != 0;
    }

    public void setInDanger(boolean OnFire)
    {
        byte b0 = ((Byte)this.dataManager.get(IN_DANGER)).byteValue();

        if (OnFire)
        {
            b0 = (byte)(b0 | 1);
        }
        else
        {
            b0 = (byte)(b0 & -2);
        }

        this.dataManager.set(IN_DANGER, Byte.valueOf(b0));
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }
    
    static class AIGoToLava extends EntityAIMoveToBlock
    {
        private final EntityFlameSpewer spewer;

        public AIGoToLava(EntityFlameSpewer spewer)
        {
            super(spewer, 0.699999988079071D, 25);
            this.spewer = spewer;
        }
        
        public boolean shouldExecute()
        {
           return !this.spewer.isInLava() && super.shouldExecute();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            super.updateTask();
            this.spewer.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.spewer.getVerticalFaceSpeed());

            if (this.getIsAboveDestination())
            {
                this.runDelay = 10;
            }
        }

        /**
         * Return true to set given position as destination
         */
        protected boolean shouldMoveTo(World worldIn, BlockPos pos)
        {
            Block block = worldIn.getBlockState(pos).getBlock();
            BlockPos tempPos = pos;
            
            if (block == Blocks.LAVA)
            {
                if (worldIn.isAirBlock(pos.up()))
                {
                	if(worldIn.getBlockState(pos.east()).getBlock() == Blocks.LAVA && worldIn.getBlockState(pos.west()).getBlock() == Blocks.LAVA
                			&& worldIn.getBlockState(pos.north()).getBlock() == Blocks.LAVA && worldIn.getBlockState(pos.south()).getBlock() == Blocks.LAVA)
                	{
                        return true;
                	}
                }
            }

            return false;
        }
    }
    
    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    @Override
    public boolean isNotColliding()
    {
        return this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }
    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere()
    {
        return this.posY < 64;
    }
	
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	if(type == EnumCreatureType.MONSTER){return false;}
    	return super.isCreatureType(type, forSpawnCount);
    }




	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        compound.setInteger("NextActionCountdown", this.getNextActionCountdown());
        compound.setFloat("VisualState", this.getVisualState());
        compound.setBoolean("OnFire", this.isOnFire());
        compound.setBoolean("InDanger", this.isInDanger());

//Preserves field values including assigned by NBT
        compound.setInteger("PreAttackCountdownMax", preAttackCountdownMax);
        compound.setInteger("ShotLifetime", shotLifetime);
        compound.setInteger("ShotParticles", shotParticles);
        compound.setInteger("GoInvulnerableWhen", goInvulnerableWhen);
        compound.setInteger("GoVulnerableWhen", goVulnerableWhen);
        compound.setInteger("AttackRapidfireShots", attackRapidfireShots);
        compound.setInteger("AttackRapidfireInterval", attackRapidfireInterval);
        compound.setDouble("AttackRapidfireSpread", attackRapidfireSpread);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        this.setNextActionCountdown(compound.getInteger("NextActionCountdown"));
        this.setVisualState(compound.getFloat("VisualState"));
        this.setOnFire(compound.getBoolean("OnFire"));
        this.setInDanger(compound.getBoolean("InDanger"));

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("PreAttackCountdownMax")) { this.preAttackCountdownMax = compound.getInteger("PreAttackCountdownMax"); }
        if (compound.hasKey("ShotLifetime")) { this.shotLifetime = compound.getInteger("ShotLifetime"); }
        if (compound.hasKey("ShotParticles")) { this.shotParticles = compound.getInteger("ShotParticles"); }
        if (compound.hasKey("GoInvulnerableWhen")) { this.goInvulnerableWhen = compound.getInteger("GoInvulnerableWhen"); }
        if (compound.hasKey("GoVulnerableWhen")) { this.goVulnerableWhen = compound.getInteger("GoVulnerableWhen"); }
        if (compound.hasKey("AttackRapidfireShots")) { this.attackRapidfireShots = compound.getInteger("AttackRapidfireShots"); }
        if (compound.hasKey("AttackRapidfireInterval")) { this.attackRapidfireInterval = compound.getInteger("AttackRapidfireInterval"); }
        if (compound.hasKey("AttackRapidfireSpread")) { this.attackRapidfireSpread = compound.getDouble("AttackRapidfireSpread"); }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIFlameSpewAttack.class))
        {
		    this.tasks.addTask(4, new EntityAIFlameSpewAttack(this, this.preAttackCountdownMax, this.shotLifetime, this.shotParticles,
            this.goInvulnerableWhen, this.goVulnerableWhen, this.attackRapidfireShots, this.attackRapidfireInterval, this.attackRapidfireSpread));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIFlameSpewAttack.class);

		    this.tasks.addTask(4, new EntityAIFlameSpewAttack(this, this.preAttackCountdownMax, this.shotLifetime, this.shotParticles,
            this.goInvulnerableWhen, this.goVulnerableWhen, this.attackRapidfireShots, this.attackRapidfireInterval, this.attackRapidfireSpread));       
        }

    }
}
