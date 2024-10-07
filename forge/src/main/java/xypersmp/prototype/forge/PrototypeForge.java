package xypersmp.prototype.forge;

import xypersmp.prototype.Prototype;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Prototype.MOD_ID)
public final class PrototypeForge {
    public PrototypeForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Prototype.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        Prototype.init();
    }
}
