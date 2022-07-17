package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.utils.hasPermission
import ch.skyfy.jsonconfig.JsonManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class DeleteHome {

//    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
//        val deleteHome =
//            literal("homes")
//                .then(
//                    literal("player").then(
//                        argument("playerName", StringArgumentType.string()).suggests { _, suggestionBuilder ->
//                            // NOT WORKS
//                            CommandSource.suggestMatching(arrayOf("one", "two"), suggestionBuilder)
//                        }.then(
//                            literal("delete").then(
//                                argument("homeName", StringArgumentType.string()).executes(DeleteHomeForAnotherPlayer())
//                            )
//                        )
//                    )
//                )
//                .then(
//                    literal("delete").then(
//                        argument("homeName", StringArgumentType.string()).executes(DeleteHome())
//                    )
//                )
//        dispatcher.register(deleteHome)
//    }

    fun deleteHome(
        playerEntity: PlayerEntity,
        homeName: String,
        requiredPerms: Perms
    ) {

        val player = Configs.PLAYERS_HOMES.data.players.find { playerEntity.uuidAsString == it.uuid } ?: return

        // Check for permission
        if (!hasPermission(player, requiredPerms)) {
            playerEntity.sendMessage(Text.literal("/homes delete <homeName> command required ${requiredPerms.name} permission"))
            return
        }

        // Check for home duplication
        if(player.homes.removeIf{it.name == homeName}){
            playerEntity.sendMessage(Text.literal("The home $homeName has been successfully removed").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
        }else{
            playerEntity.sendMessage(Text.literal("The home $homeName can not be removed because it does not exist").setStyle(Style.EMPTY.withColor(Formatting.RED)))
        }

        JsonManager.save(Configs.PLAYERS_HOMES)
    }

    inner class DeleteHome : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            deleteHome(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"), Perms.DELETE_HOME)
            return 0
        }

    }

    inner class DeleteHomeForAnotherPlayer : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            val targetPlayerName = getString(context, "playerName")
            val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
            if (targetPlayer != null) deleteHome(targetPlayer, getString(context, "homeName"), Perms.CREATE_HOME_FOR_ANOTHER_PLAYER)
            else context.source?.sendFeedback(Text.literal("Player not found"), false)
            return 0
        }
    }

}