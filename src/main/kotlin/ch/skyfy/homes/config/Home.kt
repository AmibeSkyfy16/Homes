package ch.skyfy.homes.config

import ch.skyfy.jsonconfig.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    var x: Int,
    var y: Int,
    var z: Int,
    var pitch: Float,
    var yaw: Float,
    var name: String
) : Validatable {

    override fun validate(errors: MutableList<String>) {
    }
}
