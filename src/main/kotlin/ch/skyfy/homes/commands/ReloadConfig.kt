package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.jsonconfig.JsonConfig
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

class ReloadConfig : Command<ServerCommandSource> {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        dispatcher.register(literal<ServerCommandSource?>("reloadConfig").executes(ReloadConfig()))
    }

    override fun run(context: CommandContext<ServerCommandSource>?): Int {
        JsonConfig.reloadConfig(Configs.PLAYERS_HOMES)
        JsonConfig.reloadConfig(Configs.GROUPS_PERMS)
        return SINGLE_SUCCESS
    }

}