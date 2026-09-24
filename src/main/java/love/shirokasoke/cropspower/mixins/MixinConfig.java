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
}
