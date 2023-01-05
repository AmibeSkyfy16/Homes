package ch.skyfy.homes.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class PermissionsConfig(
    val players: MutableMap<String, PlayerPerms>,
    val group: MutableList<Group>
) : Validatable

@Serializable
data class PlayerPerms(
    val groups: MutableList<String>,
    val permissions: MutableList<Permission>
) : Validatable

@Serializable
data class Group(
    val id: String,
    val weight: Int,
    val parent: String? = null,
    val permissions: MutableList<Permission>
) : Validatable

@Serializable
data class Permission(
    val id: String,
    val value: Boolean
) : Validatable

class DefaultPermissionsConfig : Defaultable<PermissionsConfig> {
    override fun getDefault() = PermissionsConfig(
        mutableMapOf(),
        mutableListOf(
            Group("CHEATER", 2000, null, mutableListOf(Permission("homes.commands.*", false))),
            Group("ADMIN", 1000, null, mutableListOf(Permission("homes.commands.*", true))),
            Group(
                "DEFAULT", 10, null, mutableListOf(
                    Permission("homes.commands.homes.create", true),
                    Permission("homes.commands.homes.delete", true),
                    Permission("homes.commands.homes.teleport", true),
                    Permission("homes.commands.homes.list", true),
                )
            )
        )
    )
}