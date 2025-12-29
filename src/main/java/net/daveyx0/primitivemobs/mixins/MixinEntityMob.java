package net.daveyx0.primitivemobs.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import net.daveyx0.primitivemobs.core.PrimitiveMobsSoundEvents;

import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveCreeper;
import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveTameableMob;

import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.minecraft.util.DamageSource;

import net.minecraft.world.World;


//Mixin this class
@Mixin(value = EntityMob.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityMob implements IMixinEntityMob
{

    @Unique
//Named this way for compatibility
    private static final DataParameter<Boolean> IS_ABSURDCRAFT_STUNNED = EntityDataManager.<Boolean>createKey(EntityMob.class, DataSerializers.BOOLEAN);


//Named this way for compatibility
    @Unique private int absurdcraftStunnedTimer;




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
        this.setAbsurdcraftStunned(false);       
    }


    @Inject
    (
//Inject in this method
        method = "onUpdate",
//At tail (low priority after all)
        at = @At("TAIL")
    )
    private void onUpdateStunTimer(CallbackInfo callInfo)
    {
        if(this.absurdcraftStunnedTimer > 0)
        {
            if(--absurdcraftStunnedTimer <= 0)
            {
                this.setAbsurdcraftStunned(false);
            }
        }
    }


//Operation
    @WrapWithCondition
    (
//On method onUpdate
        method = "onUpdate", 
//At...
        at = 
        @At
        (
//Invocation
            value = "INVOKE",
//Target is any setDead() 
            target = "Lnet/minecraft/entity/monster/EntityMob;setDead()V"
        )
    )
    private boolean preventPeacefulDespawnIfTamed(EntityMob self) 
    {
/*
        if(self instanceof EntityPrimitiveCreeper)
        {
//Must cast to access subclass fields and methods
            return !((EntityPrimitiveCreeper) self).isTamed();
        }
        if(self instanceof EntityPrimitiveTameableMob)
        {
            return !((EntityPrimitiveTameableMob) self).isTamed();
        }
*/
        if(this.isTamed())
        {
            return false;
        }

        return true;
    }


    @Inject
    (
        method = "attackEntityFrom",
        at = @At("HEAD")
    )
    private void attackEntitySpecialInterrupt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callInfo)
    {
        EntityMob selfEntityMob = (EntityMob) (Object) this;
//If a creeper
        if(selfEntityMob instanceof EntityCreeper)
        {
            IMixinEntityCreeper selfCreeperMixin = (IMixinEntityCreeper) (Object) this;
//If it takes arrow or melee damage immediately cancel special attack and apply cooldown
            if((amount >= (float) selfCreeperMixin.getCreeperSpecialInterruptedDamage()) 
            && (source.damageType.equals("arrow") || source.damageType.equals("mob") || source.damageType.equals("player")))
            {
//If attacked in the middle of the swell
                if(selfCreeperMixin.getCreeperStateSpecial() > 0)
                {
                    Entity selfEntity = (Entity) (Object) this;
                    EntityCreeper selfEntityCreeper = (EntityCreeper) (Object) this;
//Play sound
                    selfEntity.playSound(PrimitiveMobsSoundEvents.ENTITY_CREEPER_DIZZY, 3.0F, 1.0F);
//Interrupt normal swell (bugfix)
                    selfCreeperMixin.resetCreeper();
//Apply special cooldown
                    selfCreeperMixin.setCreeperSpecialCooldown(selfCreeperMixin.getCreeperSpecialCooldownStunned());
//And apply stun
                    this.setAbsurdcraftStunned(true);
                    this.absurdcraftStunnedTimer = selfCreeperMixin.getCreeperSpecialStunnedDuration();
                }
//If just attacked normally there's a smaller cooldown 
                else
                {
                    selfCreeperMixin.setCreeperSpecialCooldown(selfCreeperMixin.getCreeperSpecialCooldownAttacked());     
                }

                selfCreeperMixin.resetCreeperSpecial();        
            }
        }
    }


//New misc methods


//Excellent use of dynamic method dispatch
    public boolean isTamed()
    {
        return false;
    }


//New getters


//Named this way for compatibility
    public DataParameter<Boolean> getAbsurdcraftStunnedDataParameter()
    {
        return this.IS_ABSURDCRAFT_STUNNED; 
    }

    public boolean getAbsurdcraftStunned()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Boolean)selfEntity.getDataManager().get(IS_ABSURDCRAFT_STUNNED)).booleanValue();
    }

    public int getAbsurdcraftStunnedTimer()
    {
        return this.absurdcraftStunnedTimer;
    }


//New setters


    public void setAbsurdcraftStunned(boolean isStunned)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(isStunned)); 
    }

    public void setAbsurdcraftStunnedTimer(int time)
    {
        this.absurdcraftStunnedTimer = time;
    }

}

