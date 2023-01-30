package ch.skyfy.homes

import ch.skyfy.homes.api.Extension
import ch.skyfy.homes.api.config.Configs
import ch.skyfy.homes.api.config.Player
import ch.skyfy.homes.api.config.PlayersHomesConfig
import ch.skyfy.homes.commands.HomesCmd
import ch.skyfy.homes.extension.ExperienceExtension
import ch.skyfy.homes.features.BonusFeature
import ch.skyfy.json5configlib.ConfigManager
import ch.skyfy.json5configlib.updateIterable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
class HomesMod : ModInitializer {

    companion object {
        const val MOD_ID: String = "homes"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(HomesMod::class.java)
    }

    init {
        ConfigManager.loadConfigs(arrayOf(ExperienceExtension.javaClass))
    }

    override fun onInitialize() {
        registerCommands()

        Extension.registerExtension("Experience", ExperienceExtension::class)

        BonusFeature

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            Configs.PLAYERS_HOMES.updateIterable(PlayersHomesConfig::players) {
                if (it.none { player -> player.uuid == handler.player.uuidAsString })
                    it.add(Player(uuid = handler.player.uuidAsString, handler.player.name.string, "SHORT"))
            }
        }
    }

    private fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> HomesCmd().register(dispatcher) }

}