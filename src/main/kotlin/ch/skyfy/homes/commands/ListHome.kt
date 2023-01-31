package ch.skyfy.homes.commands

import ch.skyfy.homes.api.config.Configs
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.util.Formatting

fun listHome(
    playerEntity: PlayerEntity
): Int {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return 0

    player.homes.forEach {
        playerEntity.sendMessage(LiteralText("- ${it.name}").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)), false)
    }

    return 0
}

class ListHome : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        return listHome(context.source?.player ?: return SINGLE_SUCCESS)
    }

}

class ListHomeForAnotherPlayer : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) listHome(targetPlayer)
        else context.source?.sendFeedback(LiteralText("Player not found"), false)
        return 0
    }
}