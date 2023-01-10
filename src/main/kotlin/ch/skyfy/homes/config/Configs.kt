package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.jsonconfiglib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"), true)
    val GROUP_RULES_CONFIG = ConfigData.invoke<RulesConfig, DefaultGroupRulesConfig>(HomesMod.CONFIG_DIRECTORY.resolve("group-rules-config.json"), true)
}