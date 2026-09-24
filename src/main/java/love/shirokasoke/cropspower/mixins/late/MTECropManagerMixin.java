package love.shirokasoke.cropspower.mixins.late;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.cropsnh.api.ICropStickTile;
import com.gtnewhorizon.cropsnh.tileentity.singleblock.MTECropManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import love.shirokasoke.cropspower.mixins.MixinConfig;

@Mixin(MTECropManager.class)
public class MTECropManagerMixin {

    @Unique
    private final boolean dropSeed = MixinConfig.dropSeed;

    /**
     * Adds the seed drop of the harvested crop to the drops harvested by the crop
     * manager.
     *
     * @see MTECropManager#harvest(gregtech.api.interfaces.tileentity.IGregTechTileEntity)
     */
    @WrapOperation(
        method = "harvest(Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gtnewhorizon/cropsnh/api/ICropStickTile;harvest(D)Ljava/util/ArrayList;"),
        remap = false)
    private ArrayList<ItemStack> cropspower$addSeedDrop(ICropStickTile crop, double dropMultiplier,
        Operation<ArrayList<ItemStack>> original) {
        ArrayList<ItemStack> harvestDrops = original.call(crop, dropMultiplier);
        // the machine treats a null drop list as "skip this crop", so keep it null
        if (harvestDrops == null) return null;
        if (dropSeed) {
            ItemStack seedDrop = crop.getSeedDrop();
            if (seedDrop != null) harvestDrops.add(seedDrop);
        }

        return harvestDrops;
    }

    /**
     * Overrides the horizontal working radius of the crop manager with the values from the config.
     *
     * @author shirokasoke
     * @reason Allow the crop manager's working radius to be configured.
     * @see MTECropManager#getHorizontalRadius(int)
     */
    @Overwrite(remap = false)
    public static int getHorizontalRadius(int tier) {
        return MixinConfig.cropManagerHorizontalRadiusBase
            + Math.max(0, MixinConfig.cropManagerHorizontalRadiusPerTier * tier);
    }

    /**
     * Overrides the vertical working radius of the crop manager with the value from the config.
     *
     * @author shirokasoke
     * @reason Allow the crop manager's working radius to be configured.
     * @see MTECropManager#getVerticalRadius()
     */
    @Overwrite(remap = false)
    private int getVerticalRadius() {
        return MixinConfig.cropManagerVerticalRadius;
    }
}
