package ch.skyfy.homes.commands

import ch.skyfy.homes.api.config.Configs
import ch.skyfy.homes.api.config.Player
import ch.skyfy.json5configlib.updateIterableNested
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.util.Formatting

fun deleteHome(
    playerEntity: PlayerEntity,
    homeName: String
): Int {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return 0

    Configs.PLAYERS_HOMES.updateIterableNested(Player::homes, player.homes) { homes ->
        if (homes.removeIf { it.name == homeName }) playerEntity.sendMessage(LiteralText("The home $homeName has been successfully removed").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false)
        else playerEntity.sendMessage(LiteralText("The home $homeName can not be removed because it does not exist").setStyle(Style.EMPTY.withColor(Formatting.RED)), false)
    }
    return 0
}

class DeleteHome : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        return deleteHome(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"))
    }
}

class DeleteHomeForAnotherPlayer : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) deleteHome(targetPlayer, getString(context, "homeName"))
        else context.source?.sendFeedback(LiteralText("Player not found"), false)
        return 0
    }
}