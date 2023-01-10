package ch.skyfy.homes

import ch.skyfy.homes.commands.HomesCmd
import ch.skyfy.homes.config.*
import ch.skyfy.homes.utils.setupConfigDirectory
import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.updateIterable
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
class HomesMod : DedicatedServerModInitializer {

    companion object {
        const val MOD_ID: String = "homes"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(HomesMod::class.java)
    }

    init {
        setupConfigDirectory()
        ConfigManager.loadConfigs(arrayOf(Configs.javaClass))
    }

    override fun onInitializeServer() {
        registerCommands()

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            Configs.PLAYERS_HOMES.updateIterable(PlayersHomesConfig::players) {
                if (it.none { player -> player.uuid == handler.player.uuidAsString })
                    it.add(Player(uuid = handler.player.uuidAsString, handler.player.name.string, "SHORT"))
            }
        }
    }

    private fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> HomesCmd().register(dispatcher) }

}