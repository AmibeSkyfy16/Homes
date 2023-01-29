package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.extension.DefaultExperienceExtensionConfig
import ch.skyfy.homes.extension.ExperienceExtensionConfig
import ch.skyfy.json5configlib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json5"), true)
    val GROUP_RULES_CONFIG = ConfigData.invoke<RulesConfig, DefaultGroupRulesConfig>(HomesMod.CONFIG_DIRECTORY.resolve("group-rules-config.json5"), true)

    val EXPERIENCE_FEATURE_CONFIG = ConfigData.invoke<ExperienceExtensionConfig, DefaultExperienceExtensionConfig>(HomesMod.CONFIG_DIRECTORY.resolve("experience-extension-config.json5"), true)
}
