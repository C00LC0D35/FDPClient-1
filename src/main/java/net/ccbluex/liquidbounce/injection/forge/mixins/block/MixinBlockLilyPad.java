/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.ccbluex.liquidbounce.handler.protocol.ProtocolBase;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockLilyPad.class)
public abstract class MixinBlockLilyPad extends BlockBush {
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        if (ProtocolBase.getManager().getTargetVersion().getVersion() != ProtocolVersion.v1_8.getVersion() && !MinecraftInstance.mc.isIntegratedServerRunning())
            return new AxisAlignedBB((double) pos.getX() + 0.0625D, (double) pos.getY() + 0.0D, (double) pos.getZ() + 0.0625D, (double) pos.getX() + 0.9375D, (double) pos.getY() + 0.09375D, (double) pos.getZ() + 0.9375D);
        return new AxisAlignedBB((double) pos.getX() + 0.0D, (double) pos.getY() + 0.0D, (double) pos.getZ() + 0.0D, (double) pos.getX() + 1.0D, (double) pos.getY() + 0.015625D, (double) pos.getZ() + 1.0D);
    }
}
