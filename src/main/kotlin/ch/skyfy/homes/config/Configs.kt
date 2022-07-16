package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.jsonconfig.JsonData

object Configs {
    val PLAYERS_HOMES = JsonData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"))
    val GROUPS_PERMS = JsonData.invoke<GroupsPerms, DefaultGroupsPerms>(HomesMod.CONFIG_DIRECTORY.resolve("GroupsPerms.json"))
}