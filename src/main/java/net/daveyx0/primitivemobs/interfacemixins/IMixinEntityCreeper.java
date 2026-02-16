package net.daveyx0.primitivemobs.interfacemixins;

import org.sporotofpoorety.eternitymode.entity.ai.EntityAIStun;

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
    int getCreeperSpecialIgnitedTime();
    int getCreeperSpecialInterrupted();

    boolean getCreeperSpecialEnabled();
    int getCreeperSpecialCooldownInterrupted();
    int getCreeperSpecialCooldownAttacked();
    int getCreeperSpecialCooldownFrustrated();
    int getCreeperSpecialCooldownOver();
    int getCreeperSpecialCooldownStunned();
    int getCreeperSpecialStunnedDuration();
    int getCreeperSpecialIgnitedTimeMax();
    int getCreeperSpecialInterruptedMax();
    float getCreeperSpecialInterruptedDamage();


//Setters
    
    void setCreeperIgnitedTime(int time);
    void setCreeperExplosionRadius(int radius);

    void setCreeperStateSpecial(int state);
    void setCreeperSpecialCooldown(int cooldown);
    void setCreeperSpecialIgnitedTime(int time);
    void setCreeperSpecialInterrupted(int interrupted);

    void setCreeperSpecialEnabled(boolean enabled);
    void setCreeperSpecialCooldownInterrupted(int cooldownInterrupted);
    void setCreeperSpecialCooldownAttacked(int cooldownAttacked);
    void setCreeperSpecialCooldownFrustrated(int cooldownFrustrated);
    void setCreeperSpecialCooldownOver(int cooldownOver);
    void setCreeperSpecialCooldownStunned(int cooldownStunned);
    void setCreeperSpecialStunnedDuration(int stunnedDuration);
    void setCreeperSpecialIgnitedTimeMax(int ignitedTimeMax);
    void setCreeperSpecialInterruptedMax(int interruptedMax);
    void setCreeperSpecialInterruptedDamage(float interruptedDamage);

}
