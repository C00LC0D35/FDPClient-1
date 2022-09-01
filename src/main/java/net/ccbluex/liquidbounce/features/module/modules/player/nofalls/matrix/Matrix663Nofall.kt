package net.ccbluex.liquidbounce.features.module.modules.player.nofalls.matrix

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.player.nofalls.NoFallMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer

class Matrix663Nofall : NoFallMode("Matrix6.6.3") {
    private var matrixSend = false
    private val matrixSafe = BoolValue("SafeNoFall", true)
    
    override fun onEnable() {
        matrixSend = false
    }
    override fun onDisable() {
        mc.timer.timerSpeed =1f
    }
    override fun onNoFall(event: UpdateEvent) {
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
            mc.thePlayer.fallDistance = 0.0f
            matrixSend = true
            if (matrixSafe.get()) {
                mc.timer.timerSpeed = 0.3f
                mc.thePlayer.motionX *= 0.5
                mc.thePlayer.motionZ *= 0.5
            } else {
                mc.timer.timerSpeed = 0.5f
            }
        } else {
            mc.timer.timerSpeed = 1f
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is C03PacketPlayer && matrixSend) {
            matrixSend = false
            event.cancelEvent()
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, true))
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, false))
        }

    }
}
