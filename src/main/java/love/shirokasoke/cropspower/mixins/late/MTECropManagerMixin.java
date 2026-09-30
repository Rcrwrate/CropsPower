package love.shirokasoke.cropspower.mixins.late;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.api.ICropStickTile;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.tileentity.singleblock.MTECropManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import love.shirokasoke.cropspower.mixins.MixinConfig;

@Mixin(MTECropManager.class)
public class MTECropManagerMixin {

    @Unique
    private final boolean dropSeed = MixinConfig.dropSeed;

    /**
     * Packed block coordinates (see {@link #cropspower$packCoords}) of the crops
     * the
     * manager must not harvest, currently the four horizontal neighbours of every
     * cross
     * crop since those are used as the crossbreeding parents.
     */
    @Unique
    private final LongOpenHashSet crosslist = new LongOpenHashSet();

    /**
     * Callback fired right after a crop stick has been registered into the crop
     * manager's crop cache.
     * <p>
     * A cross crop breeds with the crops in its four horizontal neighbours, so
     * those
     * coordinates are put on the cross list to keep the manager from harvesting the
     * parents.
     *
     * @param tileEntity the tile entity of the crop stick, used for its coordinates
     * @param cropTE     the crop stick tile that was just added to the crop cache
     * @see MTECropManager#updateCropCache(gregtech.api.interfaces.tileentity.IGregTechTileEntity)
     */
    @Inject(
        method = "updateCropCache(Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/HashSet;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER),
        remap = false,
        require = 1)
    private void cropspower$onCropCached(CallbackInfo ci, @Local TileEntity tileEntity, @Local ICropStickTile cropTE) {
        if (!cropTE.isCrossCrop()) return;
        final int x = tileEntity.xCoord;
        final int y = tileEntity.yCoord;
        final int z = tileEntity.zCoord;
        // block the four horizontal neighbours, harvesting them would break the
        // crossbreeding
        crosslist.add(cropspower$packCoords(x + 1, y, z));
        crosslist.add(cropspower$packCoords(x - 1, y, z));
        crosslist.add(cropspower$packCoords(x, y, z + 1));
        crosslist.add(cropspower$packCoords(x, y, z - 1));
    }

    /**
     * Packs block coordinates into a single long so a position can be stored in a
     * set
     * without allocating an object for it. Layout: x (26 bits) | y (12 bits) | z
     * (26
     * bits), the same packing vanilla uses for its BlockPos.
     */
    @Unique
    private static long cropspower$packCoords(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

    /**
     * Callback fired right after the crop manager empties its crop cache, so the
     * tracked
     * cross list gets reset alongside it and doesn't keep stale entries.
     *
     * @see MTECropManager#updateCropCache(gregtech.api.interfaces.tileentity.IGregTechTileEntity)
     */
    @Inject(
        method = "updateCropCache(Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/HashSet;clear()V", shift = At.Shift.AFTER),
        remap = false,
        require = 1)
    private void cropspower$resetCrosslist(CallbackInfo ci) {
        crosslist.clear();
    }

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
        remap = false,
        require = 1)
    private ArrayList<ItemStack> cropspower$addSeedDrop(ICropStickTile crop, double dropMultiplier,
        Operation<ArrayList<ItemStack>> original) {
        if (crop instanceof TileEntity te
            && crosslist.contains(cropspower$packCoords(te.xCoord, te.yCoord, te.zCoord))) {
            // MyMod.LOG.info("hit crosslist, skiped");
            return null;
        }
        ISeedData seedData = crop.getSeed();
        if (!seedData.getStats()
            .isAnalyzed()) {
            seedData.setAnalyzed(true);
        }
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
     * Overrides the horizontal working radius of the crop manager with the values
     * from the config.
     *
     * @author shirokasoke
     * @reason Allow the crop manager's working radius to be configured.
     * @see MTECropManager#getHorizontalRadius(int)
     */
    @org.spongepowered.asm.mixin.Overwrite(remap = false)
    public static int getHorizontalRadius(int tier) {
        return MixinConfig.cropManagerHorizontalRadiusBase
            + Math.max(0, MixinConfig.cropManagerHorizontalRadiusPerTier * tier);
    }

    /**
     * Overrides the vertical working radius of the crop manager with the value from
     * the config.
     *
     * @author shirokasoke
     * @reason Allow the crop manager's working radius to be configured.
     * @see MTECropManager#getVerticalRadius()
     */
    @org.spongepowered.asm.mixin.Overwrite(remap = false)
    private int getVerticalRadius() {
        return MixinConfig.cropManagerVerticalRadius;
    }
}
