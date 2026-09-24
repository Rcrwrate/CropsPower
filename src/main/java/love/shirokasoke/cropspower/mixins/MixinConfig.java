package love.shirokasoke.cropspower.mixins;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import love.shirokasoke.cropspower.MyMod;

@Config(modid = MyMod.MODID, category = "mixin", filename = "CropsPower", configSubDirectory = "shirokasoke")
public class MixinConfig {

    static {
        ConfigurationManager.registerConfig(MixinConfig.class);
    }

    @Config.Comment("Whether the Crop Manager should also drop the crop's seed when it harvests.")
    @Config.DefaultBoolean(true)
    public static boolean dropSeed;

    @Config.Comment({
        "The base horizontal working radius (in blocks) of the Crop Manager's working area, used by tier 0.",
        "The vanilla value is 3. The final radius is base + perTier * tier." })
    @Config.DefaultInt(3)
    @Config.RangeInt(min = 0, max = 64)
    public static int cropManagerHorizontalRadiusBase;

    @Config.Comment({ "How many extra blocks of horizontal radius the Crop Manager gains per voltage tier.",
        "The vanilla value is 2. The final radius is base + perTier * tier." })
    @Config.DefaultInt(2)
    @Config.RangeInt(min = 0, max = 64)
    public static int cropManagerHorizontalRadiusPerTier;

    @Config.Comment("The vertical working radius (in blocks) of the Crop Manager's working area. The vanilla value is 2.")
    @Config.DefaultInt(2)
    @Config.RangeInt(min = 0, max = 64)
    public static int cropManagerVerticalRadius;
}
