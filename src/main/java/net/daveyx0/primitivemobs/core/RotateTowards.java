package net.daveyx0.primitivemobs.core;

import org.lwjgl.util.vector.Quaternion;

import net.minecraft.util.math.Vec3d;


public final class RotateTowards {

    public static Quaternion rotateTowards(Vec3d rotate, Vec3d towards) 
    {
//UP
        Vec3d UP = new Vec3d(0.0, 1.0, 0.0);
        Vec3d nt = towards.subtract(rotate).normalize();
        if (nt.y != -1.0) 
        {
            Vec3d half = UP.add(nt).normalize();
            Vec3d x = UP.crossProduct(half);
            return new Quaternion((float)x.x, (float)x.y, (float)x.z, (float)UP.dotProduct(half));
        } 
        else 
        {
            return new Quaternion(1.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}

/*

Backwards, up (0, 0, -1), (0, 1, 0)

Backwards, up right (0, 0, -1), (0.7, 0.7, 0)

Mutl


0.49, 0.49, -0.7
*/
