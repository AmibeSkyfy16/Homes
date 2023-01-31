package ch.skyfy.homes.api

import ch.skyfy.homes.api.config.Configs
import ch.skyfy.homes.api.events.PlayerTeleportationEvents
import ch.skyfy.homes.api.events.TeleportationDone
import ch.skyfy.homes.api.utils.setupConfigDirectory
import ch.skyfy.homes.api.utils.setupExtensionDirectory
import ch.skyfy.json5configlib.ConfigManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path

class HomesAPIMod : ModInitializer {

    companion object {
        const val MOD_ID: String = "homes_api"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve("homes")
        val EXTENSION_DIRECTORY: Path = CONFIG_DIRECTORY.resolve("extensions")
        val LOGGER: Logger = LogManager.getLogger(HomesAPIMod::class.java)
    }

    init {
        setupConfigDirectory()
        setupExtensionDirectory()
        ConfigManager.loadConfigs(arrayOf(Configs.javaClass))
    }

    override fun onInitialize() {}
}