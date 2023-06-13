/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.utils

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.minecraft.network.play.client.*
import java.util.*

/**
 * BlinkUtils | FDPClient Original
 * Code by Dg636
 * Date: 2023/06/12
 */


object BlinkUtils : MinecraftInstance() {
    private val packetBuffer = LinkedList<Packet<INetHandlerPlayServer>>()
    var minDelay = 0
    var maxDelay = 0

    init {
        setPingspoofState(
            off = true,
            release = true
        )
        clearPacket()
    }


    fun clearPacket() {
        packetBuffer.forEach {
            PacketUtils.sendPacketNoEvent(it)
        }
        packetBuffer.clear()
    }

    fun setPingspoofState() {
        setPingspoofState(off = true)
        clearPacket()
    }

    fun setPingspoofState(
        off: Boolean = false,
        release: Boolean = false,
        minDelay: Int = 0,
        maxDelay: Int = 0
    ) {
        if (release) {
            clearPacket()
        }
        minDelay = minDelay
        maxDelay = maxDelay
    }
    
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet.javaClass.simpleName.startsWith("C", ignoreCase = true)) {
            if (packet is C00Handshake || packet is C00PacketLoginStart || packet is C00PacketServerQuery || packet is C01PacketChatMessage || packet is C01PacketEncryptionResponse || packet is C01PacketPing) return
            event.cancelEvent()
            packetBuffer.add(packet as Packet<INetHandlerPlayServer>)
            queuePacket(TimeUtils.randomDelay(minDelay, maxDelay))
        }
    }
    
    // maybe coroutine better?
    private /*suspend*/ fun queuePacket(delayTime: Long) {
        Timer().schedule(delayTime) {
            if (this@PingSpoof.state) {
                PacketUtils.sendPacketNoEvent(packetBuffer.poll())
            }
        }
    }
}
