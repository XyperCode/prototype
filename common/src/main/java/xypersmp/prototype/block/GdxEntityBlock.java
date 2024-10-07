package xypersmp.prototype.block;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.mcgdx.impl.GdxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GdxEntityBlock extends Block implements EntityBlock {
    private final Supplier<BlockEntityType<GdxBlockEntity>> type;

    public GdxEntityBlock(Supplier<BlockEntityType<GdxBlockEntity>> type, Properties properties) {
        super(properties);
        this.type = Suppliers.memoize(type::get);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return type.get().create(blockPos, blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
