package net.daveyx0.primitivemobs.entity.monster;

import net.daveyx0.multimob.common.capabilities.CapabilityTameableEntity;
import net.daveyx0.multimob.common.capabilities.ITameableEntity;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.entity.EntityCreeperTameable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityPrimitiveCreeper extends EntityCreeper
{
    protected int timeSinceIgnited;
	private int lastActiveTime;
	
    private static final DataParameter<Boolean> BABY = EntityDataManager.<Boolean>createKey(EntityPrimitiveCreeper.class, DataSerializers.BOOLEAN);
    protected int growingAge;
    protected int forcedAge;
    protected int forcedAgeTimer;
    private float ageWidth = -1.0F;
    private float ageHeight;

//Added new fields to base class to enable modular special swell

	protected float explosionRadius;
    protected int creeperSpecialCooldown;
    protected int creeperSpecialCooldownInterrupted;
    protected int creeperSpecialCooldownAttacked;
    protected int creeperSpecialCooldownFrustrated;
    protected int creeperSpecialCooldownStunned;
    protected int creeperSpecialIgnitedTime;
//Placeholder for base class
    protected int creeperSpecialIgnitedTimeMax;
    protected float creeperSpecialInterruptedDamage;
    protected int creeperSpecialInterrupted;
    protected int creeperSpecialInterruptedMax;

//And new data parameter
    protected static final DataParameter<Integer> STATE_SPECIAL = EntityDataManager.<Integer>createKey(EntityPrimitiveCreeper.class, DataSerializers.VARINT);


	public EntityPrimitiveCreeper(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.7F);

//New
        setCreeperStateSpecial(-1);

//These, by themselves,
//won't do anything, but gotta have 
//values to prevent null pointer exceptions
        creeperSpecialCooldown = 0;
        creeperSpecialCooldownInterrupted = 69420;
        creeperSpecialCooldownAttacked = 69420;
        creeperSpecialCooldownFrustrated = 69420;
        creeperSpecialCooldownStunned = 69420;
        creeperSpecialIgnitedTime = 0;
        creeperSpecialInterrupted = 0;
    }
    
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
//Below is base special swell logic
        if(this.creeperSpecialCooldown > 0)
        {
            this.creeperSpecialCooldown--;
        }

        //Cancel special swell if state negative
        if(this.getCreeperStateSpecial() < 1)
        {
            creeperSpecialIgnitedTime = 0;
        }
        
        //Special swell if has target and state set
        if(this.getAttackTarget() != null && this.getCreeperStateSpecial() > 0)
        {
            //Increase special ignited time
            creeperSpecialIgnitedTime++;
        }

    	//Disable normal explosion`
    	if(this instanceof EntityFestiveCreeper)
    	{
        	timeSinceIgnited = 0;
    	}
    	
        if(this.getAttackTarget() != null )
        {
    		ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
    		if(tameable != null && tameable.isTamed() &&  tameable.getFollowState() == 0)
    		{
        		this.setIgnitedTime(0);
        		this.setCreeperState(-1);
                this.setCreeperStateSpecial(-1);
    		}
        }
    	
        super.onUpdate();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
		ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
		if(tameable != null && tameable.isTamed())
		{
			return source != DamageSource.FIREWORKS && source != DamageSource.IN_WALL  && source != DamageSource.FALL && source != DamageSource.DROWN && super.attackEntityFrom(source, amount);
		}

        //If it takes arrow or melee damage immediately cancel special attack and apply cooldown
        if((amount >= (float) creeperSpecialInterruptedDamage) 
        && (source.damageType.equals("arrow") || source.damageType.equals("mob") || source.damageType.equals("player")))
        {
            //If attacked in the middle of the swell
            if(this.getCreeperStateSpecial() > 0)
            {
                //Apply stun
                this.playSound(PrimitiveMobsSoundEvents.ENTITY_CREEPER_DIZZY, 3.0F, 1.0F);
                this.setCreeperSpecialCooldown(this.creeperSpecialCooldownStunned);     
            }
            //If just attacked normally there's a smaller cooldown 
            else
            {
                this.setCreeperSpecialCooldown(this.creeperSpecialCooldownAttacked);     
            }

            this.resetCreeperSpecial();        
        }

        return super.attackEntityFrom(source, amount);
    }

    public boolean creeperSpecialConditions()
    {
        return true;
    }
    
    public void setIgnitedTime(int time)
    {
    	this.timeSinceIgnited = time;
    }

    public void resetCreeperSpecial()
    {
        this.creeperSpecialIgnitedTime = 0;
        this.setCreeperStateSpecial(-1);
    }
    
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(BABY, Boolean.valueOf(false));
        this.getDataManager().register(STATE_SPECIAL, Integer.valueOf(-1));
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. Don't confuse this with EntityLiving.getAge. With a negative value the
     * Entity is considered a child.
     */
    public int getGrowingAge()
    {
        if (this.world.isRemote)
        {
            return ((Boolean)this.dataManager.get(BABY)).booleanValue() ? -1 : 1;
        }
        else
        {
            return this.growingAge;
        }
    }

    /**
     * Increases this entity's age, optionally updating {@link #forcedAge}. If the entity is an adult (if the entity's
     * age is greater than or equal to 0) then the entity's age will be set to {@link #forcedAge}.
     *  
     * @param growthSeconds Number of seconds to grow this entity by. The entity's age will be increased by 20 times
     * this number (i.e. this number converted to ticks).
     * @param updateForcedAge If true, updates {@link #forcedAge} and {@link #forcedAgeTimer}
     */
    public void ageUp(int growthSeconds, boolean updateForcedAge)
    {
        int i = this.getGrowingAge();
        int j = i;
        i = i + growthSeconds * 20;

        if (i > 0)
        {
            i = 0;

            if (j < 0)
            {
                this.onGrowingAdult();
            }
        }

        int k = i - j;
        this.setGrowingAge(i);

        if (updateForcedAge)
        {
            this.forcedAge += k;

            if (this.forcedAgeTimer == 0)
            {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getGrowingAge() == 0)
        {
            this.setGrowingAge(this.forcedAge);
        }
    }

    /**
     * Increases this entity's age. If the entity is an adult (if the entity's age is greater than or equal to 0) then
     * the entity's age will be set to {@link #forcedAge}. This method does not update {@link #forcedAge}.
     */
    public void addGrowth(int growth)
    {
        this.ageUp(growth, false);
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. With a negative value the Entity is considered a child.
     */
    public void setGrowingAge(int age)
    {
        this.dataManager.set(BABY, Boolean.valueOf(age < 0));
        this.growingAge = age;
        this.setScaleForAge(this.isChild());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("Age", this.getGrowingAge());
        compound.setInteger("ForcedAge", this.forcedAge);
//New
        compound.setInteger("SpecialState", this.getCreeperStateSpecial());

//Important states
        compound.setInteger("SpecialCooldown", this.creeperSpecialCooldown);
        compound.setInteger("SpecialIgnitedTime", this.creeperSpecialIgnitedTime);
        compound.setInteger("SpecialInterrupted", this.creeperSpecialInterrupted);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setGrowingAge(compound.getInteger("Age"));
        this.forcedAge = compound.getInteger("ForcedAge");
//Safety checks
        if (compound.hasKey("SpecialState")) {
            this.setCreeperStateSpecial(compound.getInteger("SpecialState"));
        }
        if (compound.hasKey("SpecialCooldown")) {
            this.creeperSpecialCooldown = compound.getInteger("SpecialCooldown");
        }
        if (compound.hasKey("SpecialIgnitedTime")) {
            this.creeperSpecialIgnitedTime = compound.getInteger("SpecialIgnitedTime");
        }
        if (compound.hasKey("SpecialInterrupted")) {
            this.creeperSpecialInterrupted = compound.getInteger("SpecialInterrupted");
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (BABY.equals(key))
        {
            this.setScaleForAge(this.isChild());
        }

        super.notifyDataManagerChange(key);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.world.isRemote)
        {
            if (this.forcedAgeTimer > 0)
            {
                if (this.forcedAgeTimer % 4 == 0)
                {
                    this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D);
                }

                --this.forcedAgeTimer;
            }
        }
        else
        {
            int i = this.getGrowingAge();

            if (i < 0)
            {
                ++i;
                this.setGrowingAge(i);

                if (i == 0)
                {
                    this.onGrowingAdult();
                }
            }
            else if (i > 0)
            {
                --i;
                this.setGrowingAge(i);
            }
        }
        
		ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
		if(tameable != null && tameable.isTamed() && this.isChild())
		{
			CapabilityTameableEntity.EventHandler.resetEntityTargetAI(this);
		}
    }

    /**
     * This is called when Entity's growing age timer reaches 0 (negative values are considered as a child, positive as
     * an adult)
     */
    protected void onGrowingAdult()
    {
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.getGrowingAge() < 0;
    }

    public boolean isTamed()
    {
		ITameableEntity tameable = EntityUtil.getCapability(this, CapabilityTameableEntity.TAMEABLE_ENTITY_CAPABILITY, null);
		return (tameable != null && tameable.isTamed());
    }

    /**
     * "Sets the scale for an ageable entity according to the boolean parameter, which says if it's a child."
     */
    public void setScaleForAge(boolean child)
    {
        this.setScale(child ? 0.5F : 1.0F);
    }

    /**
     * Sets the width and height of the entity.
     */
    protected final void setSize(float width, float height)
    {
        boolean flag = this.ageWidth > 0.0F;
        this.ageWidth = width;
        this.ageHeight = height;

        if (!flag)
        {
            this.setScale(1.0F);
        }
    }

    protected final void setScale(float scale)
    {
        super.setSize(this.ageWidth * scale, this.ageHeight * scale);
    }

//Moved to base class for 
//modular special attack and persistent data
    public int getCreeperStateSpecial()
    {
        return ((Integer)this.dataManager.get(STATE_SPECIAL)).intValue();
    }

    public void setCreeperStateSpecial(int state)
    {
        this.getDataManager().set(STATE_SPECIAL, Integer.valueOf(state));     
    } 

//Same for regular fields
    public int getCreeperSpecialInterrupted()
    {
        return this.creeperSpecialInterrupted;
    }

    public int getCreeperSpecialInterruptedMax()
    {
        return this.creeperSpecialInterruptedMax;
    }

//Cooldowns won't get overriden by shorter ones
    public void setCreeperSpecialCooldown(int cooldown)
    {
        if(this.creeperSpecialCooldown <= cooldown)
        {
            this.creeperSpecialCooldown = cooldown;
        }
    }

    public int getCreeperSpecialCooldown()
    {
        return this.creeperSpecialCooldown;
    }

    public int getCreeperSpecialCooldownInterrupted()
    {
        return this.creeperSpecialCooldownInterrupted;
    }

    public int getCreeperSpecialCooldownAttacked()
    {
        return this.creeperSpecialCooldownAttacked;
    }

    public int getCreeperSpecialCooldownFrustrated()
    {
        return this.creeperSpecialCooldownFrustrated;
    }

    public int getCreeperSpecialCooldownStunned()
    {
        return this.creeperSpecialCooldownStunned;
    }

    public void setCreeperSpecialInterrupted(int interrupted)
    {
        this.creeperSpecialInterrupted = interrupted;
    }
}
