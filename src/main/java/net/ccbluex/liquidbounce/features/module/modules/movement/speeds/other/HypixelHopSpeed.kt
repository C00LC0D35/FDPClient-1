/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.FDPClient
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.player.Eagle
import net.ccbluex.liquidbounce.value.*
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.client.settings.GameSettings
import net.minecraft.potion.Potion
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing


class HypixelHopSpeed : SpeedMode("HypixelHop") {

    private val bypassMode = ListValue("${valuePrefix}BypassMode", arrayOf("Latest", "Legit", "GroundStrafe"), "Latest")
    private val customSpeedBoost = FloatValue("${valuePrefix}SpeedPotJumpModifier", 0.1f, 0f, 0.4f)
    private val yMotion = FloatValue("${valuePrefix}JumpYMotion", 0.42f, 0.395f, 0.42f)
    private val damageBoost = BoolValue("${valuePrefix}DamageBoost", true)
    private val sussyPacket = BoolValue("${valuePrefix}Rise6sussyPacket", false)
    private val fallingStrafe = BoolValue("${valuePrefix}FallingDamageStrafe", false)
    private val fastFall = BoolValue("${valuePrefix}FastFall", false)
    private val sneakStrafe = BoolValue("${valuePrefix}SneakStrafe", true)


    private var minSpeed = 0.0
  
    private var wasOnGround = false
    private var offGroundTicks = 0
    private var groundTick = 0

    override fun onPreMotion() {
        if (!MovementUtils.isMoving()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
        
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0
        } else {
            offGroundTicks += 1
        }

        if (fallingStrafe.get() && mc.thePlayer.motionY < 0.0 && mc.thePlayer.hurtTime > 0) {
            MovementUtils.strafe()
        }

        if (fastFall.get()) {
            mc.thePlayer.motionY = when (offGroundTicks) {
                5 -> -0.0263837384
                6 -> -0.19025606858
                7 -> -0.26485095
                8 -> -0.3379539399
                9 -> -0.3997423801
                else -> mc.thePlayer.motionY
            }
        }

        if (damageBoost.get()) {
            if (mc.thePlayer.hurtTime == 9) {
                MovementUtils.strafe(MovementUtils.getSpeed() * 0.7f)
            }
        }

        if (sneakStrafe.get()) {
            if (offGroundTicks > 0 && offGroundTicks < 5) {
                MovementUtils.strafe()
                mc.gameSettings.keyBindSneak.pressed = true
            } else {
                mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
            }
        }



        
        when (bypassMode.get().lowercase()) {
            
            "latest" -> {
                if (sussyPacket.get()) {
                    PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, BlockPos(-1,-1,-1), EnumFacing.UP))
                    }

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()

                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MovementUtils.strafe(0.535f)
                    } else {
                        MovementUtils.strafe(0.46f)
                    }
                    
                } else {
                    
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        mc.thePlayer.motionX *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
                        mc.thePlayer.motionZ *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
                        mc.thePlayer.speedInAir = 0.02f + 0.0003f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1).toFloat()
                    }
                }
            }
            
            "legit" -> {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                }
            }
            "groundstrafe" -> {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe(MovementUtils.getSpeed())
                }
            }
        }
    }
                    
   
    
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) {
                return
            }
            if (!FDPClient.combatManager.inCombat) {
                return
            }
            
            if (packet.motionY / 8000.0 > 0.1) {
                if (damageBoost.get()) {
                    mc.thePlayer.motionX *= 1.07
                    mc.thePlayer.motionZ *= 1.07
                }
            }
        }
    }
}
