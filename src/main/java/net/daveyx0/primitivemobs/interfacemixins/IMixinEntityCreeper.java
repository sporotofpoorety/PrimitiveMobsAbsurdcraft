package net.daveyx0.primitivemobs.interfacemixins;

import net.daveyx0.primitivemobs.entity.ai.EntityAIStun;

public interface IMixinEntityCreeper
{

    boolean creeperSpecialConditions();
    void resetCreeper();
    void resetCreeperSpecial();
    void creeperSpecialAttemptSound(double atX, double atY, double atZ);

//Getters

    int getCreeperIgnitedTime();
    int getCreeperExplosionRadius();

    int getCreeperStateSpecial();

    int getCreeperSpecialCooldown();
    int getCreeperSpecialCooldownInterrupted();
    int getCreeperSpecialCooldownAttacked();
    int getCreeperSpecialCooldownFrustrated();
    int getCreeperSpecialCooldownStunned();
    int getCreeperSpecialStunnedDuration();
    int getCreeperSpecialIgnitedTime();
    int getCreeperSpecialInterrupted();

    int getCreeperSpecialIgnitedTimeMax();
    int getCreeperSpecialInterruptedMax();
    float getCreeperSpecialInterruptedDamage();


//Setters
    
    void setCreeperIgnitedTime(int time);
    void setCreeperExplosionRadius(int radius);

    void setCreeperStateSpecial(int state);

    void setCreeperSpecialCooldown(int cooldown);
    void setCreeperSpecialCooldownInterrupted(int cooldownInterrupted);
    void setCreeperSpecialCooldownAttacked(int cooldownAttacked);
    void setCreeperSpecialCooldownFrustrated(int cooldownFrustrated);
    void setCreeperSpecialCooldownStunned(int cooldownStunned);
    void setCreeperSpecialStunnedDuration(int stunnedDuration);
    void setCreeperSpecialIgnitedTime(int time);
    void setCreeperSpecialInterrupted(int interrupted);

    void setCreeperSpecialIgnitedTimeMax(int ignitedTimeMax);
    void setCreeperSpecialInterruptedMax(int interruptedMax);
    void setCreeperSpecialInterruptedDamage(float interruptedDamage);

}
