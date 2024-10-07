package xypersmp.prototype.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.JsonReader;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.ultreon.mcgdx.api.Gdx3DRenderSource;
import xypersmp.prototype.Prototype;
import xypersmp.prototype.client.blockentity.SingleBlockEntityRenderer;
import xypersmp.prototype.client.entity.LexusLc500Renderer;

public class PrototypeClient {
    public static final G3dModelLoader MODEL_LOADER = new G3dModelLoader(new JsonReader());

    public static void init() {
//        EntityRendererRegistry.register(Prototype.LEXUS_LC_500, LexusLc500Renderer::new);

        BlockEntityRendererRegistry.register(Prototype.SINGLE_BLOCK_ENTITY.get(), SingleBlockEntityRenderer::new);

        SingleBlockEntityRenderer.register(Prototype.BUSH_BLOCK, () -> new Gdx3DRenderable() {
            private final Model model = MODEL_LOADER.loadModel(Gdx.files.internal("assets/prototype/models/custom/bush.g3dj"));
            private final ModelInstance modelInstance = new ModelInstance(model);

            {
                for (Material material : model.materials) {
                    material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
                }
            }

            @Override
            public void render(Gdx3DRenderSource<?> source) {
                ModelBatch batch = source.getBatch();
                batch.render(modelInstance);
            }
        });
    }
}
