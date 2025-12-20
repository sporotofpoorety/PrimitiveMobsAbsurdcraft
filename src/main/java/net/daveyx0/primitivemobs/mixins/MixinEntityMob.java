package net.daveyx0.primitivemobs.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.daveyx0.primitivemobs.entity.monster.EntityBabySpider;
import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveCreeper;
import net.daveyx0.primitivemobs.entity.monster.EntityPrimitiveTameableMob;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.EnumDifficulty;


//Mixin this class
@Mixin(value = EntityMob.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityMob
{
/*
//Inject extra code
    @Inject(
//On this specific method
        method = "onUpdate",
        remap = true,
//At
        at = 
        @At(
//Method invoke
            value = "INVOKE",
//Of set dead
            target = "Lnet/minecraft/entity/monster/EntityMob;setDead()V",
            remap = true,
//Shift to before it
            shift = At.Shift.BEFORE
        ),
//Remap (EXPLICIT)
        cancellable = true,
        require = 1
    )
    private void preventPeacefulDespawnIfTamed(CallbackInfo callinfo) {
        EntityMob self = (EntityMob)(Object)this;

        if (self.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            if (self instanceof EntityBabySpider
                && ((EntityBabySpider) self).isTamed()) {

                callinfo.cancel();
            }

            if (self instanceof EntityPrimitiveTameableMob
                && ((EntityPrimitiveTameableMob) self).isTamed()) {

                callinfo.cancel();
            }
        }
    }
*/

    @WrapWithCondition
    (
        method = "onUpdate", 
        at = 
        @At
        (
            value = "INVOKE", 
            target = "Lnet/minecraft/entity/monster/EntityMob;setDead()V"
        )
    )
    private boolean preventPeacefulDespawnIfTamed(EntityMob instance) 
    {
/*
        return !(instance instanceof EntityBabySpider) 
        || !((EntityBabySpider) instance).isTamed();
*/
        return false;
    }
}

