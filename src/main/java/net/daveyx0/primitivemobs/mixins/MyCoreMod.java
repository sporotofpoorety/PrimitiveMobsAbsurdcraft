import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("MyCoreModName")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class MyCoreMod implements IFMLLoadingPlugin, IEarlyMixinLoader { }

