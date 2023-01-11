package ch.skyfy.homes.utils

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.config.Rule
import ch.skyfy.homes.features.ConditionalFeature
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

fun getConditionalBasedObject(name: String): ConditionalFeature? {
    try {
        return Class.forName(name).getDeclaredField("INSTANCE").get(null) as ConditionalFeature
    } catch (_: Exception) { }
    return null
}

fun getGroupRules(player: Player): Rule? = Configs.GROUP_RULES_CONFIG.serializableData.groupsRules.firstOrNull { it.name == player.groupRuleName }?.rule
