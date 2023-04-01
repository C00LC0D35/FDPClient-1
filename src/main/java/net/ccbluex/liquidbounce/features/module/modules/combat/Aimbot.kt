/*
 * LiquidLite Ghost Client
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.utils.LocationCache
import kotlin.random.Random

@ModuleInfo(name = "Aimbot", category = ModuleCategory.COMBAT)
class Aimbot : Module() {

    private val rangeValue = FloatValue("Range", 4.4F, 1F, 8F)
    private val playerPredictValue = FloatValue("PlayerPredictAmount", 1.2f, -2f, 3f)
    private val opPredictValue = FloatValue("TargetPredictAmount", 1.5f, -2f, 3f)
    private val centerSpeed = FloatValue("CenterSpeed", 10F, 1F, 100F)
    private val centerRandom = FloatValue("CenterRandomRange", 1.0F, 0F, 15F)
    private val edgeSpeed = FloatValue("EdgeSpeed", 20F, 1F, 100F)
    private val edgeRandom = FloatValue("EdgeRandomRange", 1.0F, 0F, 15F)
    private val fovValue = FloatValue("FOV", 180F, 1F, 180F)
    private val onClickValue = BoolValue("OnClick", false)
    private val onClickDurationValue = IntegerValue("OnClickDuration", 500, 100, 1000).displayable { onClickValue.get() }
    private val jitterValue = BoolValue("Jitter", false)
    private val randomJitterValue = FloatValue("JitterRandomRate", 1.0F, 0F, 5.0F).displayable { jitterValue.get() }

    private val clickTimer = MSTimer()

    private var oldMouse = Rotation(0f, 0f)
    private var newMouse = Rotation(0f, 0f)

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.gameSettings.keyBindAttack.isKeyDown) {
            clickTimer.reset()
        }

        oldMouse = newMouse
        newMouse = Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)

        if (onClickValue.get() && clickTimer.hasTimePassed(onClickDurationValue.get().toLong())) {
            return
        }

        val range = rangeValue.get()
        val entity = mc.theWorld.loadedEntityList
            .filter {
                EntityUtils.isSelected(it, true) && mc.thePlayer.canEntityBeSeen(it) &&
                        mc.thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
            }
            .minByOrNull { RotationUtils.getRotationDifference(it) } ?: return

        entity.entityBoundingBox.offset((entity.posX - entity.lastTickPosX) * opPredictValue.get(),
                                        (entity.posY - entity.lastTickPosY) * opPredictValue.get(),
                                        (entity.posZ - entity.lastTickPosZ) * opPredictValue.get())
        entity.entityBoundingBox.offset(mc.thePlayer.motionX * -1f * playerPredictValue.get(),
                                        mc.thePlayer.motionY * -1f * playerPredictValue.get(),
                                        mc.thePlayer.motionX * -1f * playerPredictValue.get())

        val mouseSpeed = RotationUtils.getRotationDifference(oldMouse,  newMouse).toFloat()

        var playerRot = Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        var targetRot = RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), true)
        var rotDiff = RotationUtils.getRotationDifference(playerRot,  targetRot).toFloat()

        val rotationCenter = RotationUtils.limitAngleChange(
            playerRot, targetRot,
            (mouseSpeed.toFloat() / rotDiff.toFloat()) * mouseSpeed.toFloat() * (centerSpeed.get() + (centerRandom.get() * Math.random() * 0.5f)).toFloat() * 0.1f
        )

        rotationCenter.toPlayer(mc.thePlayer)

        targetRot = RotationUtils.searchCenter(entity.entityBoundingBox, false, false, true, false).rotation
        rotDiff = RotationUtils.getRotationDifference(playerRot,  targetRot).toFloat()

        val rotationEdge = RotationUtils.limitAngleChange(
            playerRot, targetRot,
            (mouseSpeed.toFloat() / rotDiff.toFloat()) * mouseSpeed.toFloat() * (edgeSpeed.get() + (edgeRandom.get() * Math.random() * 0.5f)).toFloat() * 0.1f
        )

        rotationEdge.toPlayer(mc.thePlayer)

        if (jitterValue.get()) {
            val yaw = Random.nextBoolean()
            val pitch = Random.nextBoolean()
            val yawNegative = Random.nextBoolean()
            val pitchNegative = Random.nextBoolean()

            if (yaw) {
                mc.thePlayer.rotationYaw += if (yawNegative) -RandomUtils.nextFloat(0F, randomJitterValue.get()) else RandomUtils.nextFloat(0F, randomJitterValue.get())
            }

            if (pitch) {
                mc.thePlayer.rotationPitch += if (pitchNegative) -RandomUtils.nextFloat(0F, randomJitterValue.get()) else RandomUtils.nextFloat(0F, randomJitterValue.get())
                if (mc.thePlayer.rotationPitch > 90) {
                    mc.thePlayer.rotationPitch = 90F
                } else if (mc.thePlayer.rotationPitch < -90) {
                    mc.thePlayer.rotationPitch = -90F
                }
            }
        }
    }
}
