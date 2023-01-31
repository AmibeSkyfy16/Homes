package ch.skyfy.homes.commands

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.api.config.Configs
import ch.skyfy.json5configlib.ConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText

class ReloadConfig : Command<ServerCommandSource> {

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val list = mutableListOf<Boolean>()
        list.add(ConfigManager.reloadConfig(Configs.PLAYERS_HOMES))
        list.add(ConfigManager.reloadConfig(Configs.RULES_CONFIG))
        list.add(ConfigManager.reloadConfig(Configs.BONUS_CONFIG))
        if (list.contains(false)) {
            context.source.sendFeedback(LiteralText("Configuration could not be reloaded"), false)
            HomesMod.LOGGER.warn("Configuration could not be reloaded")
        } else {
            context.source.sendFeedback(LiteralText("The configuration was successfully reloaded"), false)
            HomesMod.LOGGER.info("The configuration was successfully reloaded")
        }
        return SINGLE_SUCCESS
    }

}