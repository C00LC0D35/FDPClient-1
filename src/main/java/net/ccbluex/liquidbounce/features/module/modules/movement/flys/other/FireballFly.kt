/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flys.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.network.play.server.S27PacketExplosion

class FireballFly : FlyMode("Fireball") {


    private val warn = BoolValue("${valuePrefix}DamageWarn",true)
    private val boostValue = FloatValue("${valuePrefix}BoostAmount", 1.2f, 1f, 2f)

    private var velocitypacket = false

    override fun onEnable() {
        if (warn.get())
            ClientUtils.displayChatMessage("§8[§c§lFireball-Flight§8] §aGetting exlposion from a fireball or tnt from behind is required to bypass.")
        velocitypacket = false
        mc.thePlayer.rotationYaw += 180f
        mc.thePlayer.rotationPitch = 70f
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 1.0f
        if(velocitypacket) {
            mc.thePlayer.rotationYaw += 180f
            mc.thePlayer.rotationPitch = 30f
            mc.thePlayer.motionX *=  boostValue.get().toDouble()
            mc.thePlayer.motionZ *=  boostValue.get().toDouble()
            velocitypacket = false
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S27PacketExplosion ) {
            velocitypacket = true
        }
    }
}
