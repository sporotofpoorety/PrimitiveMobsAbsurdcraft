package net.daveyx0.primitivemobs.core;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

@net.minecraftforge.fml.common.Optional.Interface(modid = "mixinbooter", iface = "zone.rong.mixinbooter.ILateMixinLoader")
public class FuturisticMobsLateMixins implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.late.primitivemobs.json");
    }
}
