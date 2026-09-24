package love.shirokasoke.cropspower.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
// 只排除本项目自己的 core 包（ASM 所在处），不要排除整个 mod，被排除的包会完全跳过 LaunchClassLoader 的转换链（含 mixin）
@IFMLLoadingPlugin.TransformerExclusions("love.shirokasoke.cropspower.core")
public class EarlyMixinsLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final Logger LOG = LogManager.getLogger("AP-EarlyMixins");

    @Override
    public String[] getASMTransformerClass() {
        // Patches the crop cache refresh interval CropsNH inlines into the Crop Manager so it can be configured.
        return new String[] { "love.shirokasoke.cropspower.core.asm.CropManagerCacheRefreshTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        // rename the associated .json file by replacing the "mymodid" with your own mod ID
        // in the .json file edit the "package" and "refmap" properties to match your mod
        // also edit the "refmap" property in the "mixins.mymodid.json" file
        return "mixins.cropspower.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {

        List<String> mixins = new ArrayList<>();

        // Register your early mixins here by adding them to the list.
        // The early mixins target vanilla/forge classes and are loaded
        // during the CoreMod phase, before any mod initialization.
        //
        // Example:
        // if (MixinConfig.enableSomeEarlyMixin) {
        // mixins.add("SomeEarlyMixin");
        // }

        if (!mixins.isEmpty()) {
            LOG.info("Enabled early mixins: {}", mixins);
        }

        return mixins;
    }
}
