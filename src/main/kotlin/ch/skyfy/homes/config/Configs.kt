package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.jsonconfiglib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"), true)
    val COMMANDS_AND_PERMISSIONS_LIST = ConfigData.invoke<CommandsAndPermissionsInformation, DefaultCommandsAndPermissionsInformation>(HomesMod.CONFIG_DIRECTORY.resolve("commands-permissions-information.json"), true)
    val PERMISSION_CONFIG = ConfigData.invoke<PermissionsConfig, DefaultPermissionsConfig>(HomesMod.CONFIG_DIRECTORY.resolve("permission-config.json"), true)
    val GROUP_RULES_CONFIG = ConfigData.invoke<RulesConfig, DefaultGroupRulesConfig>(HomesMod.CONFIG_DIRECTORY.resolve("group-rules-config.json"), true)
}