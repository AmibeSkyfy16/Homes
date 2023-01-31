package ch.skyfy.homes.api.config

import ch.skyfy.json5configlib.Defaultable
import ch.skyfy.json5configlib.Validatable
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.Serializable

@Serializable
data class BonusConfig(
    @SerialComment("A list of group bonus. by default, player will be member of group bonus called DEFAULT")
    val groupBonus: MutableList<GroupBonus>
) : Validatable

@Serializable
data class GroupBonus(
    @SerialComment("The name of the group")
    val name: String,
    @SerialComment("The bonus config")
    val bonus: Bonus
) : Validatable

@Serializable
data class Bonus(
    @SerialComment("A list of bonus based on specific events (TELEPORTATION_DONE, TELEPORTATION_STANDSTILL_STARTED, TELEPORTATION_CANCELLED)")
    val map: Map<String, List<BonusEffect>>
) : Validatable

@Serializable
data class BonusEffect(
    @SerialComment("The name of the effect, like minecraft:resistance")
    val name: String,
    @SerialComment("The duration of the effect in seconds")
    val duration: Int,
    @SerialComment("The amplifier of the effect")
    val amplifier: Int
)

class DefaultBonusConfig : Defaultable<BonusConfig> {
    override fun getDefault(): BonusConfig {
        return BonusConfig(mutableListOf(
            GroupBonus("DEFAULT", Bonus(mutableMapOf(
                "TELEPORTATION_DONE" to listOf(
                    BonusEffect("minecraft:resistance", 10, 4)
                ),
                "TELEPORTATION_CANCELLED" to listOf(
                    BonusEffect("minecraft:slowness", 5, 1)
                )
            )))
        ))
    }
}