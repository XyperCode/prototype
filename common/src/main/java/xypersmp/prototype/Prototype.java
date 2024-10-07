package xypersmp.prototype;

import com.badlogic.gdx.math.Octree;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.mcgdx.impl.GdxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import xypersmp.prototype.block.GdxEntityBlock;
import xypersmp.prototype.client.PrototypeClient;

import java.util.function.Supplier;

public final class Prototype {
    public static final String MOD_ID = "prototype";
    public static final RegistrarManager REGISTRAR_MANAGER = RegistrarManager.get(MOD_ID);

    public static final Registrar<EntityType<?>> ENTITY_TYPE_REG = REGISTRAR_MANAGER.get(Registries.ENTITY_TYPE);
    public static final Registrar<Block> BLOCK_REG = REGISTRAR_MANAGER.get(Registries.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REG = REGISTRAR_MANAGER.get(Registries.BLOCK_ENTITY_TYPE);
    public static final Registrar<Item> ITEM_REG = REGISTRAR_MANAGER.get(Registries.ITEM);

//    public static final RegistrySupplier<EntityType<LexusLc500>> LEXUS_LC_500 = ENTITY_TYPE_REG.register(id("2020_lexus_lc_500"), () -> EntityType.Builder.of(LexusLc500::new, MobCategory.AMBIENT).build("2020_lexus_lc_500"));
    public static final RegistrySupplier<Block> BUSH_BLOCK = BLOCK_REG.register(id("bush"), () -> new GdxEntityBlock(Prototype.getSingleBlockEntity(), BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS).dropsLike(Blocks.GRASS_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<GdxBlockEntity>> SINGLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPE_REG.register(id("single"), () -> BlockEntityType.Builder.of(Prototype::create, BUSH_BLOCK.get()).build(null));

    public static void init() {
        // Write common init code here.

        EnvExecutor.runInEnv(Env.CLIENT, () -> PrototypeClient::init);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static GdxBlockEntity create(BlockPos blockPos, BlockState blockState) {
        return new GdxBlockEntity(SINGLE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    private static Supplier<BlockEntityType<GdxBlockEntity>> getSingleBlockEntity() {
        return () -> SINGLE_BLOCK_ENTITY.get();
    }
}
