/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.CooldownHelper
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.block.material.Material
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color


@ElementInfo(name = "ScaffoldCounter")
class Cooldown(x: Double = 0.0, y: Double = -80.0, scale: Float = 1F,
               side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.MIDDLE)) : Element(x, y, scale, side) {
  
    private val counterStyleValue = ListValue("CounterStyle", arrayOf("Rise", "Rise6", "Tenacity"), "Rise6")
    private val animTypeValue = EaseUtils.getEnumEasingList("AnimType")
    private val animOrderValue = EaseUtils.getEnumEasingOrderList("AnimOrder")
    private val animSpeedValue = IntegerValue("AnimSpeed", 7, 3, 20)
    
    private var time =  System.currentTimeMillis()
    private var pct = 0f
    private var lastUpdate =  System.currentTimeMillis()
    
    private var displayPercent = 0f

    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border {
        time = System.currentTimeMillis()
        pct = (time - lastUpdate) / (animSpeedValue.get() * 50f)
        lastUpdate = System.currentTimeMillis()
        
        if (target != null) {
            if (displayPercent < 1) {
                displayPercent += pct
            }
            if (displayPercent > 1) {
                displayPercent = 1f
            }
        } else {
            if (displayPercent > 0) {
                displayPercent -= pct
            }
            if (displayPercent < 0) {
                displayPercent = 0f
                prevTarget = null
                return getTBorder()
            }
        }
        
        val easedPersent = EaseUtils.apply(EaseUtils.EnumEasingType.valueOf(animTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(animOrderValue.get()), displayPercent.toDouble()).toFloat()
        val border = getTBorder() ?: return null
        GL11.glScalef(easedPersent, easedPersent, easedPersent)
        GL11.glTranslatef(((border.x2 * 0.5f * (1 - easedPersent)) / easedPersent), ((border.y2 * 0.5f * (1 - easedPersent)) / easedPersent), 0f)
        val progress = CooldownHelper.getAttackCooldownProgress()

        if (progress < 1.0) {
            RenderUtils.drawRect(-25f, 0f, 25.0f, 3f, Color(0, 0, 0, 150).rgb)
            RenderUtils.drawRect(-25f, 0f, 25.0f - 50.0f * progress.toFloat(), 3f, Color(0, 111, 255, 200).rgb)
        }

        
    }
    
    fun getTBorder(): Border? {
        return when (counterStyleValue.get().lowercase()) {
            "rise" -> Border(0F, 0F, 140F, 40F)
            "novoline2" -> Border(0F, 0F, 140F, 40F)
            "astolfo" -> Border(0F, 0F, 140F, 60F)
        }
    }
}
