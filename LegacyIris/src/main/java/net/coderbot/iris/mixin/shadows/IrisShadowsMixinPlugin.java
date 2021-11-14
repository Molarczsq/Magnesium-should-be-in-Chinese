package net.coderbot.iris.mixin.shadows;

//import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class IrisShadowsMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return mixinClassName.contains("PreventRebuildNearInShadowPass") == !(FMLLoader.getLoadingModList().getModFileById("magnesium") != null); //(Forge doesn't like this for some reason, not sure why.)
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
	}

}
