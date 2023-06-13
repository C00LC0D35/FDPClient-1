/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.minecraft.network.play.client.*
import java.util.*
import kotlin.concurrent.schedule

/**
 * BlinkUtils | FDPClient Original
 * Code by Dg636
 * Date: 2023/06/12
 */


object PingspoofUtils : MinecraftInstance() {
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
        minD: Int = 0,
        maxD: Int = 0
    ) {
        if (release) {
            clearPacket()
        }
        minDelay = minD
        maxDelay = maxD
    }
    
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val packetName = packet.javaClass.simpleName
        if (packetName.startsWith("C", ignoreCase = true)) {
            if (packetName.startsWith("C00") || packetName.startsWith("C01")) return
            event.cancelEvent()
            packetBuffer.add(packet as Packet<INetHandlerPlayServer>)
            queuePacket(TimeUtils.randomDelay(minDelay, maxDelay))
        }
    }
    
    // maybe coroutine better?
    private /*suspend*/ fun queuePacket(delayTime: Long) {
        Timer().schedule(delayTime) {
            PacketUtils.sendPacketNoEvent(packetBuffer.poll())
        }
    }
}
