package ch.skyfy.homes.commands

import ch.skyfy.homes.api.Extension
import ch.skyfy.homes.api.config.Configs
import ch.skyfy.homes.api.utils.getRule
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

abstract class AbstractCommand : Command<ServerCommandSource> {

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { context.source?.player!!.uuidAsString == it.uuid } ?: return 0
        val rule = getRule(player) ?: return 0

        when(this){
            is CreateHome -> Extension.getExtension(rule.extensionName)?.createHome(context)
            is DeleteHome -> Extension.getExtension(rule.extensionName)?.deleteHome(context)
            is ListHome -> Extension.getExtension(rule.extensionName)?.listHome(context)
            is TeleportHome -> Extension.getExtension(rule.extensionName)?.teleportHome(context)
        }

        return runImpl(context)
    }

    abstract fun runImpl(context: CommandContext<ServerCommandSource>): Int
}