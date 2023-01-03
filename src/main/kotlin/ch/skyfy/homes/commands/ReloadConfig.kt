package ch.skyfy.homes.commands

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.config.Configs
import ch.skyfy.jsonconfiglib.ConfigManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class ReloadConfig : Command<ServerCommandSource> {

//    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
//        dispatcher.register(literal<ServerCommandSource?>("reloadConfig").executes(ReloadConfig()))
//    }

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val list = mutableListOf<Boolean>()
        list.add(ConfigManager.reloadConfig(Configs.PLAYERS_HOMES))
        list.add(ConfigManager.reloadConfig(Configs.GROUPS_PERMS))
        if(list.contains(false)){
            context.source.sendFeedback(Text.literal("Configuration could not be reloaded"), false)
            HomesMod.LOGGER.warn("Configuration could not be reloaded")
        }else {
            context.source.sendFeedback(Text.literal("The configuration was successfully reloaded"), false)
            HomesMod.LOGGER.info("The configuration was successfully reloaded")
        }
        return SINGLE_SUCCESS
    }

}