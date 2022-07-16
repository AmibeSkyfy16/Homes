package ch.skyfy.homes.config

import ch.skyfy.jsonconfig.Defaultable
import ch.skyfy.jsonconfig.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class PlayersHomesConfig(var players: MutableList<Player>) : Validatable {

    override fun validate(errors: MutableList<String>) {
        players.forEach { it.validate(errors) }
        confirmValidate(errors)
    }

}

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault(): PlayersHomesConfig = PlayersHomesConfig(mutableListOf())
}