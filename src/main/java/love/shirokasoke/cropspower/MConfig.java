package love.shirokasoke.cropspower;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = MyMod.MODID, filename = "CropsPower", configSubDirectory = "shirokasoke")
@Config.RequiresWorldRestart
public class MConfig {

    static {
        ConfigurationManager.registerConfig(MConfig.class);
    }

    @Config.DefaultString("禾下乘凉梦，一梦逐一生")
    public static String greeting;
}
