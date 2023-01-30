package ch.skyfy.homes.api.config

import ch.skyfy.json5configlib.Defaultable
import ch.skyfy.json5configlib.Validatable
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.Serializable

@Serializable
data class RulesConfig(
    @SerialComment("A list of group rule. you can configure the home limit, the cooldown, etc.")
    val groupsRules: MutableList<GroupRules>
) : Validatable

@Serializable
data class GroupRules(
    @SerialComment("The name of the group")
    val name: String,
    @SerialComment("The rule config")
    val rule: Rule
) : Validatable

@Serializable
data class Rule(
    @SerialComment("The maximum number of homes")
    val maxHomes: Int = 3,
    @SerialComment("The number of seconds you have to wait before teleporting a new time")
    val cooldown: Int = 15,
    @SerialComment("The number of seconds to remain standing without moving more than 2 blocks before the teleportation is effective")
    val standStill: Int = 5,
    @SerialComment("Names of some extensions (Experience (teleporting cost XP), TheMoney (teleporting cost money), etc.)")
    val extensionName: String,
    @SerialComment("The name of a bonus group. Allow you to give some extra bonus (effect, items, xp) after a teleportation")
    val groupBonusName: String,
) : Validatable {
    override fun validateImpl(errors: MutableList<String>) {
        if (maxHomes < 0) errors.add("maxHome cannot have a negative value")

        if (cooldown < 0) errors.add("cooldown cannot have a negative value")

        if (standStill != 0)
            if (standStill < 0) errors.add("standStill cannot have a negative value")
    }
}

class DefaultRulesConfig : Defaultable<RulesConfig> {
    override fun getDefault() = RulesConfig(
        mutableListOf(
            GroupRules("SHORT", Rule(3, 10, 3, "NONE", "DEFAULT")),
            GroupRules("MEDIUM", Rule(4, 15, 5, "NONE", "DEFAULT")),
            GroupRules("LONG", Rule(5, 30, 5, "NONE", "DEFAULT")),
            GroupRules("BORING", Rule(6, 60, 5, "NONE", "DEFAULT")),
            )
    )

}