package ch.skyfy.homes.api.config

import ch.skyfy.json5configlib.Defaultable
import ch.skyfy.json5configlib.Validatable
import kotlinx.serialization.Serializable
import io.github.xn32.json5k.SerialComment

@Serializable
data class PlayersHomesConfig(
    @SerialComment("Will contain the list of players, with their name, the name of the group they are associated with and the list of their homes")
    var players: MutableSet<Player>
) : Validatable {
    override fun validateImpl(errors: MutableList<String>) {
        players.forEach { it.validateImpl(errors) }
    }
}

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

@Serializable
data class Location (val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float, val dimension: String) : Validatable

@Serializable
data class Home(
    var name: String,
    var location: Location
) : Validatable

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault(): PlayersHomesConfig = PlayersHomesConfig(mutableSetOf())
}