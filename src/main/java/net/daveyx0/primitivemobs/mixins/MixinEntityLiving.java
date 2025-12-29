package net.daveyx0.primitivemobs.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;

import net.minecraft.nbt.NBTTagCompound;


//Mixin this class
@Mixin(value = EntityLiving.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityLiving
{

//May be unnecessary
    @Inject
    (
        method = "onUpdate", 
        at = @At("HEAD")
    )
    private void clearTargetsWhileStunned(CallbackInfo callInfo)
    {
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;

        if(selfEntityLiving instanceof EntityMob)
        {
            IMixinEntityMob selfEntityMobMixin = (IMixinEntityMob) (Object) this;

            if (selfEntityMobMixin.getAbsurdcraftStunned()) 
            {
                selfEntityLiving.setAttackTarget(null);
                selfEntityLiving.setRevengeTarget(null);
            }
        }
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
//Have to cast to access target class superclass fields and methods
        Entity selfEntityLiving = (EntityLiving) (Object) this;

        if(selfEntityLiving instanceof EntityMob)
        {
            Entity selfEntity = (Entity) (Object) this;
            IMixinEntityMob selfEntityMobMixin = (IMixinEntityMob) (Object) this;
//Register the new data parameter
            selfEntity.getDataManager().register(selfEntityMobMixin.getAbsurdcraftStunnedDataParameter(), Boolean.valueOf(false));         
        }
    }


    @Inject
    (
        method = "writeEntityToNBT",
        at = @At("TAIL")
    )
//EntityMob doesn't override writeEntityToNBT so i 
//i had to mixin here, however most of the logic is still kept in EntityMob
    private void writeNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;

        if(selfEntityLiving instanceof EntityMob)
        {
            IMixinEntityMob selfEntityMobMixin = (IMixinEntityMob) (Object) this;
//New NBT below
            compound.setBoolean("AbsurdcraftStunned", selfEntityMobMixin.getAbsurdcraftStunned());

            compound.setInteger("AbsurdcraftStunnedTimer", selfEntityMobMixin.getAbsurdcraftStunnedTimer());
        }
    }


    @Inject
    (
        method = "readEntityFromNBT",
        at = @At("TAIL")
    )
//EntityMob doesn't override readEntityFromNBT so i 
//i had to mixin here, however most of the logic is still kept in EntityMob
    private void readNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;

        if(selfEntityLiving instanceof EntityMob)
        {
            IMixinEntityMob selfEntityMobMixin = (IMixinEntityMob) (Object) this;
//New NBT below
            if (compound.hasKey("AbsurdcraftStunned")) {
                selfEntityMobMixin.setAbsurdcraftStunned(compound.getBoolean("AbsurdcraftStunned"));
            }

            if (compound.hasKey("AbsurdcraftStunnedTimer")) {
                selfEntityMobMixin.setAbsurdcraftStunnedTimer(compound.getInteger("AbsurdcraftStunnedTimer"));
            }
        }
    }


    @Inject
    (
        method = "setAttackTarget",
        at = @At("HEAD"),
        cancellable = true
    )
//EntityMob doesn't override setAttackTarget so i 
//i had to mixin here, however most of the logic is still kept in EntityMob
    private void setAttackTargetFailIfStunned(@Nullable EntityLivingBase entitylivingbaseIn, CallbackInfo callInfo)
    {
        EntityLiving selfEntityLiving = (EntityLiving) (Object) this;

        if(selfEntityLiving instanceof EntityMob)
        {
            IMixinEntityMob selfEntityMobMixin = (IMixinEntityMob) (Object) this;
//Can't target anything if stunned
            if(selfEntityMobMixin.getAbsurdcraftStunned())
            {
                callInfo.cancel();
            }
        }
    }

}

