package dev.vixid.notenoughdynamiclights.mixin;

import dev.vixid.notenoughdynamiclights.DynamicLightItemsEditor;
import dev.vixid.notenoughdynamiclights.NotEnoughDynamicLights;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class NEDLMixinConfigPlugin implements IMixinConfigPlugin {

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    if ("dev.vixid.notenoughdynamiclights.mixin.MixinOFDynamicLights".equals(mixinClassName)) {
      try {
        Class.forName("io.github.moulberry.notenoughupdates.miscgui.DynamicLightItemsEditor");
        NotEnoughDynamicLights.Companion.setNeuHasFeature(true);
        return false;
      } catch (ClassNotFoundException e) {
        return true;
      }
    }
    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    if ("dev.vixid.notenoughdynamiclights.mixin.MixinOFDynamicLights".equals(mixinClassName)) {
      DynamicLightItemsEditor.setDidApplyMixin(true);
    }
  }
}
