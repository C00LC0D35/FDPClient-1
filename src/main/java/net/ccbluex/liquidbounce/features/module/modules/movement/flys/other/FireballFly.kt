/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flys.other

import net.ccbluex.liquidbounce.FDPClient
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.features.module.modules.visual.FreeLook
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.client.settings.GameSettings
import net.minecraft.item.ItemFireball
import net.minecraft.client.settings.KeyBinding

class FireballFly : FlyMode("Fireball") {


    private val boostValue = FloatValue("${valuePrefix}BoostAmount", 1.2f, 1f, 2f)

    private var velocitypacket = false
    private var ticks = 0
    private var beforeVelo = false

    private var startingSlot = 0
    private var veloStatus = false

    override fun onEnable() {
        var fbSlot = getFBSlot()
        if (fbSlot == -1) {
            ClientUtils.displayChatMessage("§8[§c§lFireball-Flight§8] §aYou need a fireball to fly.")
            fly.state = false
        } else {
            veloStatus = FDPClient.moduleManager[Velocity::class.java]!!.state
            FDPClient.moduleManager[Velocity::class.java]!!.state = false
            velocitypacket = false
            beforeVelo = true
            ticks = 0
            startingSlot = mc.thePlayer.inventory.currentItem
            mc.thePlayer.inventory.currentItem = fbSlot
        }
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 1.0f
        if (beforeVelo) {
            if (mc.thePlayer.onGround) {
                mc.gameSettings.keyBindForward.pressed = true
                mc.thePlayer.jump()
                MovementUtils.strafe(0.46f)
                FDPClient.moduleManager[FreeLook::class.java]!!.enable()
                ticks = 0
            } else if (ticks == 1) {
                mc.thePlayer.rotationYaw += 180f
                mc.thePlayer.rotationPitch = 70f
                mc.gameSettings.keyBindBack.pressed = true
                mc.gameSettings.keyBindForward.pressed = false
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            } else if (ticks == 3) {
                mc.thePlayer.rotationYaw += 180f
                mc.thePlayer.rotationPitch = 30f
                FDPClient.moduleManager[FreeLook::class.java]!!.disable()
                mc.gameSettings.keyBindForward.pressed = true
                mc.gameSettings.keyBindBack.pressed = false
            }
        } else {
            if (ticks > 6) {
                mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
                fly.state = false
            }
        }

        ticks ++
        
        if(velocitypacket) {
            mc.thePlayer.motionX *=  boostValue.get().toDouble()
            mc.thePlayer.motionZ *=  boostValue.get().toDouble()
            velocitypacket = false
            beforeVelo = false
            ticks = 0
            FDPClient.moduleManager[Velocity::class.java]!!.state = veloStatus
        }
    }

    override fun onDisable() {
        FDPClient.moduleManager[Velocity::class.java]!!.state = veloStatus
        mc.timer.timerSpeed = 1f
        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
        mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
        mc.thePlayer.inventory.currentItem = startingSlot
        FDPClient.moduleManager[FreeLook::class.java]!!.disable()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S27PacketExplosion ) {
            velocitypacket = true
        }
    }

    private fun getFBSlot(): Int {
        for(i in 36..45) {
            var stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack()
            if (stack != null && stack.getItem() is ItemFireball) {
                return i - 36
            }
        }
        return -1
    }
}
