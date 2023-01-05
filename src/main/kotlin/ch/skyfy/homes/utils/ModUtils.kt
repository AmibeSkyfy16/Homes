package ch.skyfy.homes.utils

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Group
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.config.Rule
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

fun setupConfigDirectory() {
    try {
        if (!HomesMod.CONFIG_DIRECTORY.exists()) HomesMod.CONFIG_DIRECTORY.createDirectory()
    } catch (e: java.lang.Exception) {
        HomesMod.LOGGER.fatal("An exception occurred. Could not create the root folder that should contain the configuration files")
        throw RuntimeException(e)
    }
}

/**
 * return a player id with the name of the player followed by '#' and her uuid
 */
fun getPlayerId(player: Player) = player.name + "#" + player.uuid


/**
 * Check if a player has the permission
 *
 * A player will belong to one or more groups containing permissions.
 * It can also have permissions that are directly assigned to it without going through a group.
 * These are given priority in the permissions check
 *
 *
 */
fun hasPermission(player: Player, permission: String): Boolean {

    val playersMap = Configs.PERMISSION_CONFIG.serializableData.players

    val groupsAsId = playersMap.filter { it.key == getPlayerId(player) }.values.flatMap { it.groups }.toMutableList()
    val groups = getGroupsOfPlayer(groupsAsId)

    // Checking directly assigned permission (priority)
    playersMap.filter { it.key == getPlayerId(player) }.values.flatMap { it.permissions }.forEach {

        if (it.id == permission) {
            println("found a non-group permission, value is: ${it.value}")
            return it.value
        }

        if (it.id.contains('*') && permission.contains(it.id.substringBeforeLast('*').substringBeforeLast('.'))) {
            println("found a non-group permission #2, value is: ${it.value}")
            return it.value
        }
    }

    groups.forEach { group -> return recurseGroup(group, permission) }

    return false
}

private fun recurseGroup(group: Group, permission: String) : Boolean {
//    val perm = group.permissions.find { it.id == permission || (it.id.contains('*') && permission.contains(it.id.substringBeforeLast('*').substringBeforeLast('.'))) }
//    if (perm != null) return perm.value

    return group.permissions.find { it.id == permission || (it.id.contains('*') && permission.contains(it.id.substringBeforeLast('*').substringBeforeLast('.'))) }?.value
        ?: if(group.parent != null) recurseGroup(getGroupsOfPlayer(mutableListOf(group.parent)).first(), permission) else false

//    return if(group.parent != null) recurseGroup(getGroupsOfPlayer(mutableListOf(group.parent)).first(), permission) else false
}

fun getGroupsOfPlayer(groupsNameList: MutableList<String>): List<Group> = Configs.PERMISSION_CONFIG.serializableData.group.filter { groupsNameList.contains(it.id) }.sortedBy { it.weight }
//    val list = Configs.PERMISSION_CONFIG.serializableData.group.filter { groupsNameList.contains(it.id) }
//    list.sortedBy { it.weight }
//    return list


fun getGroupRules(player: Player): Rule? = Configs.GROUP_RULES_CONFIG.serializableData.groupsRules.firstOrNull() { it.name == player.groupRuleName }?.rule
