package xypersmp.prototype.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import dev.ultreon.mcgdx.impl.GdxEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import xypersmp.prototype.Prototype;
import xypersmp.prototype.client.PrototypeClient;
import xypersmp.prototype.entity.LexusLc500;

public class LexusLc500Renderer extends GdxEntityRenderer<LexusLc500> {
    private Model model;

    public LexusLc500Renderer(EntityRendererProvider.Context context) {
        super(context, gdx3DRenderSource -> {});

        G3dModelLoader modelLoader = PrototypeClient.MODEL_LOADER;
        model = modelLoader.loadModel(Gdx.files.internal("assets/prototype/models/entity/2020_lexus_lc_500.g3dj"));
    }

    @Override
    public void render(LexusLc500 entity) {
        super.render(entity);

        ModelInstance instance;
        if (entity.userData instanceof ModelInstance modelInstance) {
            instance = modelInstance;
        } else {
            instance = new ModelInstance(model);
            entity.userData = instance;
        }

        instance.transform.setToTranslationAndScaling(-15.5f / 2f, 0, -136 / 2f, 0.5f, 0.5f, 0.5f);
        for (Material material : instance.materials) {
            material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        getBatch().render(instance);
    }
}
