package dev.vixid.notenoughdynamiclights

import dev.vixid.notenoughdynamiclights.utils.ItemData
import dev.vixid.notenoughdynamiclights.utils.Utils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.ceil

class DynamicLightItemsEditor : GuiScreen() {

    val background = ResourceLocation("notenoughdynamiclights", "dynamic_light_items_editor.png")
    val enabledButton = ResourceLocation("notenoughdynamiclights", "enabled_button.png")
    val disabledButton = ResourceLocation("notenoughdynamiclights", "disabled_button.png")
    val chestGui = ResourceLocation("textures/gui/container/generic_54.png")
    val widgets = ResourceLocation("textures/gui/widgets.png")
    val help = ResourceLocation("notenoughdynamiclights", "help.png")

    var xSize = 217
    var ySize = 88
    var guiLeft = 0
    var guiTop = 0

    var stackToRender: String = ""
    var itemSelected: String = ""

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()

        val numOfItems = NotEnoughDynamicLights.config.dynamicLightItems.size
        val numOfRows = if (didApplyMixin) ceil(numOfItems / 9f).toInt() else 0
        ySize = 70 + 18 * numOfRows
        guiLeft = (width - xSize) / 2
        guiTop = (height - ySize) / 2

        // Top and bottom half of gui
        Minecraft.getMinecraft().textureManager.bindTexture(background)
        Utils.drawTexturedRect(guiLeft.toFloat(), guiTop.toFloat(), xSize.toFloat(), 24F,
            0F, 1F, 0F, 24 / 88f, GL11.GL_NEAREST)
        Utils.drawTexturedRect(guiLeft.toFloat(), (guiTop + ySize - 46).toFloat(), xSize.toFloat(), 46F,
            0F, 1F, 42 / 88f, 1F, GL11.GL_NEAREST)

        fontRendererObj.drawString("Dynamic Light Items Editor", guiLeft + 10, guiTop + 7, 4210752)

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(help)
        Utils.drawTexturedRect((guiLeft + xSize + 3).toFloat(), guiTop.toFloat(), 16F, 16F, GL11.GL_NEAREST)
        if (mouseX >= guiLeft + xSize + 3 &&
            mouseX <= guiLeft + xSize + 19 &&
            mouseY >= guiTop &&
            mouseY <= guiTop + 16) {
            val tooltip = listOf(
                "§bDynamic Light Item Editor",
                "§eWhat is this?",
                "§eNEDL makes use of OptiFine's feature of certain items",
                "§eemitting dynamic light. By default OptiFine only implements",
                "§ethis feature for a select few minecraft items.",
                "",
                "§eThis editor however, allows you to add specific skyblock",
                "§eitems that will emit dynamic light when held. Simply hold the",
                "§eitem you wish to add, then open this menu again and click",
                "§e'Add Held Item', now if you have OptiFine installed and the",
                "§edynamic lights option enabled, the added items will emit light!",
                "",
                "§eTo remove an item, click the item in this menu and click",
                "§ethe 'Remove Item' button in the bottom right.",
                "",
                "§cNOTE: This is feature I first implemented in NEU but due",
                "§cto a request, I have also made this feature a standalone mod.",
                "§cThe NEU version works much better than this version due to",
                "§ca lot of code that is included in NEU and not in this mod."
            )
            Utils.drawHoveringText(tooltip as MutableList<String>, mouseX, mouseY, width, height)
        }

        if (!didApplyMixin) {
            fontRendererObj.drawString("Could not find OptiFine!", guiLeft + 50, guiTop + 22, Color.RED.rgb)
            fontRendererObj.drawString("Open an Issue on the", guiLeft + 52, guiTop + 32, Color.RED.rgb)
            fontRendererObj.drawString("GitHub for help", guiLeft + 65, guiTop + 42, Color.RED.rgb)
            return
        }

        // Buttons
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(enabledButton)
        Utils.drawTexturedRect(guiLeft.toFloat() + 15, (guiTop + ySize - 32).toFloat(), 88F, 20F,
            0F, 1F, 0F, 1F, GL11.GL_NEAREST)

        if (itemSelected.isNotEmpty()) {
            Minecraft.getMinecraft().textureManager.bindTexture(enabledButton)
        } else {
            Minecraft.getMinecraft().textureManager.bindTexture(disabledButton)
        }
        Utils.drawTexturedRect(guiLeft.toFloat() + 114, (guiTop + ySize - 32).toFloat(), 88F, 20F,
            0F, 1F, 0F, 1F, GL11.GL_NEAREST)

        fontRendererObj.drawString("Add Held Item", guiLeft + 27, guiTop + ySize - 26, 4210752)
        fontRendererObj.drawString("Remove Item", guiLeft + 130, guiTop + ySize - 26, 4210752)

        GlStateManager.color(1f, 1f, 1f, 1f)

        // Add in some part of the gui for every row
        Minecraft.getMinecraft().textureManager.bindTexture(background)
        for (i in 0 until numOfRows) {
            Utils.drawTexturedRect(guiLeft.toFloat(), ((guiTop + 24) + (i * 18)).toFloat(), xSize.toFloat(), 18f,
                0f, 1f, 24 / 88f, 42 / 88f, GL11.GL_NEAREST)
        }

        var hoveredItem = ""
        var selectedPosition: Pair<Int, Int> = Pair(-999, -999)

        // Draw a slot for each item and the ItemStack
        for ((index, item) in NotEnoughDynamicLights.config.dynamicLightItems.entries.withIndex()) {
            val i = index % 9
            val j = index / 9
            GlStateManager.color(1f, 1f, 1f, 1f)

            Minecraft.getMinecraft().textureManager.bindTexture(chestGui)
            drawTexturedModalRect(guiLeft + 27 + i % 9 * 18, guiTop + 24 + j * 18, 7, 17, 18, 18)

            val itemStack = resolveItemStack(item.value)
            Utils.drawItemStack(itemStack, guiLeft + 28 + i % 9 * 18, guiTop + 25 + j * 18)

            if (mouseX >= guiLeft + 27 + i % 9 * 18 && mouseX <= guiLeft + 45 + i % 9 * 18) {
                if (mouseY >= guiTop + 24 + j * 18 && mouseY <= guiTop + 42 + j * 18) {
                    hoveredItem = item.key
                    val tooltip = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, false)
                    Utils.drawHoveringText(tooltip, mouseX, mouseY, width, height)
                }
            }

            if (itemSelected.isNotEmpty() && itemSelected == item.key) {
                // Save the position, so when we render the selected box its renders on top of everything
                selectedPosition = Pair(guiLeft + 24 + i % 9 * 18, guiTop + 21 + j * 18)
            }
        }

        stackToRender = hoveredItem

        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().textureManager.bindTexture(widgets)
        drawTexturedModalRect(selectedPosition.first, selectedPosition.second, 0, 22, 24, 24)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (didApplyMixin) {
            // Add Held Item button
            if (mouseX >= guiLeft + 15 &&
                mouseX <= guiLeft + 103 &&
                mouseY >= (guiTop + ySize - 32) &&
                mouseY <= (guiTop + ySize - 12)) {

                val heldItem = Minecraft.getMinecraft().thePlayer.heldItem

                if (heldItem == null) {
                    Utils.addChatMessage("§c[NEDL] You can't add your hand to the list of dynamic light items.")
                    return
                }

                val itemData = resolveItemData(heldItem).fillData()
                NotEnoughDynamicLights.config.dynamicLightItems[itemData.uuid] = itemData.configData
            }

            // Remove Item button
            if (mouseX >= guiLeft + 114 &&
                mouseX <= guiLeft + 202 &&
                mouseY >= guiTop + ySize - 32 &&
                mouseY <= guiTop + ySize - 12 &&
                itemSelected.isNotEmpty()) {
                NotEnoughDynamicLights.config.dynamicLightItems.remove(itemSelected)
                itemSelected = ""
            }

            if (stackToRender.isNotEmpty()) {
                itemSelected = stackToRender
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    companion object {
        @JvmStatic
        var didApplyMixin = false

        fun resolveItemStack(itemData: Config.ItemData): ItemStack {
            val tag = CompressedStreamTools.readCompressed(ByteArrayInputStream(Base64.getDecoder().decode(itemData.nbtData)))
            val itemStack = GameRegistry.makeItemStack(itemData.itemType, 0, 1, null)
            itemStack.tagCompound = tag.getCompoundTag("tag")
            if (itemStack.item.registryName.equals("minecraft:skull")) itemStack.itemDamage = 3

            return itemStack
        }

        @JvmStatic
        fun resolveItemData(itemStack: ItemStack): ItemData {
            var uuid = ""
            if (itemStack.hasTagCompound() && itemStack.tagCompound.getCompoundTag("ExtraAttributes") != null) {
                uuid = itemStack.tagCompound.getCompoundTag("ExtraAttributes").getString("uuid")
            }
            uuid = uuid.ifEmpty { itemStack.item.registryName }

            val outputStream = ByteArrayOutputStream()
            var nbtCompound = itemStack.serializeNBT()

            if (nbtCompound != null) {
                CompressedStreamTools.writeCompressed(nbtCompound, outputStream)
            }

            val compressedNBT = Base64.getEncoder().encodeToString(outputStream.toByteArray())

            return ItemData(uuid, itemStack.item.registryName, compressedNBT)
        }

        @JvmStatic
        fun findDynamicLightItems(itemStack: ItemStack?): Int {
            if (itemStack == null) return 0
            val uuid: String = resolveItemData(itemStack).uuid
            if (NotEnoughDynamicLights.config.dynamicLightItems.contains(uuid)) {
                return 15
            }
            return 0
        }
    }
}