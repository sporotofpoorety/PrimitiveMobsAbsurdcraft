package net.daveyx0.primitivemobs.core;

import net.minecraft.util.math.Vec3d;


public class AbsurdcraftMathUtils {

//It's pretty complicated but it should work fine

    public static Vec3d[] makeOrthonormalBasis(Vec3d originalVector) {
        Vec3d forwardV = originalVector.normalize();
        Vec3d upV;

        if (Math.abs(forwardV.x) > Math.abs(forwardV.z)) {
            upV = new Vec3d(-forwardV.y, forwardV.x, 0).normalize();
        } else {
            upV = new Vec3d(0, -forwardV.z, forwardV.y).normalize();
        }

        Vec3d rightVctor = forwardV.crossProduct(upV).normalize();

        return new Vec3d[]{ forwardV, upV, rightVctor };
    }
}
