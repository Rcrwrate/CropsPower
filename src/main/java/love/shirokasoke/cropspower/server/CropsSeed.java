package love.shirokasoke.cropspower.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.gtnewhorizon.cropsnh.tileentity.singleblock.MTECropManager;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * The mod's {@code /cropsseed} command, used to switch the automatic seed harvesting of the Crop Managers around the
 * player on or off, so they don't have to be hit with a soft mallet one by one. The state is stored per coordinate in
 * {@link DataConfig}, so a manager keeps its setting after its chunk is reloaded.
 */
public class CropsSeed extends CommandBase {

    /** The distance, in blocks, the command scans around the player in every direction. */
    private static final int SCAN_RADIUS = 2;

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "cropsseed";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("cropsseed");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cropsseed [on|off]";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "on", "off");
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || !("on".equalsIgnoreCase(args[0]) || "off".equalsIgnoreCase(args[0]))) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        final boolean enable = "on".equalsIgnoreCase(args[0]);
        final ChunkCoordinates origin = sender.getPlayerCoordinates();
        if (origin == null) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "This command can only be run from a position in the world."));
            return;
        }

        final Coordinates coord = findCropManagers(sender.getEntityWorld(), origin.posX, origin.posY, origin.posZ);
        if (coord == null) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.YELLOW + "No Crop Manager within "
                        + SCAN_RADIUS
                        + " blocks around you. "
                        + origin.toString()));
            return;
        }

        if (!DataConfig.add(coord, enable)) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.YELLOW + "The Crop Manager at "
                        + coord.toString()
                        + " is already "
                        + (enable ? "harvesting seeds" : "not harvesting seeds")
                        + "."));
            return;
        }

        DataConfig.save();
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GREEN + (enable ? "Enabled" : "Disabled")
                    + " seed harvesting for the Crop Manager at "
                    + coord.toString()
                    + "."));
    }

    private static Coordinates findCropManagers(World world, int centerX, int centerY, int centerZ) {
        for (int x = centerX - SCAN_RADIUS; x <= centerX + SCAN_RADIUS; x++) {
            for (int y = centerY - SCAN_RADIUS; y <= centerY + SCAN_RADIUS; y++) {
                if (y < 0 || y >= world.getHeight()) continue;
                for (int z = centerZ - SCAN_RADIUS; z <= centerZ + SCAN_RADIUS; z++) {
                    final TileEntity tileEntity = world.getTileEntity(x, y, z);
                    if (!(tileEntity instanceof IGregTechTileEntity machine)) continue;
                    if (machine.getMetaTileEntity() instanceof MTECropManager) {
                        return new Coordinates(x, y, z, world.provider.dimensionId);
                    }
                }
            }
        }
        return null;
    }
}
