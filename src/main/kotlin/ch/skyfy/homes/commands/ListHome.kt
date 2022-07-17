package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.utils.hasPermission
import com.mojang.brigadier.Command
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
    requiredPerms: Perms
) {

    val player = Configs.PLAYERS_HOMES.data.players.find { playerEntity.uuidAsString == it.uuid } ?: return

    // Check for permission
    if (!hasPermission(player, requiredPerms)) {
        playerEntity.sendMessage(Text.literal("/homes delete <homeName> command required ${requiredPerms.name} permission"))
        return
    }

    player.homes.forEach {
        playerEntity.sendMessage(Text.literal("- ${it.name}").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)))
    }
}

class ListHome : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        listHome(context.source?.player ?: return SINGLE_SUCCESS, Perms.LIST_HOME)
        return 0
    }

}

class ListHomeForAnotherPlayer : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) listHome(targetPlayer, Perms.LIST_HOME_FOR_ANOTHER_PLAYER)
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return 0
    }
}