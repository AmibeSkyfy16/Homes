package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    var uuid: String,
    var maxHomes: Int = 3,
    var cooldown: Int = 15,
    var standStill: Int = 5,
    var homes: MutableSet<Home> = mutableSetOf(),
    var permsGroups: MutableSet<String>
) : Validatable {

    override fun validateImpl(errors: MutableList<String>) {
        homes.forEach { it.validateImpl(errors) }

        // TODO check in mojang database if this uuid is a real and premium minecraft account

        if (maxHomes < 0) errors.add("maxHome cannot have a negative value")

        if (cooldown < 0) errors.add("cooldown cannot have a negative value")

        if (standStill != 0)
            if (standStill < 0) errors.add("standStill cannot have a negative value")
    }
}
