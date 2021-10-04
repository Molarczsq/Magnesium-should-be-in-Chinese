package me.jellysquid.mods.phosphor.mixin.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import me.jellysquid.mods.phosphor.common.chunk.level.LevelPropagatorExtended;
import me.jellysquid.mods.phosphor.common.chunk.level.LevelUpdateListener;
import me.jellysquid.mods.phosphor.common.chunk.light.LevelPropagatorAccess;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
//import net.minecraft.world.chunk.light.LevelPropagator;
import net.minecraft.world.lighting.LevelBasedGraph;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelBasedGraph.class)
public abstract class MixinLevelPropagator implements LevelPropagatorExtended, LevelUpdateListener, LevelPropagatorAccess {
    @Shadow
    @Final
    private Long2ByteMap propagationLevels;

    @Shadow
    protected abstract int getLevel(long id);

    @Shadow
    @Final
    private int levelCount;

    @Shadow
    protected abstract int getEdgeLevel(long sourceId, long targetId, int level);

    @Shadow
    protected abstract void propagateLevel(long sourceId, long id, int level, int currentLevel, int pendingLevel, boolean decrease);

    @Shadow
    private volatile boolean needsUpdate;

    @Shadow
    private int minLevelToUpdate;

    @Override
    @Invoker("propagateLevel")
    public abstract void invokePropagateLevel(long sourceId, long targetId, int level, boolean decrease);

    @Override
    public void checkForUpdates() {
        this.needsUpdate = this.minLevelToUpdate < this.levelCount;
    }

    // [VanillaCopy] LevelPropagator#propagateLevel(long, long, int, boolean)
    @Override
    public void propagateLevel(long sourceId, BlockState sourceState, long targetId, int level, boolean decrease) {
        int pendingLevel = this.propagationLevels.get(targetId) & 0xFF;

        int propagatedLevel = this.getPropagatedLevel(sourceId, sourceState, targetId, level);
        int clampedLevel = MathHelper.clamp(propagatedLevel, 0, this.levelCount - 1);

        if (decrease) {
            this.propagateLevel(sourceId, targetId, clampedLevel, this.getLevel(targetId), pendingLevel, true);

            return;
        }

        boolean flag;
        int resultLevel;

        if (pendingLevel == 0xFF) {
            flag = true;
            resultLevel = MathHelper.clamp(this.getLevel(targetId), 0, this.levelCount - 1);
        } else {
            resultLevel = pendingLevel;
            flag = false;
        }

        if (clampedLevel == resultLevel) {
            this.propagateLevel(sourceId, targetId, this.levelCount - 1, flag ? resultLevel : this.getLevel(targetId), pendingLevel, false);
        }
    }

    @Override
    public int getPropagatedLevel(long sourceId, BlockState sourceState, long targetId, int level) {
        return this.getEdgeLevel(sourceId, targetId, level);
    }

    @Redirect(method = { "removeToUpdate(JIIZ)V", "processUpdates" }, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ByteMap;remove(J)B", remap = false))
    private byte redirectRemovePendingUpdate(Long2ByteMap map, long key) {
        byte ret = map.remove(key);

        if (ret != map.defaultReturnValue()) {
            this.onPendingUpdateRemoved(key);
        }

        return ret;
    }

    @Redirect(method = "addToUpdate", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ByteMap;put(JB)B", remap = false))
    private byte redirectAddPendingUpdate(Long2ByteMap map, long key, byte value) {
        byte ret = map.put(key, value);

        if (ret == map.defaultReturnValue()) {
            this.onPendingUpdateAdded(key);
        }

        return ret;
    }

    @Override
    public void onPendingUpdateAdded(long key) {
        // NO-OP
    }

    @Override
    public void onPendingUpdateRemoved(long key) {
        // NO-OP
    }
}
