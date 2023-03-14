package dev.vixid.notenoughdynamiclights.mixin;

import dev.vixid.notenoughdynamiclights.DynamicLightItemsEditor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.optifine.DynamicLights", remap = false)
public class MixinOFDynamicLights {

  @Inject(method = "getLightLevel(Lnet/minecraft/item/ItemStack;)I", at = @At("TAIL"), cancellable = true)
  private static void getLightLevel(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
    int lightLevel = DynamicLightItemsEditor.findDynamicLightItems(itemStack);
    if (lightLevel != 0) cir.setReturnValue(lightLevel);
  }
}
