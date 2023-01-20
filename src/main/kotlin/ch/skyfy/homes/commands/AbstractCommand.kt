package ch.skyfy.homes.commands

import ch.skyfy.homes.api.Feature
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.utils.getGroupRules
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

abstract class AbstractCommand : Command<ServerCommandSource> {

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { context.source?.player!!.uuidAsString == it.uuid } ?: return 0
        val rule = getGroupRules(player) ?: return 0

        when(this){
            is TeleportHome -> Feature.getFeature(rule.featureName)?.teleportHomeImpl(context)
        }

        return runImpl(context)
    }

    abstract fun runImpl(context: CommandContext<ServerCommandSource>): Int
}