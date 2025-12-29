package net.daveyx0.primitivemobs.entity.monster;

import javax.annotation.Nullable;

import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.daveyx0.primitivemobs.entity.ai.EntityAISwitchBetweenRangedAndMelee;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntitySkeletonWarrior extends EntitySkeleton {

    protected String skeletonWarriorEffectName;
    protected Potion skeletonWarriorEffect;
    protected int skeletonWarriorEffectDuration;
    protected int skeletonWarriorEffectAmplifier;
    protected double skeletonWarriorStrafeThreshold;
    protected double skeletonWarriorSwitchToMelee;
    protected double skeletonWarriorSwitchToRanged;
    protected int skeletonWarriorShootDelayTime;
    protected int skeletonWarriorShootDrawTime;
    protected double skeletonWarriorShootDamage;
    protected double skeletonWarriorShootVelocity;
    protected double skeletonWarriorShootInaccuracyFactor;
    
	public EntitySkeletonWarrior(World worldIn) {
		super(worldIn);

        this.skeletonWarriorEffectName = PrimitiveMobsConfigSpecial.getSkeletonWarriorEffect();
        this.skeletonWarriorEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(skeletonWarriorEffectName));
        this.skeletonWarriorEffectDuration = PrimitiveMobsConfigSpecial.getSkeletonWarriorEffectDuration();
        this.skeletonWarriorEffectAmplifier = PrimitiveMobsConfigSpecial.getSkeletonWarriorEffectAmplifier();
        this.skeletonWarriorStrafeThreshold = PrimitiveMobsConfigSpecial.getSkeletonWarriorStrafeThreshold(); 
        this.skeletonWarriorSwitchToMelee = PrimitiveMobsConfigSpecial.getSkeletonWarriorSwitchToMelee();
        this.skeletonWarriorSwitchToRanged = PrimitiveMobsConfigSpecial.getSkeletonWarriorSwitchToRanged();
        this.skeletonWarriorShootDelayTime = PrimitiveMobsConfigSpecial.getSkeletonWarriorShootDelayTime();
        this.skeletonWarriorShootDrawTime = PrimitiveMobsConfigSpecial.getSkeletonWarriorShootDrawTime();
        this.skeletonWarriorShootDamage = PrimitiveMobsConfigSpecial.getSkeletonWarriorShootDamage();
        this.skeletonWarriorShootVelocity = PrimitiveMobsConfigSpecial.getSkeletonWarriorShootVelocity();
        this.skeletonWarriorShootInaccuracyFactor = PrimitiveMobsConfigSpecial.getSkeletonWarriorShootInaccuracyFactor();

        this.tasks.addTask(4, new EntityAISwitchBetweenRangedAndMelee(this, 1.35D, PrimitiveMobsConfigSpecial.getSkeletonWarriorShootDelayTime(), (float) PrimitiveMobsConfigSpecial.getSkeletonWarriorStrafeThreshold(), PrimitiveMobsConfigSpecial.getSkeletonWarriorShootDrawTime()));
        this.tasks.addTask(5, new EntitySkeletonWarrior.EntityAISwitchWeapons(this, PrimitiveMobsConfigSpecial.getSkeletonWarriorSwitchToMelee(), PrimitiveMobsConfigSpecial.getSkeletonWarriorSwitchToRanged(), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.BOW)));
	}
	
    protected void initEntityAI()
    {
        this.tasks.taskEntries.clear();
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        //this.tasks.addTask(6, new EntityAIBackOffFromEnemy(this, 5D, false));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }
    
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
    }
    
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));

        if (this.rand.nextFloat() < 0.05F)
        {
            this.setLeftHanded(true);
        }
        else
        {
            this.setLeftHanded(false);
        }

        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());

        if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
        {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }
        
        if (this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty())
        {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
        
        return livingdata;
    }

	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Preserves field values including assigned by NBT
        compound.setString("EffectName", this.skeletonWarriorEffectName);
        compound.setInteger("EffectDuration", this.skeletonWarriorEffectDuration);
        compound.setInteger("EffectAmplifier", this.skeletonWarriorEffectAmplifier);
        compound.setDouble("StrafeThreshold", this.skeletonWarriorStrafeThreshold);
        compound.setDouble("SwitchToMelee", this.skeletonWarriorSwitchToMelee);
        compound.setDouble("SwitchToRanged", this.skeletonWarriorSwitchToRanged);
        compound.setInteger("ShootDelayTime", this.skeletonWarriorShootDelayTime);
        compound.setInteger("ShootDrawTime", this.skeletonWarriorShootDrawTime);
        compound.setDouble("ShootDamage", this.skeletonWarriorShootDamage);
        compound.setDouble("ShootVelocity", this.skeletonWarriorShootVelocity);
        compound.setDouble("ShootInaccuracyFactor", this.skeletonWarriorShootInaccuracyFactor);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("EffectName")) 
        {
            this.skeletonWarriorEffectName = compound.getString("EffectName");
            this.skeletonWarriorEffect =
                ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.skeletonWarriorEffectName));
        }
        if (compound.hasKey("EffectDuration")) { this.skeletonWarriorEffectDuration = compound.getInteger("EffectDuration"); }
        if (compound.hasKey("EffectAmplifier")) { this.skeletonWarriorEffectAmplifier = compound.getInteger("EffectAmplifier"); }
        if (compound.hasKey("StrafeThreshold")) { this.skeletonWarriorStrafeThreshold = compound.getDouble("StrafeThreshold"); }
        if (compound.hasKey("SwitchToMelee")) { this.skeletonWarriorSwitchToMelee = compound.getDouble("SwitchToMelee"); }
        if (compound.hasKey("SwitchToRanged")) { this.skeletonWarriorSwitchToRanged = compound.getDouble("SwitchToRanged"); }
        if (compound.hasKey("ShootDelayTime")) { this.skeletonWarriorShootDelayTime = compound.getInteger("ShootDelayTime"); }
        if (compound.hasKey("ShootDrawTime")) { this.skeletonWarriorShootDrawTime = compound.getInteger("ShootDrawTime"); }
        if (compound.hasKey("ShootDamage")) { this.skeletonWarriorShootDamage = compound.getDouble("ShootDamage"); }
        if (compound.hasKey("ShootVelocity")) { this.skeletonWarriorShootVelocity = compound.getDouble("ShootVelocity"); }
        if (compound.hasKey("ShootInaccuracyFactor")) { this.skeletonWarriorShootInaccuracyFactor = compound.getDouble("ShootInaccuracyFactor"); }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAISwitchBetweenRangedAndMelee.class))
        {
            this.tasks.addTask(4, new EntityAISwitchBetweenRangedAndMelee(this, 1.35D, this.skeletonWarriorShootDelayTime, (float) this.skeletonWarriorStrafeThreshold, this.skeletonWarriorShootDrawTime));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAISwitchBetweenRangedAndMelee.class);

            this.tasks.addTask(4, new EntityAISwitchBetweenRangedAndMelee(this, 1.35D, this.skeletonWarriorShootDelayTime, (float) this.skeletonWarriorStrafeThreshold, this.skeletonWarriorShootDrawTime));            
        }

//Add task if absent        
        if(!TaskUtils.mobHasTask(this, EntityAISwitchWeapons.class))
        {
            this.tasks.addTask(5, new EntitySkeletonWarrior.EntityAISwitchWeapons(this, this.skeletonWarriorSwitchToMelee, this.skeletonWarriorSwitchToRanged, new ItemStack(Items.IRON_SWORD), new ItemStack(Items.BOW)));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAISwitchWeapons.class);

            this.tasks.addTask(5, new EntitySkeletonWarrior.EntityAISwitchWeapons(this, this.skeletonWarriorSwitchToMelee, this.skeletonWarriorSwitchToRanged, new ItemStack(Items.IRON_SWORD), new ItemStack(Items.BOW)));
        }
    }
 
    public ItemStack getBackItem()
    {
    	if(this.getHeldItemMainhand().getItem() == Items.IRON_SWORD)
    	{
    		return new ItemStack(Items.BOW);
    	}
    	else
    	{
    		return new ItemStack(Items.IRON_SWORD);
    	}
    }
    
    protected EntityArrow getArrow(float p_190726_1_)
    {
        EntityArrow entityarrow = super.getArrow(p_190726_1_);

        if (!this.skeletonWarriorEffectName.isEmpty())
        {
            if(entityarrow instanceof EntityTippedArrow)
            {
                ( (EntityTippedArrow) entityarrow ).addEffect(new PotionEffect(skeletonWarriorEffect, skeletonWarriorEffectDuration, skeletonWarriorEffectAmplifier));
            }
        }

        return entityarrow;
    }
    
    public class EntityAISwitchWeapons extends EntityAIBase
    {
    	EntitySkeleton mob;
    	EntityLivingBase target;
    	double minDistance;
    	double maxDistance;
    	ItemStack weaponOne;
    	ItemStack weaponTwo;
    	
    	public EntityAISwitchWeapons(EntitySkeleton entitymob, double minDistance, double maxDistance, ItemStack weaponOne, ItemStack weaponTwo) {
    		mob = entitymob;
    		this.minDistance = minDistance;
    		this.maxDistance = maxDistance;
    		this.weaponOne = weaponOne;
    		this.weaponTwo = weaponTwo;
		}

		/**
		* Returns whether the EntityAIBase should begin execution.
		*/
		public boolean shouldExecute()
		{
	        this.target = this.mob.getAttackTarget();

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
	        	if(((this.mob.getDistance(this.target) < minDistance && this.mob.getHeldItemMainhand() != weaponOne) || 
	        			(this.mob.getDistance(this.target) > maxDistance && this.mob.getHeldItemMainhand() != weaponTwo)) && this.mob.canEntityBeSeen(this.target))
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
	    }
	    
	    /**
	     * Updates the task
	     */
	    public void updateTask()
	    {
	    	if(this.mob.getDistance(this.target) < minDistance)
	    	{
	    		this.mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, weaponOne);
	    	}
	    	else if(this.mob.getDistance(this.target) > maxDistance)
	    	{
	    		this.mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, weaponTwo);
	    	}
	    }
    }
    
    /**
     * sets this entity's combat AI.
     */
    @Override
    public void setCombatTask()
    {
        
    }
    
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {

        EntityArrow entityarrow = this.getArrow(distanceFactor);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
//Trying to adjust arrow shoot height to speed
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D * (skeletonWarriorShootInaccuracyFactor / 1.6F), d2, 
        (float) this.skeletonWarriorShootVelocity, 
        (float) this.skeletonWarriorShootInaccuracyFactor * (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);


/*

//This is here to test using vectors for projectiles

//Distances
        double d0 = target.posX - this.posX;
//Manually plug in default arrow spawn height since arrow generation saved for later down
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - (this.posY + this.getEyeHeight() - 0.1);
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

//Vector 
        Vec3d direction = new Vec3d(d0, d1, d2).normalize();

//Axes off that vector
        Vec3d[] basis = makeOrthonormalBasis(direction);
        Vec3d forward = basis[0]; 
        Vec3d up = basis[1]; 
        Vec3d right = basis[2]; 

*/

/*

//This is square grid pattern

//Across a specified distance range
        for(int mainAt = -1; mainAt <= 1; mainAt++)
        {
//Offsets on vectors relative axes
            Vec3d offsetUp = up.scale(mainAt * 2.0);
//Repeat for cross axis
            for(int crossAt = -1; crossAt <= 1; crossAt++)
            {
                Vec3d offsetRight = right.scale(crossAt * 2.0);

//New arrows
                    EntityArrow newArrow = this.getArrow(distanceFactor);

//Then move new arrows according to offsets
                    newArrow.setPosition(this.posX + offsetUp.x + offsetRight.x,
                    this.posY + this.getEyeHeight() - 0.1 + offsetUp.y + offsetRight.y,
                    this.posZ + offsetUp.z + offsetRight.z);


                    newArrow.shoot(d0, d1 + d3 * 0.20000000298023224D * (skeletonWarriorShootInaccuracyFactor / 1.6F), d2, 
                    (float) this.skeletonWarriorShootVelocity, 
                    (float) this.skeletonWarriorShootInaccuracyFactor * (float) (14 - this.world.getDifficulty().getDifficultyId() * 4));

                    newArrow.setDamage(this.skeletonWarriorShootDamage - 3.0D);
                    world.spawnEntity(newArrow);
                
            }
        }
*/


/*

//This is concentric ring pattern

        for(int layer = 1; layer <= 2; layer++)
        {
            for(int angleAt = 0; angleAt < 32; angleAt++) 
            {
                Vec3d offsetUp = up.scale(4.0 * layer * Math.sin(Math.toRadians(11.25 * angleAt)));
                Vec3d offsetRight = right.scale(4.0 * layer * Math.cos(Math.toRadians(11.25 * angleAt)));

//New arrows
                EntityArrow newArrow = this.getArrow(distanceFactor);

//Then move new arrows according to offsets
                newArrow.setPosition(this.posX + offsetUp.x + offsetRight.x,
                this.posY + this.getEyeHeight() - 0.1 + offsetUp.y + offsetRight.y,
                this.posZ + offsetUp.z + offsetRight.z);


                newArrow.shoot(d0, d1 + d3 * 0.20000000298023224D * (skeletonWarriorShootInaccuracyFactor / 1.6F), d2, 
                (float) this.skeletonWarriorShootVelocity, 
                (float) this.skeletonWarriorShootInaccuracyFactor * (float) (14 - this.world.getDifficulty().getDifficultyId() * 4));

                newArrow.setDamage(this.skeletonWarriorShootDamage - 3.0D);
                world.spawnEntity(newArrow);
            }  
        }
*/
    }
}
