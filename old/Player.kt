package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    var uuid: String,
    var name: String,
    var groupRuleName: String,
    var homes: MutableSet<Home> = mutableSetOf()
) : Validatable {
    override fun validateImpl(errors: MutableList<String>) {
        homes.forEach { it.validateImpl(errors) }

        // TODO check in mojang database if this uuid is a real and premium minecraft account
    }
}
