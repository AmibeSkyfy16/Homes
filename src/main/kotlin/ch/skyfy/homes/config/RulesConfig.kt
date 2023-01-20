package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class RulesConfig(
    val groupsRules: MutableList<GroupRules>
) : Validatable

@Serializable
data class GroupRules(
    val name: String,
    val rule: Rule
) : Validatable

@Serializable
data class Rule(
    val maxHomes: Int = 3,
    val cooldown: Int = 15,
    val standStill: Int = 5,
    val featureName: String
) : Validatable {
    override fun validateImpl(errors: MutableList<String>) {
        if (maxHomes < 0) errors.add("maxHome cannot have a negative value")

        if (cooldown < 0) errors.add("cooldown cannot have a negative value")

        if (standStill != 0)
            if (standStill < 0) errors.add("standStill cannot have a negative value")
    }
}

class DefaultGroupRulesConfig : Defaultable<RulesConfig> {
    override fun getDefault() = RulesConfig(
        mutableListOf(
            GroupRules("SHORT", Rule(3, 10, 3, "Experience")),
            GroupRules("MEDIUM", Rule(4, 15, 5, "Experience")),
            GroupRules("LONG", Rule(5, 30, 5, "Experience")),
            GroupRules("BORING", Rule(6, 60, 5, "Experience")),
            )
    )

}