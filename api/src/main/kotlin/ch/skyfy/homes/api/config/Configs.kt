package ch.skyfy.homes.api.config

import ch.skyfy.homes.api.HomesAPIMod
import ch.skyfy.json5configlib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesAPIMod.CONFIG_DIRECTORY.resolve("homes.json5"), true)
    val RULES_CONFIG = ConfigData.invoke<RulesConfig, DefaultRulesConfig>(HomesAPIMod.CONFIG_DIRECTORY.resolve("rules-config.json5"), true)
    val BONUS_CONFIG = ConfigData.invoke<BonusConfig, DefaultBonusConfig>(HomesAPIMod.CONFIG_DIRECTORY.resolve("bonus-config.json5"), true)
}
