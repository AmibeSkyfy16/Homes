package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.utils.hasPermission
import ch.skyfy.jsonconfiglib.updateIterableNested
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun deleteHome(
    playerEntity: PlayerEntity,
    permission: String,
    homeName: String
) {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return

    if (!hasPermission(player, permission)) {
        playerEntity.sendMessage(Text.literal("You don't have the permission to use this command").setStyle(Style.EMPTY.withColor(Formatting.RED)))
        return
    }

    // Check for home duplication
    Configs.PLAYERS_HOMES.updateIterableNested(Player::homes, player.homes) { homes ->
        if (homes.removeIf { it.name == homeName }) playerEntity.sendMessage(Text.literal("The home $homeName has been successfully removed").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
        else playerEntity.sendMessage(Text.literal("The home $homeName can not be removed because it does not exist").setStyle(Style.EMPTY.withColor(Formatting.RED)))
    }
}

class DeleteHome(override val permission: String) : Permission(permission) {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        deleteHome(context.source?.player ?: return SINGLE_SUCCESS, permission, getString(context, "homeName"))
        return 0
    }

}

class DeleteHomeForAnotherPlayer(override val permission: String) : Permission(permission) {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) deleteHome(targetPlayer, permission, getString(context, "homeName"))
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return 0
    }
}