package love.shirokasoke.cropspower.server;

import java.util.Arrays;
import java.util.HashSet;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import love.shirokasoke.cropspower.MyMod;

@Config(modid = MyMod.MODID, category = "data", filename = "CropsPowerData", configSubDirectory = "shirokasoke")
public class DataConfig {

    @Config.Comment("You should never change this, it will generate automatically")
    @Config.DefaultStringList({})
    public static String[] on;

    @Config.Comment("You should never change this, it will generate automatically")
    @Config.DefaultStringList({})
    public static String[] off;

    @Config.Ignore
    public static HashSet<Coordinates> ons = new HashSet<Coordinates>();

    @Config.Ignore
    public static HashSet<Coordinates> offs = new HashSet<Coordinates>();

    // 必须放在所有字段声明之后：静态初始化按文本顺序执行，放在前面的话 load() 解析出的数据
    // 会被后面 ons/offs 的字段初始化覆盖掉。
    static {
        load();
    }

    public static boolean add(Coordinates coord, boolean changed) {
        final HashSet<Coordinates> target = changed ? ons : offs;
        if (!target.add(coord)) return false;
        (changed ? offs : ons).remove(coord);
        return true;
    }

    public static void load() {
        ConfigurationManager.registerConfig(DataConfig.class);
        ons = new HashSet<Coordinates>();
        offs = new HashSet<Coordinates>();
        read(on, ons, offs);
        read(off, offs, ons);
    }

    public static void save() {
        on = write(ons);
        off = write(offs);
        ConfigurationManager.save(DataConfig.class);
    }

    /**
     * 解析一组 Base64 坐标到 {@code target}，并把它们从 {@code opposite} 中移出
     */
    private static void read(String[] source, HashSet<Coordinates> target, HashSet<Coordinates> opposite) {
        if (source == null) return;
        for (String entry : source) {
            if (entry == null) continue;
            try {
                final Coordinates coord = Coordinates.read(entry);
                target.add(coord);
                opposite.remove(coord);
            } catch (IllegalArgumentException e) {
                MyMod.LOG.warn("Skipping malformed coordinates entry \"" + entry + "\" in CropsPowerData config", e);
            }
        }
    }

    /** HashSet 迭代顺序不稳定，排序后写出的配置内容才稳定，避免每次保存都整段变动。 */
    private static String[] write(HashSet<Coordinates> source) {
        final String[] result = new String[source.size()];
        int index = 0;
        for (Coordinates coord : source) {
            result[index++] = coord.write();
        }
        Arrays.sort(result);
        return result;
    }
}
