package ch.skyfy.homes.utils

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.config.Player
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

fun hasPermission(player: Player, perms: Perms): Boolean {
    var has = false
    player.permsGroups.forEach { group ->
        Configs.GROUPS_PERMS.serializableData.groups[group]?.let { permsList ->
            has = permsList.contains(perms) || permsList.contains(Perms.ALL)
        }
    }
    return has
}