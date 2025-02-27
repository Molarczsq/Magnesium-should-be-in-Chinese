package me.jellysquid.mods.sodium.client.render.pipeline;

import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatBiomeColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.SmoothBiomeColorBlender;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.MinecraftClient;

public class ChunkRenderCache {
    protected BiomeColorBlender createBiomeColorBlender() {
        return Minecraft.getInstance().gameSettings.biomeBlendRadius <= 0 ? new FlatBiomeColorBlender() : new SmoothBiomeColorBlender();
    }
}
