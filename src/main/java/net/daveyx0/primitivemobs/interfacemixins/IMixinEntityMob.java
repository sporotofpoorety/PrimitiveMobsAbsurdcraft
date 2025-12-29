package net.daveyx0.primitivemobs.interfacemixins;

import net.minecraft.network.datasync.DataParameter;

public interface IMixinEntityMob
{
    public boolean isTamed();

    public DataParameter<Boolean> getAbsurdcraftStunnedDataParameter();
    public boolean getAbsurdcraftStunned();
    public int getAbsurdcraftStunnedTimer();

    public void setAbsurdcraftStunned(boolean isStunned);
    public void setAbsurdcraftStunnedTimer(int time);
}
