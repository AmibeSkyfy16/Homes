package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.utils.getConditionalBasedObject
import ch.skyfy.homes.utils.getGroupRules
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

abstract class AbstractCommand : Command<ServerCommandSource> {

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { context.source?.player!!.uuidAsString == it.uuid } ?: return 0
        val rule = getGroupRules(player) ?: return 0
        val conditionalFeature = getConditionalBasedObject(rule.conditionalFeature)
        if (conditionalFeature?.impl(this::class, context) == false) return 0
        return runImpl(context)
    }

    abstract fun runImpl(context: CommandContext<ServerCommandSource>) : Int
}