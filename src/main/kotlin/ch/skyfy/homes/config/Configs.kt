package ch.skyfy.homes.config

import ch.skyfy.homes.HomesMod
import ch.skyfy.jsonconfig.JsonData
import java.nio.file.Paths

object Configs {
    val PLAYERS_HOMES = JsonData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(HomesMod.CONFIG_DIRECTORY.resolve("homes.json"))
}