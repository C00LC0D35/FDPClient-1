/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.client;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.client.button.*;
import net.ccbluex.liquidbounce.value.ListValue;
import net.ccbluex.liquidbounce.value.TextValue;
import net.minecraft.client.gui.GuiButton;

@ModuleInfo(name = "Spoofer", category = ModuleCategory.CLIENT, defaultOn = true)
public class ClientSpoof extends Module {

    public final ListValue modeValue = new ListValue("Payloads", new String[]{"Vanilla", "Fabric", "Lunar", "LabyMod", "Custom", "CheatBreaker", "PvPLounge"}, "Lunar");
    public final TextValue CustomClient = new TextValue("CustomClientSpoof", "CustomClient");
    public final ListValue buttonValue = new ListValue("Button", new String[]{"Better", "RGBRounded", "Wolfram", "Rounded", "Hyperium", "RGB", "Badlion", "PVP", "Flat", "FLine", "Rise", "Vanilla"}, "PVP");
    public AbstractButtonRenderer getButtonRenderer(GuiButton button) {
        String lowerCaseButtonValue = buttonValue.get().toLowerCase();
        switch (lowerCaseButtonValue) {
            case "better":
                return new BetterButtonRenderer(button);
            case "rounded":
                return new RoundedButtonRenderer(button);
            case "fline":
                return new FLineButtonRenderer(button);
            case "rise":
                return new RiseButtonRenderer(button);
            case "hyperium":
                return new HyperiumButtonRenderer(button);
            case "rgb":
                return new RGBButtonRenderer(button);
            case "badlion":
                return new BadlionTwoButtonRenderer(button);
            case "rgbrounded":
                return new RGBRoundedButtonRenderer(button);
            case "wolfram":
                return new WolframButtonRenderer(button);
            case "pvp":
                return new PvPClientButtonRenderer(button);
            default:
                return null; // vanilla or unknown
        }
    }
    @Override
    public String getTag() {
        return modeValue.get();
    }
}