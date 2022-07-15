package offkilter.infohud.infoline

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome

class BiomeInfoLine :
    InfoLineBase("biome", SettingsCategory.WORLD) {
    override fun getInfoString(env: InfoLineEnvironment): String? {
        return if (env.blockPos.y >= env.level.minBuildHeight && env.blockPos.y < env.level.maxBuildHeight) {
            val holder = env.level.getBiome(env.blockPos)
            "Biome: " + holder.unwrap().map({ resourceKey: ResourceKey<Biome> ->
                resourceKey.location().toString()
            }) { biome: Biome -> "[unregistered $biome]" }
        } else {
            null
        }
    }
}
