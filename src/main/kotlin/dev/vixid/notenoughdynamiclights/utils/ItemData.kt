package dev.vixid.notenoughdynamiclights.utils

import dev.vixid.notenoughdynamiclights.Config

class ItemData(
    var uuid: String,
    var itemType: String,
    var nbtData: String
) {
    var configData: Config.ItemData = Config.ItemData()

    fun fillData(): ItemData {
        configData.itemType = itemType
        configData.nbtData = nbtData
        return this
    }
}