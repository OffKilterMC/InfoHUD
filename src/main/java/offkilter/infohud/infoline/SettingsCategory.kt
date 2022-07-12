package offkilter.infohud.infoline

import net.minecraft.resources.ResourceLocation

enum class SettingsCategory(val iconResource: ResourceLocation) {
    LOCATION(ResourceLocation("infohud:textures/gui/compass.png")),
    PERF(ResourceLocation("infohud:textures/gui/stopwatch.png")),
    BLOCK(ResourceLocation("infohud:textures/gui/block.png")),
    LIGHT(ResourceLocation("infohud:textures/gui/light.png")),
    WORLD(ResourceLocation("infohud:textures/gui/world.png")),
    GAMEPLAY(ResourceLocation("infohud:textures/gui/sword.png"));
}
