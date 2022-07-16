package ch.skyfy.homes.config

import ch.skyfy.jsonconfig.Defaultable
import ch.skyfy.jsonconfig.Validatable

@kotlinx.serialization.Serializable
data class GroupsPerms(
    val groups: MutableMap<String, MutableSet<Perms>>
) : Validatable

class DefaultGroupsPerms : Defaultable<GroupsPerms>{
    override fun getDefault() = GroupsPerms(
        mutableMapOf(
            "PLAYERS" to mutableSetOf(Perms.CREATE_HOME, Perms.DELETE_HOME, Perms.TELEPORT_HOME, Perms.LIST_HOME),
            "ADMINS" to mutableSetOf(Perms.ALL)
        )
    )

}