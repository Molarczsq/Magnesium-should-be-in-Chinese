package me.jellysquid.mods.sodium.client.gl.attribute;

//import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormat;

public interface BufferVertexFormat {
    static BufferVertexFormat from(VertexFormat format) {
        return (BufferVertexFormat) format;
    }

    int getStride();
}
