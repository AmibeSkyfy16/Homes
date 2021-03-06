package ch.skyfy.homes

import ch.skyfy.homes.commands.*
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.utils.setupConfigDirectory
import ch.skyfy.jsonconfig.JsonConfig
import ch.skyfy.jsonconfig.JsonManager
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import net.minecraft.network.message.*;
import net.minecraft.text.Text.translatable

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

    override fun onInitializeServer() {
        registerCommands()

        ServerPlayConnectionEvents.JOIN.register{ handler, _, _ ->

            handler.player.sendMessage(translatable("chat.test"))

            if(Configs.PLAYERS_HOMES.data.players.stream().noneMatch { it.uuid ==  handler.player.uuidAsString}){
                Configs.PLAYERS_HOMES.data.players.add(Player(
                    uuid = handler.player.uuidAsString,
                    permsGroups = mutableSetOf("PLAYERS")
                ))
                JsonManager.save(Configs.PLAYERS_HOMES)
            }
        }
    }

    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            HomesCmd().register(dispatcher)
            ReloadConfig().register(dispatcher)
        }
    }

}