package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.features.DefaultExperienceFeatureConfig
import ch.skyfy.homes.features.ExperienceFeatureConfig
import ch.skyfy.jsonconfiglib.ConfigData

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"), true)
    val GROUP_RULES_CONFIG = ConfigData.invoke<RulesConfig, DefaultGroupRulesConfig>(HomesMod.CONFIG_DIRECTORY.resolve("group-rules-config.json"), true)

    val EXPERIENCE_FEATURE_CONFIG = ConfigData.invoke<ExperienceFeatureConfig, DefaultExperienceFeatureConfig>(HomesMod.CONFIG_DIRECTORY.resolve("experience-feature-config.json"), true)
}
