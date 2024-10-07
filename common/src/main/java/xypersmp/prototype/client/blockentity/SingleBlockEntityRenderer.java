package xypersmp.prototype.client.blockentity;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.mcgdx.impl.GdxBlockEntity;
import dev.ultreon.mcgdx.impl.GdxBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import xypersmp.prototype.client.Gdx3DRenderable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SingleBlockEntityRenderer extends GdxBlockEntityRenderer {
    private static final Map<ResourceKey<Block>, Supplier<Gdx3DRenderable>> RENDERABLE_MAP = new HashMap<>();

    public static void register(RegistrySupplier<Block> block, Supplier<Gdx3DRenderable> renderable) {
        RENDERABLE_MAP.put(block.getKey(), Suppliers.memoize(renderable::get));
    }

    public SingleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, source -> {});
    }

    @Override
    public void render(GdxBlockEntity holder) {
        holder.getBlockState().getBlock().arch$holder().unwrapKey().ifPresent(blockResourceKey -> {
            Gdx3DRenderable gdx3DRenderable = RENDERABLE_MAP.get(blockResourceKey).get();
            gdx3DRenderable.render(this);
        });
    }
}
