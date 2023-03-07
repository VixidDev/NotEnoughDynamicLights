package dev.vixid.notenoughdynamiclights.commands

import dev.vixid.notenoughdynamiclights.NotEnoughDynamicLights
import dev.vixid.notenoughdynamiclights.DynamicLightItemsEditor
import dev.vixid.notenoughdynamiclights.utils.Utils
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class DynamicLightItemsCommand : CommandBase() {

    override fun getCommandName(): String {
        return "nedl"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/nedl"
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (NotEnoughDynamicLights.foundNEU) {
            Utils.addChatMessage("§c[NEDL] Found NEU installed, please use their implementation of this feature with /neudli")
            return
        }
        NotEnoughDynamicLights.openGui = DynamicLightItemsEditor()
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }
}