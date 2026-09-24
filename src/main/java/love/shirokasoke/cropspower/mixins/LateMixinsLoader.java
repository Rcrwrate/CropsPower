package love.shirokasoke.cropspower.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

// The annotation is required, it indicates to
// the mixins framework to instantiate this class
// and look for LateMixins to load.
@LateMixin
public class LateMixinsLoader implements ILateMixinLoader {

    private static final Logger LOG = LogManager.getLogger("AP-LateMixins");

    @Override
    public String getMixinConfig() {
        return "mixins.cropspower.late.json";
    }

    @Nonnull
    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        // Register your late mixins here by adding them to the list.
        // The late mixins target classes from other mods.
        // The loadedMods contains the mod ID of currently loaded mods,
        // you can check this Set to conditionally load certain mixins.
        List<String> mixins = new ArrayList<>();

        // CropsNH is a hard dependency, but only add its mixins if the mod is actually present.
        if (loadedMods.contains("cropsnh")) {
            // Adds the crop's seed drop to the drops harvested by the crop manager.
            mixins.add("MTECropManagerMixin");
        }

        return mixins;
    }
}
