package ch.skyfy.homes

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.utils.setupConfigDirectory
import ch.skyfy.jsonconfig.JsonConfig
import ch.skyfy.jsonconfig.JsonManager
import kotlinx.serialization.json.Json
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

@Suppress("MemberVisibilityCanBePrivate")
class HomesMod : DedicatedServerModInitializer {

    companion object {
        const val MOD_ID: String = "homes"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val LOGGER: Logger = LogManager.getLogger(HomesMod::class.java)
    }

    init {
        setupConfigDirectory()
        JsonConfig.loadConfigs(arrayOf(Configs.javaClass))
    }

    override fun onInitializeServer() {}

}