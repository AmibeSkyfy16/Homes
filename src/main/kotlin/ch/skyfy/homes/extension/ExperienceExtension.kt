@file:Suppress("unused")

package ch.skyfy.homes.extension

import ch.skyfy.homes.api.Extension
import ch.skyfy.homes.config.Configs
import ch.skyfy.json5configlib.Defaultable
import ch.skyfy.json5configlib.Validatable
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import kotlinx.serialization.Serializable
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.max
import kotlin.math.min

/**
 * When player use teleport command, it will cost some XP, more the distance is high between player position and his home,
 * more the experience cost will be high
 */
object ExperienceExtension : Extension() {

    override fun teleportHome(context: CommandContext<ServerCommandSource>): Boolean {
        val spe = context.source.player ?: return true
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { spe.uuidAsString == it.uuid } ?: return true

        val homeName = StringArgumentType.getString(context, "homeName")

        val playerX = spe.x
        val playerY = spe.y
        val playerZ = spe.z

        val home = player.homes.find { it.name == homeName } ?: return true

        val greaterDistance = listOf(
            (max(playerX, home.x) - min(playerX, home.x)).toInt(),
            (max(playerY, home.y) - min(playerY, home.y)).toInt(),
            (max(playerZ, home.z) - min(playerZ, home.z)).toInt(),
        ).max()

        val costs = Configs.EXPERIENCE_FEATURE_CONFIG.serializableData.experienceCosts
        val cost = costs.map {
            val min = it.first.first
            val max = it.second.first
            if (greaterDistance in min until max) {
                return@map (it.second.second * greaterDistance) / max
            }
            return@map null
        }.filterNotNull().firstOrNull()

        if (cost != null) {
            return if (spe.experienceLevel < cost) {
                spe.sendMessage(Text.literal("You need at least $cost experience level to be able to teleport to your home").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                false
            } else {
                spe.sendMessage(Text.literal("The cost for the teleportation is $cost experience level"))
                spe.setExperienceLevel(spe.experienceLevel - cost)
                true
            }
        }
        return false
    }

}

@Serializable
data class ExperienceFeatureConfig(
    val experienceCosts: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>
) : Validatable

class DefaultExperienceFeatureConfig : Defaultable<ExperienceFeatureConfig> {
    override fun getDefault(): ExperienceFeatureConfig = ExperienceFeatureConfig(
        listOf(
            Pair(
                Pair(0, 0),
                Pair(500, 5)
            ),
            Pair(
                Pair(500, 5),
                Pair(1000, 10)
            ),
            Pair(
                Pair(1000, 10),
                Pair(2000, 15)
            ),
            Pair(
                Pair(2000, 15),
                Pair(8000, 30)
            ),
            Pair(
                Pair(10000, 30),
                Pair(1_000_000_000, 100)
            )
        )
    )
}