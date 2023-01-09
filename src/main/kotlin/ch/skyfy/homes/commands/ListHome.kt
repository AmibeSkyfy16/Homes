package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun listHome(
    playerEntity: PlayerEntity,
    permission: String
) {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return

//    if (!hasPermission(player, permission)) {
//        playerEntity.sendMessage(Text.literal("You don't have the permission to use this command").setStyle(Style.EMPTY.withColor(Formatting.RED)))
//        return
//    }

    player.homes.forEach {
        playerEntity.sendMessage(Text.literal("- ${it.name}").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)))
    }
}

class ListHome(override val permission: String) : Permission(permission) {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        listHome(context.source?.player ?: return SINGLE_SUCCESS, permission)
        return 0
    }

}

class ListHomeForAnotherPlayer(override val permission: String) : Permission(permission) {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) listHome(targetPlayer, permission)
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return 0
    }
}