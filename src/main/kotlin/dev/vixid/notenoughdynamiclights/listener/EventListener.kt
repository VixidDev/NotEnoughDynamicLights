package dev.vixid.notenoughdynamiclights.listener

import dev.vixid.notenoughdynamiclights.NotEnoughDynamicLights
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class EventListener {

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        NotEnoughDynamicLights.saveConfig()
    }
}