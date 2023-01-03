package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class PlayersHomesConfig(var players: MutableSet<Player>) : Validatable {

    override fun validateImpl(errors: MutableList<String>) {
        players.forEach { it.validateImpl(errors) }
    }

}

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault(): PlayersHomesConfig = PlayersHomesConfig(mutableSetOf())
}