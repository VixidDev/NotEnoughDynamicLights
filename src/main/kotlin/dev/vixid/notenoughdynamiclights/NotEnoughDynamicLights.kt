package dev.vixid.notenoughdynamiclights

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.vixid.notenoughdynamiclights.commands.DynamicLightItemsCommand
import dev.vixid.notenoughdynamiclights.listener.EventListener
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.*
import java.nio.charset.StandardCharsets

@Mod(modid = NotEnoughDynamicLights.MOD_ID,
     name = NotEnoughDynamicLights.MOD_NAME,
     version = NotEnoughDynamicLights.MOD_VERSION,
     clientSideOnly = true)
class NotEnoughDynamicLights {

    companion object {
        const val MOD_ID = "notenoughdynamiclights"
        const val MOD_NAME = "NotEnoughDynamicLights"
        const val MOD_VERSION = "1.0.0"

        var foundNEU = false

        var openGui: GuiScreen? = null
        var gson: Gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()

        var config: Config = Config()
        lateinit var nedlConfigFile: File

        fun saveConfig() {
            try {
                BufferedWriter(OutputStreamWriter(FileOutputStream(nedlConfigFile), StandardCharsets.UTF_8)).use {
                    it.write(gson.toJson(config))
                }
            } catch (ignored: Exception) {
            }
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val nedlDir = File(event.modConfigurationDirectory, "notenoughdynamiclights")
        nedlDir.mkdirs()

        nedlConfigFile = File(nedlDir, "config.json")

        if (nedlConfigFile.exists()) {
            try {
                BufferedReader(InputStreamReader(FileInputStream(nedlConfigFile), StandardCharsets.UTF_8)).use {
                    config = gson.fromJson(it, Config::class.java)
                }
            } catch (e: Exception) {
                RuntimeException("[NEDL] Invalid Config! Config will be reset :(").printStackTrace()
            }
        }

        saveConfig()

        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(EventListener())

        ClientCommandHandler.instance.registerCommand(DynamicLightItemsCommand())
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        if (Loader.isModLoaded("notenoughupdates")) {
            foundNEU = true
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (Minecraft.getMinecraft().thePlayer == null) {
            openGui = null
            return
        }

        if (openGui != null) {
            if (Minecraft.getMinecraft().thePlayer.openContainer != null) {
                Minecraft.getMinecraft().thePlayer.closeScreen()
            }
            Minecraft.getMinecraft().displayGuiScreen(openGui)
            openGui = null
        }
    }
}