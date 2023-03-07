package dev.vixid.notenoughdynamiclights

import com.google.gson.annotations.Expose

class Config {

    @Expose
    var dynamicLightItems: MutableMap<String, ItemData> = hashMapOf()

    class ItemData {
        @Expose
        var itemType: String = ""
        @Expose
        var nbtData: String = ""
    }
}