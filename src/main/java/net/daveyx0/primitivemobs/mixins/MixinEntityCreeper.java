package net.daveyx0.primitivemobs.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;
import net.daveyx0.primitivemobs.entity.ai.EntityAIStun;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;

import net.minecraft.world.World;


//Mixin this class
@Mixin(value = EntityCreeper.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityCreeper implements IMixinEntityCreeper
{

//Have to shadow to access target class direct fields and methods
    @Shadow 
    private int timeSinceIgnited;

    @Shadow
    private int explosionRadius;


//New mutable state fields

//And a data parameter too
    @Unique
    private static final DataParameter<Integer> STATE_SPECIAL = EntityDataManager.<Integer>createKey(EntityCreeper.class, DataSerializers.VARINT);

    @Unique private int creeperSpecialCooldown;
    @Unique private int creeperSpecialCooldownInterrupted;
    @Unique private int creeperSpecialCooldownAttacked;
    @Unique private int creeperSpecialCooldownFrustrated;
    @Unique private int creeperSpecialCooldownStunned;
    @Unique private int creeperSpecialStunnedDuration;
    @Unique private int creeperSpecialIgnitedTime;
    @Unique private int creeperSpecialInterrupted;
//New configurable parameter fields
    @Unique private int creeperSpecialIgnitedTimeMax;
    @Unique private int creeperSpecialInterruptedMax;
    @Unique private float creeperSpecialInterruptedDamage;


    @Inject
    (
//Internal representation of constructor
        method = "<init>",
//At return
        at = @At("RETURN")
    )
//Injects take 
//callbackinfo and original parameters
    private void constructorNewFields(World world, CallbackInfo callInfo)
    {
//Set the special data parameter
        this.setCreeperStateSpecial(-1);

//Add stunned task
        EntityCreeper selfEntityCreeper = (EntityCreeper) (Object) this;
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;
        selfEntityLiving.tasks.addTask(0, new EntityAIStun(selfEntityCreeper));

//These, by themselves,
//won't do anything, but gotta have 
//values to prevent null pointer exceptions
        this.creeperSpecialCooldown = 0;
        this.creeperSpecialCooldownInterrupted = 69420;
        this.creeperSpecialCooldownAttacked = 69420;
        this.creeperSpecialCooldownFrustrated = 69420;
        this.creeperSpecialCooldownStunned = 69420;
        this.creeperSpecialStunnedDuration = 69420;
        this.creeperSpecialIgnitedTime = 0;
        this.creeperSpecialInterrupted = 0;        
    }


    @Inject
    (
//Inject in this method
        method = "entityInit",
//At tail
        at = @At("TAIL")
    )
//On entity init
    private void entityInitNewDataParameter(CallbackInfo callInfo)
    {
//Have to cast to access mixin superclass fields and methods
        Entity selfEntity = (Entity) (Object) this;
//Register the new data parameter
        selfEntity.getDataManager().register(STATE_SPECIAL, Integer.valueOf(-1));
    }


    @Inject
    (
//Inject in this method
        method = "onUpdate",
//At head
        at = @At("HEAD")
    )
//Task controls intent and mutex
//onUpdate controls state progress
    private void onUpdateSpecialSwellTick(CallbackInfo callInfo)
    {
//Decrement special cooldown
        if(this.creeperSpecialCooldown > 0)
        {
            this.creeperSpecialCooldown--;
        }

        int stateSpecial = this.getCreeperStateSpecial();
//If special state positive
        if(stateSpecial > 0)
        {
//Increase special ignited time
            creeperSpecialIgnitedTime++;
        }
//Else immediately shut down special swell
        else
        {
            creeperSpecialIgnitedTime = 0;
        }
    }

    @Inject
    (
        method = "writeEntityToNBT",
        at = @At("TAIL")
    )
    private void writeNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
        compound.setInteger("SpecialState", this.getCreeperStateSpecial());

        compound.setInteger("SpecialCooldown", this.creeperSpecialCooldown);
        compound.setInteger("SpecialIgnitedTime", this.creeperSpecialIgnitedTime);
        compound.setInteger("SpecialInterrupted", this.creeperSpecialInterrupted);
    }


    @Inject
    (
        method = "readEntityFromNBT",
        at = @At("TAIL")
    )
    private void readNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
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


//New misc methods


    public boolean creeperSpecialConditions()
    {
        return true;
    }

    public void resetCreeper()
    {
        EntityCreeper selfEntityCreeper = (EntityCreeper) (Object) this;

        this.setCreeperIgnitedTime(0);
        selfEntityCreeper.setCreeperState(-1);       
    }

    public void resetCreeperSpecial()
    {
        this.creeperSpecialIgnitedTime = 0;
        this.setCreeperStateSpecial(-1);
    }

    public void creeperSpecialAttemptSound(double atX, double atY, double atZ)
    {
        Entity selfEntity = (Entity) (Object) this;

        selfEntity.world.playSound(null, atX, atY, atZ,
        PrimitiveMobsSoundEvents.ENTITY_CREEPER_NUKE, SoundCategory.NEUTRAL, 3.0F, 1.0F);
    }


//New getters


    public int getCreeperIgnitedTime()
    {
        return this.timeSinceIgnited;
    }

    public int getCreeperExplosionRadius()
    {
        return this.explosionRadius;
    }


//Enables client-accessible persistent data for special attack
    public int getCreeperStateSpecial()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Integer)selfEntity.getDataManager().get(STATE_SPECIAL)).intValue();
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

    public int getCreeperSpecialStunnedDuration()
    {
        return this.creeperSpecialStunnedDuration;
    }

    public int getCreeperSpecialIgnitedTime()
    {
        return this.creeperSpecialIgnitedTime;
    }


    public int getCreeperSpecialInterrupted()
    {
        return this.creeperSpecialInterrupted;
    }


    public int getCreeperSpecialIgnitedTimeMax()
    {
        return this.creeperSpecialIgnitedTimeMax;
    }

    public int getCreeperSpecialInterruptedMax()
    {
        return this.creeperSpecialInterruptedMax;
    }

    public float getCreeperSpecialInterruptedDamage()
    {
        return this.creeperSpecialInterruptedDamage;
    }


//New setters


    public void setCreeperIgnitedTime(int time)
    {
        this.timeSinceIgnited = time;
    }

    public void setCreeperExplosionRadius(int radius)
    {
        this.explosionRadius = radius;
    }


    public void setCreeperStateSpecial(int state)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(STATE_SPECIAL, Integer.valueOf(state));     
    } 


//Short cooldown applications won't override long ones
    public void setCreeperSpecialCooldown(int cooldown)
    {
        if(this.creeperSpecialCooldown <= cooldown)
        {
            this.creeperSpecialCooldown = cooldown;
        }
    }

    public void setCreeperSpecialCooldownInterrupted(int cooldownInterrupted)
    {
        this.creeperSpecialCooldownInterrupted = cooldownInterrupted;
    }

    public void setCreeperSpecialCooldownAttacked(int cooldownAttacked)
    {
        this.creeperSpecialCooldownAttacked = cooldownAttacked;
    }

    public void setCreeperSpecialCooldownFrustrated(int cooldownFrustrated)
    {
        this.creeperSpecialCooldownFrustrated = cooldownFrustrated;
    }

    public void setCreeperSpecialCooldownStunned(int cooldownStunned)
    {
        this.creeperSpecialCooldownStunned = cooldownStunned;
    }

    public void setCreeperSpecialStunnedDuration(int stunnedDuration)
    {
        this.creeperSpecialStunnedDuration = stunnedDuration;
    }

    public void setCreeperSpecialIgnitedTime(int time)
    {
        this.creeperSpecialIgnitedTime = time;
    }

    public void setCreeperSpecialInterrupted(int interrupted)
    {
        this.creeperSpecialInterrupted = interrupted;
    }


    public void setCreeperSpecialIgnitedTimeMax(int ignitedTimeMax)
    {
        this.creeperSpecialIgnitedTimeMax = ignitedTimeMax;
    }

    public void setCreeperSpecialInterruptedMax(int interruptedMax)
    {
        this.creeperSpecialInterruptedMax = interruptedMax;
    }

    public void setCreeperSpecialInterruptedDamage(float interruptedDamage)
    {
        this.creeperSpecialInterruptedDamage = interruptedDamage;
    }

}
