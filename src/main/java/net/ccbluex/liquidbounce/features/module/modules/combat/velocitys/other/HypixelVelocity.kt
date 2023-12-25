package net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.other

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.VelocityMode
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.ccbluex.liquidbounce.utils.misc.RandomUtils

class HypixelVelocity : VelocityMode("Hypixel") {

    private val bw = BoolValue("BedwarsMode", false)

    override fun onVelocityPacket(event: PacketEvent) {
        val packet = event.packet
        if(packet is S12PacketEntityVelocity) {
            event.cancelEvent()
            if (!bw.get()) mc.thePlayer.motionY = packet.getMotionY().toDouble() / 8000.0
        }
    }
}
