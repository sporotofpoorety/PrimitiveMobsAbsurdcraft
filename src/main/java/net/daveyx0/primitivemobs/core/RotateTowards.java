package net.daveyx0.primitivemobs.core;

import org.lwjgl.util.vector.Quaternion;

import net.minecraft.util.math.Vec3d;


public class RotateTowards {

    public static Quaternion rotateTowards(Vec3d rotate, Vec3d towards) 
    {
        Vec3d ONE = new Vec3d(0.0, 1.0, 0.0);
        Vec3d nt = towards.subtract(rotate).normalize();
        if (nt.y != -1.0) 
        {
            Vec3d half = ONE.add(nt).normalize();
            Vec3d x = ONE.crossProduct(half);
            return new Quaternion((float)x.x, (float)x.y, (float)x.z, (float)ONE.dotProduct(half));
        } 
        else 
        {
            return new Quaternion(1.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}
