package net.daveyx0.primitivemobs.entity.passive;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.daveyx0.multimob.entity.IMultiMobPassive;
import net.daveyx0.multimob.entity.ai.EntityAIGrabItemFromFloor;
import net.daveyx0.multimob.entity.ai.EntityAIStealFromPlayer;
import net.daveyx0.multimob.util.EntityUtil;
import net.daveyx0.primitivemobs.config.PrimitiveMobsConfigSpecial;
import net.daveyx0.primitivemobs.core.PrimitiveMobsItemIdsToItemStacks;
import net.daveyx0.primitivemobs.core.PrimitiveMobsRandomWeightedItem;
import net.daveyx0.primitivemobs.core.TaskUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityFilchLizard extends EntityCreature implements IMultiMobPassive {

	private int itemChance = 4;
	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;
	
    public ArrayList<String> lootStealItems = new ArrayList<>();
    public ArrayList<ItemStack> lootStealItemStacks = new ArrayList<>();

    public ArrayList<String> lootSpawnItems = new ArrayList<>();
    public ArrayList<Integer> lootSpawnItemWeights = new ArrayList<>();
    public ArrayList<ItemStack> lootSpawnItemStacks = new ArrayList<>();
	
	public EntityFilchLizard(World worldIn) {
		super(worldIn);
		this.inventoryHandsDropChances[0] = 0f;
		this.inventoryHandsDropChances[1] = 0f;
		this.setSize(0.6f, 0.5f);

//Item steal Ids converted from array to list
    	lootStealItems = new ArrayList<>(Arrays.asList(PrimitiveMobsConfigSpecial.getFilchStealLoot()));
//Turn the list into an ItemStack list to provide to the tasks
        lootStealItemStacks = PrimitiveMobsItemIdsToItemStacks.itemIdsToItemStacks(lootStealItems);    
//Use for tasks
        this.tasks.addTask(2, new EntityAIGrabItemFromFloor(this, 1.2D, Sets.newHashSet(lootStealItemStacks), true));
        this.tasks.addTask(3, new EntityAIStealFromPlayer(this, 0.8D, Sets.newHashSet(lootStealItemStacks), true));
	}

	  /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//Preserves field values including assigned by NBT

//Loop through field list and append its values to NBT list
        NBTTagList stealItems = new NBTTagList();
        for(String id : this.lootStealItems)
        {
            stealItems.appendTag(new NBTTagString(id));
        }
        compound.setTag("StealItems", stealItems);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//Avoids overwriting the fields with empty NBT tag values on initial spawn
        if (compound.hasKey("StealItems")) 
        {
//Clear field lists
            this.lootStealItems.clear();
            this.lootStealItemStacks.clear();
            
//8 is for strings
//Get NBT list of the item ids
            NBTTagList items = compound.getTagList("StealItems", 8);
//Iterate through it
            for(int i = 0; i < items.tagCount(); i++)
            {
//Get the string tag values and rebuild field item id list
                String itemId = items.getStringTagAt(i);
                this.lootStealItems.add(itemId);
            }
//Then rebuild itemstack field list accordingly
            this.lootStealItemStacks = PrimitiveMobsItemIdsToItemStacks.itemIdsToItemStacks(this.lootStealItems);    
        }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIGrabItemFromFloor.class))
        {
            this.tasks.addTask(2, new EntityAIGrabItemFromFloor(this, 1.2D, Sets.newHashSet(lootStealItemStacks), true));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIGrabItemFromFloor.class);

            this.tasks.addTask(2, new EntityAIGrabItemFromFloor(this, 1.2D, Sets.newHashSet(lootStealItemStacks), true));         
        }

//Add task if absent
        if(!TaskUtils.mobHasTask(this, EntityAIStealFromPlayer.class))
        {
            this.tasks.addTask(3, new EntityAIStealFromPlayer(this, 0.8D, Sets.newHashSet(lootStealItemStacks), true));
        }
//If task is here remove then reassign based on NBT (can be used to overwrite configs and make custom variants)
        else
        {
            TaskUtils.mobRemoveTaskIfPresent(this, EntityAIStealFromPlayer.class);

            this.tasks.addTask(3, new EntityAIStealFromPlayer(this, 0.8D, Sets.newHashSet(lootStealItemStacks), true));
        }
    }
	
    protected void initEntityAI()
    {
    	int prio = 0;

        this.tasks.addTask(prio++, new EntityAISwimming(this));
        this.tasks.addTask(prio++, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(prio++, new EntityFilchLizard.AIAvoidWhenNasty(this, EntityPlayer.class, 16.0F, 1.0D, 1.33D));
        this.tasks.addTask(prio++, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(prio++, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(prio++, new EntityAILookIdle(this));
    }
	
    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(!this.getHeldItemMainhand().isEmpty())
        {
        	this.setSize(0.6f, 0.75f);
        }
        else
        {
        	this.setSize(0.6f, 0.3f);
        }
        	
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25000000417232513D);
    }
   
    protected void updateAITasks()
    {
        super.updateAITasks();
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
    	int chance = PrimitiveMobsConfigSpecial.getFilchLizardLootChance();

//Only give it an item if chance passes
    	if(chance > 0 && (chance >= 100 || (rand.nextInt(100/chance) == 0)))
    	{
//Spawn item ids converted from array to arraylist then to itemstacks
        	lootSpawnItems = new ArrayList<>(Arrays.asList(PrimitiveMobsConfigSpecial.getFilchSpawnLoot()));
            lootSpawnItemStacks = PrimitiveMobsItemIdsToItemStacks.itemIdsToItemStacks(lootSpawnItems);
//Item weights from array to arraylist
//My first time using streams, still need to understand them better  
            lootSpawnItemWeights = new ArrayList<>(Arrays.stream(PrimitiveMobsConfigSpecial.getFilchSpawnLootWeights()).boxed().collect(Collectors.toList()));

//If hand empty, server sided
    		if(this.getHeldItemMainhand().isEmpty() && !getEntityWorld().isRemote)
    		{
//Get random weighted item based on its list
                ItemStack heldItem = PrimitiveMobsRandomWeightedItem.getRandomWeightedItem(this.lootSpawnItemStacks, this.lootSpawnItemWeights);
//Safety check
                if(heldItem == null)
                {
                    heldItem = ItemStack.EMPTY;                
                }
//Make it hold item in main hand
    			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, heldItem);
    		}
    	}

        return super.onInitialSpawn(difficulty, livingdata);
    }

    public EntityItem dropItemStack(ItemStack itemIn, float offsetY)
    {
        return this.entityDropItem(itemIn, offsetY);
    }
    
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
	    if (par1DamageSource.getTrueSource() != null)
	    {
	    	this.setLastAttackedEntity(par1DamageSource.getTrueSource());
	    }
	    
        ItemStack stack = this.getHeldItemMainhand();

        if (!stack.isEmpty() && !getEntityWorld().isRemote)
        {
            ItemStack newStack = stack.copy();
            this.dropItemStack(newStack, 1);
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

	    return super.attackEntityFrom(par1DamageSource, par2);
	}
    
    static class AIAvoidWhenNasty extends EntityAIAvoidEntity
    {
        public AIAvoidWhenNasty(EntityCreature theEntityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn,
				double nearSpeedIn) {
			super(theEntityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
		}

		/**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
        	return !entity.getHeldItemMainhand().isEmpty() && super.shouldExecute();
        }	
    }
    
    protected Block spawnableBlock = Blocks.SAND;
    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.getEntityBoundingBox().minY);
        int k = MathHelper.floor(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        return this.getEntityWorld().getBlockState(blockpos.down()).getBlock() == this.spawnableBlock && this.getEntityWorld().getLight(blockpos) > 8 && super.getCanSpawnHere();
    }
    
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
    	return super.isCreatureType(type, forSpawnCount);
    }
}
