package net.daveyx0.primitivemobs.mixins;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.core.EternityModeSoundEvents;
import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;

import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveCreeper;
import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveTameableMob;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityCreeper;
import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;




//Mixin this class
@Mixin(value = EntityMob.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityMob implements IMixinEntityMob
{

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


/*
    @WrapWithCondition
    (
        method = "despawnEntity",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityMob;ut$dropEquipmentAndDespawn()V",
            remap = false
        ),
//Slice
        slice = @Slice(
//From this reference point
            from = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=16384.0D"
            ),
//To another, later reference point
            to = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=1024.0D"
            )
        )
    )*/
/*
    @WrapWithCondition
    (
        method = "despawnEntity",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityMob;ut$dropEquipmentAndDespawn()V",
            remap = false,
            ordinal = 0
        ),
        require = 0
    )
    private boolean flyingHelperMob(EntityMob self) 
    {
        return false;
    }
*/


    @Inject
    (
        method = "attackEntityFrom",
        at = @At("TAIL")
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
                    selfEntity.playSound(EternityModeSoundEvents.ENTITY_DIZZY, 3.0F, 1.0F);
//Apply special cooldown
                    selfCreeperMixin.setCreeperSpecialCooldown(selfCreeperMixin.getCreeperSpecialCooldownStunned());
//And apply stun
                    IMixinEntityLiving selfEntityLivingMixin = (IMixinEntityLiving) (Object) this;
                    selfEntityLivingMixin.setAbsurdcraftStunned(true);
                    selfEntityLivingMixin.setAbsurdcraftStunnedTimer(selfCreeperMixin.getCreeperSpecialStunnedDuration());
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

}

