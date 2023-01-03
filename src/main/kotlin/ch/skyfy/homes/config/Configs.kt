package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.jsonconfiglib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"), true)
    val GROUPS_PERMS = ConfigData.invoke<GroupsPerms, DefaultGroupsPerms>(HomesMod.CONFIG_DIRECTORY.resolve("GroupsPerms.json"), true)
}