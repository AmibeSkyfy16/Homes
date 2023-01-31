package ch.skyfy.homes.commands

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.api.config.Configs
import ch.skyfy.json5configlib.ConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class ReloadFiles : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val fileName = StringArgumentType.getString(context, "fileName")
        val list = mutableListOf<Boolean>()
        if (fileName == "ALL") {
            list.add(ConfigManager.reloadConfig(Configs.PLAYERS_HOMES))
            list.add(ConfigManager.reloadConfig(Configs.RULES_CONFIG))
            list.add(ConfigManager.reloadConfig(Configs.BONUS_CONFIG))
        } else {
            // Reflection will not work cause of inlined reified fun
            when (fileName) {
                "homes.json5" -> list.add(ConfigManager.reloadConfig(Configs.PLAYERS_HOMES))
                "rules-config.json5" -> list.add(ConfigManager.reloadConfig(Configs.RULES_CONFIG))
                "bonus-config.json5" -> list.add(ConfigManager.reloadConfig(Configs.BONUS_CONFIG))
            }
        }

        if (list.contains(false)) {
            context.source.sendFeedback(Text.literal("Configuration could not be reloaded"), false)
            HomesMod.LOGGER.warn("Configuration could not be reloaded")
        } else {
            context.source.sendFeedback(Text.literal("The configuration was successfully reloaded"), false)
            HomesMod.LOGGER.info("The configuration was successfully reloaded")
        }

        return SINGLE_SUCCESS
    }

}