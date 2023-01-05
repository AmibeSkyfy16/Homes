package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable


@kotlinx.serialization.Serializable
data class CommandsAndPermissionsInformation(val map: MutableMap<String, String>) : Validatable

class DefaultCommandsAndPermissionsInformation : Defaultable<CommandsAndPermissionsInformation> {
    override fun getDefault() = CommandsAndPermissionsInformation(mutableMapOf())
}