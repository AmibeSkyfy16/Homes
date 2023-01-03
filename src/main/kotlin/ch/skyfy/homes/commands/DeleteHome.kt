package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.utils.hasPermission
import ch.skyfy.jsonconfiglib.updateIterableNested
import com.mojang.brigadier.Command
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
    homeName: String,
    requiredPerms: Perms
) {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return

    // Check for permission
    if (!hasPermission(player, requiredPerms)) {
        playerEntity.sendMessage(Text.literal("/homes delete <homeName> command required ${requiredPerms.name} permission"))
        return
    }

    // Check for home duplication
    Configs.PLAYERS_HOMES.updateIterableNested(Player::homes, player.homes){
        if(it.removeIf{it.name == homeName})
            playerEntity.sendMessage(Text.literal("The home $homeName has been successfully removed").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
        else
            playerEntity.sendMessage(Text.literal("The home $homeName can not be removed because it does not exist").setStyle(Style.EMPTY.withColor(Formatting.RED)))
    }
}

class DeleteHome : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        deleteHome(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"), Perms.DELETE_HOME)
        return 0
    }

}

class DeleteHomeForAnotherPlayer : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) deleteHome(targetPlayer, getString(context, "homeName"), Perms.CREATE_HOME_FOR_ANOTHER_PLAYER)
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return 0
    }
}