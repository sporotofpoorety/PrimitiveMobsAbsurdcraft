package net.daveyx0.primitivemobs.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.daveyx0.multimob.entity.ai.EntityAIBackOffFromEntity;

//import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityAIBackOffFromEntity;

import net.minecraft.entity.EntityCreature;




//Mixin this class
@Mixin(value = EntityAIBackOffFromEntity.class, remap = false)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityAIBackOffFromEntity
{

    @Unique private int backOffSpeedMultiplier;



//Modify variable
    @ModifyVariable(
//In updateTask
        method = "updateTask",
//Stored
        at = @At("STORE"),
//First one
        ordinal = 0
    )
//That is a double and is called d
    private double multimobBackOffSpeedScale(double d) {
        return d * this.backOffSpeedMultiplier;
    }


    @Inject
    (
//Internal representation of constructor
        method = "<init>",
//At return
        at = @At("TAIL")
    )
//Injects take 
//callbackinfo after original parameters
    private void constructorNewFields(EntityCreature entitycreature, double maxDistance, boolean defensiveAttack, CallbackInfo callInfo)
    {
//Set mutex bits
        EntityAIBackOffFromEntity selfEntityAIBackOff = (EntityAIBackOffFromEntity) (Object) this;
        selfEntityAIBackOff.setMutexBits(1);
    }

}
