package net.daveyx0.primitivemobs.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

import org.sporotofpoorety.eternitymode.core.EternityModeSoundEvents;
import org.sporotofpoorety.eternitymode.entity.ai.EntityAIStun;

import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;




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


//Special handlers
//Including a data parameter
    @Unique
    private static final DataParameter<Integer> STATE_SPECIAL = EntityDataManager.<Integer>createKey(EntityCreeper.class, DataSerializers.VARINT);
    @Unique private int creeperSpecialCooldown;
    @Unique private int creeperSpecialIgnitedTime;
    @Unique private int creeperSpecialInterrupted;


//Special config values
    @Unique private boolean creeperSpecialEnabled;
    @Unique private int creeperSpecialCooldownInterrupted;
    @Unique private int creeperSpecialCooldownAttacked;
    @Unique private int creeperSpecialCooldownFrustrated;
    @Unique private int creeperSpecialCooldownOver;
    @Unique private int creeperSpecialCooldownStunned;
    @Unique private int creeperSpecialStunnedDuration;
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
//callbackinfo after original parameters
    private void constructorNewFields(World world, CallbackInfo callInfo)
    {
//Special handlers
        this.setCreeperStateSpecial(-1);
        this.creeperSpecialCooldown = 0;
        this.creeperSpecialIgnitedTime = 0;
        this.creeperSpecialInterrupted = 0;


//Special configs
        this.creeperSpecialEnabled = false;
        this.creeperSpecialCooldownInterrupted = 69420;
        this.creeperSpecialCooldownAttacked = 69420;
        this.creeperSpecialCooldownFrustrated = 69420;
        this.creeperSpecialCooldownOver = 69420;
        this.creeperSpecialCooldownStunned = 69420;
        this.creeperSpecialStunnedDuration = 69420;
        this.creeperSpecialIgnitedTimeMax = 69420;
        this.creeperSpecialInterruptedMax = 69420;
        this.creeperSpecialInterruptedDamage = 69420;


//Add stunned task
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;
        selfEntityLiving.tasks.addTask(0, new EntityAIStun(selfEntityLiving));        
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
//Do special particle
            this.creeperSpecialParticles(); 
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
//Special handlers
        compound.setInteger("CreeperSpecialState", this.getCreeperStateSpecial());
        compound.setInteger("CreeperSpecialCooldown", this.creeperSpecialCooldown);
        compound.setInteger("CreeperSpecialIgnitedTime", this.creeperSpecialIgnitedTime);
        compound.setInteger("CreeperSpecialInterrupted", this.creeperSpecialInterrupted);

//Special configs
        compound.setBoolean("CreeperSpecialEnabled", this.creeperSpecialEnabled);
        compound.setInteger("CreeperSpecialCooldownInterrupted", this.creeperSpecialCooldownInterrupted);
        compound.setInteger("CreeperSpecialCooldownAttacked", this.creeperSpecialCooldownAttacked);
        compound.setInteger("CreeperSpecialCooldownFrustrated", this.creeperSpecialCooldownFrustrated);
        compound.setInteger("CreeperSpecialCooldownOver", this.creeperSpecialCooldownOver);
        compound.setInteger("CreeperSpecialCooldownStunned", this.creeperSpecialCooldownStunned);
        compound.setInteger("CreeperSpecialStunnedDuration", this.creeperSpecialStunnedDuration);
        compound.setInteger("CreeperSpecialIgnitedTimeMax", this.creeperSpecialIgnitedTimeMax);
        compound.setInteger("CreeperSpecialInterruptedMax", this.creeperSpecialInterruptedMax);
        compound.setFloat("CreeperSpecialInterruptedDamage", this.creeperSpecialInterruptedDamage);
    }


    @Inject
    (
        method = "readEntityFromNBT",
        at = @At("TAIL")
    )
    private void readNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//Special handlers
        if (compound.hasKey("CreeperSpecialState")) { this.setCreeperStateSpecial(compound.getInteger("CreeperSpecialState")); }
        if (compound.hasKey("CreeperSpecialCooldown")) { this.creeperSpecialCooldown = compound.getInteger("CreeperSpecialCooldown"); }
        if (compound.hasKey("CreeperSpecialIgnitedTime")) { this.creeperSpecialIgnitedTime = compound.getInteger("CreeperSpecialIgnitedTime"); }
        if (compound.hasKey("CreeperSpecialInterrupted")) { this.creeperSpecialInterrupted = compound.getInteger("CreeperSpecialInterrupted"); }

//Special configs
        if (compound.hasKey("CreeperSpecialEnabled")) { this.creeperSpecialEnabled = compound.getBoolean("CreeperSpecialEnabled"); }
        if (compound.hasKey("CreeperSpecialCooldownInterrupted")) { this.creeperSpecialCooldownInterrupted = compound.getInteger("CreeperSpecialCooldownInterrupted"); }
        if (compound.hasKey("CreeperSpecialCooldownAttacked")) { this.creeperSpecialCooldownAttacked = compound.getInteger("CreeperSpecialCooldownAttacked"); }
        if (compound.hasKey("CreeperSpecialCooldownFrustrated")) { this.creeperSpecialCooldownFrustrated = compound.getInteger("CreeperSpecialCooldownFrustrated"); }
        if (compound.hasKey("CreeperSpecialCooldownOver")) { this.creeperSpecialCooldownOver = compound.getInteger("CreeperSpecialCooldownOver"); }
        if (compound.hasKey("CreeperSpecialCooldownStunned")) { this.creeperSpecialCooldownStunned = compound.getInteger("CreeperSpecialCooldownStunned"); }
        if (compound.hasKey("CreeperSpecialStunnedDuration")) { this.creeperSpecialStunnedDuration = compound.getInteger("CreeperSpecialStunnedDuration"); }
        if (compound.hasKey("CreeperSpecialIgnitedTimeMax")) { this.creeperSpecialIgnitedTimeMax = compound.getInteger("CreeperSpecialIgnitedTimeMax"); }
        if (compound.hasKey("CreeperSpecialInterruptedMax")) { this.creeperSpecialInterruptedMax = compound.getInteger("CreeperSpecialInterruptedMax"); }
        if (compound.hasKey("CreeperSpecialInterruptedDamage")) { this.creeperSpecialInterruptedDamage = compound.getFloat("CreeperSpecialInterruptedDamage"); }
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
        EternityModeSoundEvents.ENTITY_CREEPER_NUKE, SoundCategory.HOSTILE, 3.0F, 1.0F);
    }

    public void creeperSpecialParticles()
    {

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



//Special handlers
    public int getCreeperStateSpecial()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Integer)selfEntity.getDataManager().get(STATE_SPECIAL)).intValue();
    }

    public int getCreeperSpecialCooldown()
    {
        return this.creeperSpecialCooldown;
    }

    public int getCreeperSpecialIgnitedTime()
    {
        return this.creeperSpecialIgnitedTime;
    }

    public int getCreeperSpecialInterrupted()
    {
        return this.creeperSpecialInterrupted;
    }



//Special config
    public boolean getCreeperSpecialEnabled()
    {
        return this.creeperSpecialEnabled;
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

    public int getCreeperSpecialCooldownOver()
    {
        return this.creeperSpecialCooldownOver;
    }

    public int getCreeperSpecialCooldownStunned()
    {
        return this.creeperSpecialCooldownStunned;
    }

    public int getCreeperSpecialStunnedDuration()
    {
        return this.creeperSpecialStunnedDuration;
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



//Special handlers
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

    public void setCreeperSpecialIgnitedTime(int time)
    {
        this.creeperSpecialIgnitedTime = time;
    }

    public void setCreeperSpecialInterrupted(int interrupted)
    {
        this.creeperSpecialInterrupted = interrupted;
    }



//Special config
    public void setCreeperSpecialEnabled(boolean enabled)
    {
        this.creeperSpecialEnabled = enabled;
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

    public void setCreeperSpecialCooldownOver(int cooldownOver)
    {
        this.creeperSpecialCooldownOver = cooldownOver;
    }

    public void setCreeperSpecialCooldownStunned(int cooldownStunned)
    {
        this.creeperSpecialCooldownStunned = cooldownStunned;
    }

    public void setCreeperSpecialStunnedDuration(int stunnedDuration)
    {
        this.creeperSpecialStunnedDuration = stunnedDuration;
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
