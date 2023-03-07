package dev.vixid.notenoughdynamiclights.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import java.awt.Color

/**
 * Taken from NEU
 *
 * https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/util/Utils.java
 */
class Utils {

    companion object {
        private var scrollY: LerpingFloat = LerpingFloat(0f, 100)

        @JvmStatic
        fun addChatMessage(message: String) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(message))
        }

        @JvmStatic
        fun drawItemStack(itemStack: ItemStack, x: Int, y: Int) {
            val itemRender: RenderItem = Minecraft.getMinecraft().renderItem
            RenderHelper.enableGUIStandardItemLighting()
            itemRender.zLevel = -145F
            itemRender.renderItemAndEffectIntoGUI(itemStack, x, y)
            itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, itemStack, x, y, null)
            itemRender.zLevel = 0F
            RenderHelper.disableStandardItemLighting()
        }

        @JvmStatic
        fun drawTexturedRect(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
            uMin: Float,
            uMax: Float,
            vMin: Float,
            vMax: Float,
            filter: Int
        ) {
            GlStateManager.enableTexture2D()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ONE_MINUS_SRC_ALPHA
            )
            GL14.glBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ONE_MINUS_SRC_ALPHA
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.worldRenderer
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
            worldrenderer
                .pos(x.toDouble(), (y + height).toDouble(), 0.0)
                .tex(uMin.toDouble(), vMax.toDouble()).endVertex()
            worldrenderer
                .pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
                .tex(uMax.toDouble(), vMax.toDouble()).endVertex()
            worldrenderer
                .pos((x + width).toDouble(), y.toDouble(), 0.0)
                .tex(uMax.toDouble(), vMin.toDouble()).endVertex()
            worldrenderer
                .pos(x.toDouble(), y.toDouble(), 0.0)
                .tex(uMin.toDouble(), vMin.toDouble()).endVertex()
            tessellator.draw()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GlStateManager.disableBlend()
        }

        @JvmStatic
        fun drawTexturedRect(x: Float, y: Float, width: Float, height: Float, filter: Int) {
            drawTexturedRect(x, y, width, height, 0F, 1F, 0F, 1F, filter)
        }

        @JvmStatic
        fun drawHoveringText(
            textIn: MutableList<String>,
            mouseX: Int,
            mouseY: Int,
            screenWidth: Int,
            screenHeight: Int
        ) {
            val font = Minecraft.getMinecraft().fontRendererObj
            var text: MutableList<String> = textIn

            if (text.isNotEmpty()) {
                GlStateManager.disableRescaleNormal()
                RenderHelper.disableStandardItemLighting()
                GlStateManager.disableLighting()
                GlStateManager.enableDepth()
                var tooltipTextWidth = 0

                for (line: String in text) {
                    val textLineWidth: Int = font.getStringWidth(line)

                    if (textLineWidth > tooltipTextWidth) {
                        tooltipTextWidth = textLineWidth
                    }
                }

                var needsWrap = false

                var titleLinesCount = 1
                var tooltipX: Int = mouseX + 12
                if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                    tooltipX = mouseX - 16 - tooltipTextWidth
                    if (tooltipX < 4) {
                        if (mouseX > screenWidth / 2) {
                            tooltipTextWidth = mouseX - 20
                        } else {
                            tooltipTextWidth = screenWidth - 16 - mouseX
                        }
                        needsWrap = true
                    }
                }

                if (needsWrap) {
                    var wrappedTooltipWidth = 0
                    val wrappedTextLines: MutableList<String> = mutableListOf()
                    for (i in 0 until text.size) {
                        val textLine: String = text[i]
                        val wrappedLine: MutableList<String> = font.listFormattedStringToWidth(textLine, tooltipTextWidth)
                        if (i == 0) titleLinesCount = wrappedLine.size

                        for (line: String in wrappedLine) {
                            val lineWidth: Int = font.getStringWidth(line)
                            if (lineWidth > wrappedTooltipWidth) {
                                wrappedTooltipWidth = lineWidth
                            }
                            wrappedTextLines.add(line)
                        }
                    }
                    tooltipTextWidth = wrappedTooltipWidth
                    text = wrappedTextLines

                    if (mouseX > screenWidth / 2) {
                        tooltipX = mouseX - 16 - tooltipTextWidth
                    } else {
                        tooltipX = mouseX + 12
                    }
                }

                var tooltipY: Int = mouseY - 12
                var tooltipHeight = 8

                if (text.size > 1) {
                    tooltipHeight += (text.size - 1) * 10
                    if (text.size > titleLinesCount) {
                        tooltipHeight += 2
                    }
                }

                // Scrollable tooltips
                if (tooltipHeight + 6 > screenHeight) {
                    if (scrollY.getTarget() < 0) {
                        scrollY.setTarget(0F)
                        scrollY.resetTimer()
                    } else if (screenHeight - tooltipHeight - 12 + scrollY.getTarget() > 0) {
                        scrollY.setTarget((-screenHeight + tooltipHeight + 12).toFloat())
                        scrollY.resetTimer()
                    }
                } else {
                    scrollY.setValue(0F)
                    scrollY.resetTimer()
                }
                scrollY.tick()

                if (tooltipY + tooltipHeight + 6 > screenHeight) {
                    tooltipY = (screenHeight - tooltipHeight - 6 + scrollY.getTarget()).toInt()
                }

                val zLevel = 300
                val backgroundColor = 4027580432.toInt()
                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 4,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    backgroundColor,
                    backgroundColor
                )
                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 4,
                    backgroundColor,
                    backgroundColor
                )
                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
                )
                drawGradientRect(
                    zLevel,
                    tooltipX - 4,
                    tooltipY - 3,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
                )
                drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 4,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
                )

                var borderColorStart = 1347420415
                if (text.size > 1) {
                    val first: String = text[0]
                    borderColorStart = getPrimaryColour(first).rgb and 0x00FFFFFF or (200 shl 24)
                }
                val borderColorEnd: Int = (borderColorStart and 0xFEFEFE) shr 1 or borderColorStart and -16777216

                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3 + 1,
                    tooltipX - 3 + 1,
                    tooltipY + tooltipHeight + 3 - 1,
                    borderColorStart,
                    borderColorEnd
                )
                drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + 2,
                    tooltipY - 3 + 1,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3 - 1,
                    borderColorStart,
                    borderColorEnd
                )
                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3 + 1,
                    borderColorStart,
                    borderColorStart
                )
                drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 2,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3,
                    borderColorEnd,
                    borderColorEnd
                )

                GlStateManager.disableDepth()
                for (lineNumber in 0 until text.size) {
                    val line: String = text[lineNumber]
                    font.drawStringWithShadow(line, tooltipX.toFloat(), tooltipY.toFloat(), -1)

                    if (lineNumber + 1 == titleLinesCount) {
                        tooltipY += 2
                    }

                    tooltipY += 10
                }

                GlStateManager.enableLighting()
                GlStateManager.enableDepth()
                RenderHelper.enableStandardItemLighting()
                GlStateManager.enableRescaleNormal()
            }
            GlStateManager.disableLighting()
        }

        fun getPrimaryColourCode(displayName: String): Char {
            var lastColourCode = -99
            var currentColour = 0
            val mostCommon = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (i in displayName.indices) {
                val c = displayName[i]
                if (c == '\u00A7') {
                    lastColourCode = i
                } else if (lastColourCode == i - 1) {
                    val colIndex = "0123456789abcdef".indexOf(c)
                    currentColour = if (colIndex >= 0) {
                        colIndex
                    } else {
                        0
                    }
                } else if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c) >= 0) {
                    if (currentColour > 0) {
                        mostCommon[currentColour]++
                    }
                }
            }
            var mostCommonCount = 0
            for (index in mostCommon.indices) {
                if (mostCommon[index] > mostCommonCount) {
                    mostCommonCount = mostCommon[index]
                    currentColour = index
                }
            }
            return "0123456789abcdef"[currentColour]
        }

        fun getPrimaryColour(displayName: String): Color {
            val colourInt = Minecraft.getMinecraft().fontRendererObj.getColorCode(getPrimaryColourCode(displayName))
            return Color(colourInt).darker()
        }
    }
}