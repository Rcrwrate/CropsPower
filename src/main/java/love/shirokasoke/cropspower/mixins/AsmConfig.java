package love.shirokasoke.cropspower.mixins;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import love.shirokasoke.cropspower.MyMod;
import love.shirokasoke.cropspower.core.asm.CropManagerCacheRefreshTransformer;

@Config(modid = MyMod.MODID, category = "asm", filename = "CropsPower", configSubDirectory = "shirokasoke")
public class AsmConfig {

    static {
        ConfigurationManager.registerConfig(AsmConfig.class);
    }

    /**
     * default
     * 
     * @see {@link com.gtnewhorizon.cropsnh.tileentity.singleblock.MTECropManager#CACHE_REFRESH_ANY}
     */
    @Config.Ignore
    public static final int VANILLA_CACHE_REFRESH_MULTIPLIER = 12;

    @Config.Comment({
        "The multiplier used to compute the Crop Manager's crop cache refresh interval (CACHE_REFRESH_ANY).",
        "While the cache still contains crops it is refreshed every GLOBAL_UPDATE_RATE ticks times this value,",
        "so a higher value lowers the machine's load but delays new crops being picked up.",
        "The vanilla value is 12." })
    @Config.DefaultInt(VANILLA_CACHE_REFRESH_MULTIPLIER)
    @Config.RangeInt(min = 10, max = 30)
    @Config.RequiresMcRestart
    public static int cropManagerCacheRefreshMultiplier;

    /**
     * Scales the vanilla crop cache refresh interval by the configured multiplier. Used by
     * {@link CropManagerCacheRefreshTransformer} to compute the value it patches into CropsNH's bytecode.
     *
     * @param vanillaValue The vanilla value of {@code CACHE_REFRESH_ANY} baked into CropsNH.
     * @return The crop cache refresh interval that should be used instead.
     */
    public static int scaleCacheRefreshAny(int vanillaValue) {
        return (int) ((long) vanillaValue * cropManagerCacheRefreshMultiplier / VANILLA_CACHE_REFRESH_MULTIPLIER);
    }
}
