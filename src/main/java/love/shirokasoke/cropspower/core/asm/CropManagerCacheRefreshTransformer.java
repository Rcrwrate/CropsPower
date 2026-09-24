package love.shirokasoke.cropspower.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import love.shirokasoke.cropspower.mixins.AsmConfig;

/**
 * Replaces the crop cache refresh interval inlined into CropsNH's {@code MTECropManager#onPostTick} with the value
 * computed by {@link AsmConfig#scaleCacheRefreshAny(int)}, so the interval's multiplier can be configured.
 * <p>
 * {@code CACHE_REFRESH_ANY} is a compile time constant, meaning its value is baked into every method using it. That is
 * why it can't be replaced with a mixin, hence the ASM transformation.
 */
public class CropManagerCacheRefreshTransformer implements IClassTransformer {

    private static final Logger LOG = LogManager.getLogger("CropsPower-ASM");

    private static final String TARGET_CLASS = "com.gtnewhorizon.cropsnh.tileentity.singleblock.MTECropManager";
    private static final String TARGET_METHOD = "onPostTick";
    private static final String TARGET_METHOD_DESC = "(Lgregtech/api/interfaces/tileentity/IGregTechTileEntity;J)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || (!TARGET_CLASS.equals(transformedName) && !TARGET_CLASS.equals(name))) {
            return basicClass;
        }
        // leave the class untouched while the multiplier is still at its vanilla value
        if (AsmConfig.cropManagerCacheRefreshMultiplier == AsmConfig.VANILLA_CACHE_REFRESH_MULTIPLIER) {
            return basicClass;
        }

        try {
            return patch(basicClass);
        } catch (Throwable t) {
            LOG.error(
                "Failed to patch the crop cache refresh interval of {}, keeping the vanilla behaviour",
                TARGET_CLASS,
                t);
            return basicClass;
        }
    }

    private static byte[] patch(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if (!TARGET_METHOD.equals(method.name) || !TARGET_METHOD_DESC.equals(method.desc)) continue;

            IntInsnNode refreshInterval = findCacheRefreshAny(method.instructions);
            if (refreshInterval == null) break;

            int vanillaValue = refreshInterval.operand;
            final int scaledValue = AsmConfig.scaleCacheRefreshAny(vanillaValue);
            // push with ldc when the scaled value doesn't fit into a sipush operand
            AbstractInsnNode scaledConstant = scaledValue >= Short.MIN_VALUE && scaledValue <= Short.MAX_VALUE
                ? new IntInsnNode(Opcodes.SIPUSH, scaledValue)
                : new LdcInsnNode(scaledValue);
            method.instructions.set(refreshInterval, scaledConstant);
            LOG.info(
                "Patched the crop cache refresh interval of {} from {} to {} ticks",
                TARGET_CLASS,
                vanillaValue,
                scaledValue);

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        LOG.warn(
            "Could not find the crop cache refresh interval in {}#{}, keeping the vanilla behaviour",
            TARGET_CLASS,
            TARGET_METHOD);
        return basicClass;
    }

    /**
     * Finds the {@code sipush} instruction holding {@code CACHE_REFRESH_ANY}. The compiler inlines the surrounding
     * constants, so it's the only {@code sipush} in the method and the value it pushes is stored into a local.
     */
    private static IntInsnNode findCacheRefreshAny(InsnList instructions) {
        IntInsnNode refreshInterval = null;
        for (AbstractInsnNode insn = instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() != Opcodes.SIPUSH) continue;
            if (refreshInterval != null) return null; // more than one candidate, refusing to guess
            refreshInterval = (IntInsnNode) insn;
        }
        if (refreshInterval == null) return null;

        // labels, frames and line numbers don't have an opcode
        AbstractInsnNode next = refreshInterval.getNext();
        while (next != null && next.getOpcode() < 0) next = next.getNext();
        return next != null && next.getOpcode() == Opcodes.ISTORE ? refreshInterval : null;
    }
}
