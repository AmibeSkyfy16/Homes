package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Home
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.utils.hasPermission
import ch.skyfy.jsonconfig.JsonManager
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType.getFloat
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

open class CreateHome {

//    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
//        val createHome =
//            literal("homes")
//                .then(
//                    literal("player").then(
//                        argument("playerName", StringArgumentType.string()).suggests { _, suggestionBuilder ->
//                            // NOT WORKS
//                            CommandSource.suggestMatching(arrayOf("one", "two"), suggestionBuilder)
//                        }.then(
//                            literal("create").then(
//                                argument("homeName", StringArgumentType.string()).executes(CreateHomeForAnotherPlayer()).then(
//                                    argument("x", DoubleArgumentType.doubleArg()).then(
//                                        argument("y", DoubleArgumentType.doubleArg()).then(
//                                            argument("z", DoubleArgumentType.doubleArg()).then(
//                                                argument("yaw", FloatArgumentType.floatArg()).then(
//                                                    argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeForAnotherPlayerWithCoordinates())
//                                                )
//                                            )
//                                        )
//                                    )
//                                )
//                            )
//                        )
//                    )
//                )
//                .then(
//                    literal("create").then(
//                        argument("homeName", StringArgumentType.string()).executes(CreateHome()).then(
//                            argument("x", DoubleArgumentType.doubleArg()).then(
//                                argument("y", DoubleArgumentType.doubleArg()).then(
//                                    argument("z", DoubleArgumentType.doubleArg()).then(
//                                        argument("yaw", FloatArgumentType.floatArg()).then(
//                                            argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeWithCoordinates())
//                                        )
//                                    )
//                                )
//                            )
//                        )
//                    )
//                )
//        dispatcher.register(createHome)
//    }

    fun addHomeToPlayer(
        playerEntity: PlayerEntity,
        homeName: String,
        requiredPerms: Perms,
        x: Double = playerEntity.x,
        y: Double = playerEntity.y,
        z: Double = playerEntity.z,
        pitch: Float = playerEntity.pitch,
        yaw: Float = playerEntity.yaw
    ) {

        val player = Configs.PLAYERS_HOMES.data.players.find { playerEntity.uuidAsString == it.uuid } ?: return

        // Check for permission
        if (!hasPermission(player, requiredPerms)) {
            playerEntity.sendMessage(Text.literal("/homes create <homeName> command required ${requiredPerms.name} permission"))
            return
        }

        // Check for home duplication
        player.homes.find { homeName == it.name }?.let {
            playerEntity.sendMessage(Text.literal("You have already a home named $homeName"))
            return
        }

        // Check for maxHomes rule
        if (player.homes.size + 1 > player.maxHomes) {
            playerEntity.sendMessage(Text.literal("You cant have more than ${player.maxHomes}"))
            return
        }

        player.homes.add(Home(x, y, z, pitch, yaw, homeName))

        JsonManager.save(Configs.PLAYERS_HOMES)

        playerEntity.sendMessage(Text.literal("New home added"))
    }

     inner class CreateHome : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            addHomeToPlayer(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"), Perms.CREATE_HOME)
            return 0
        }

    }

    inner class CreateHomeWithCoordinates : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            addHomeToPlayer(
                playerEntity = context.source?.player ?: return SINGLE_SUCCESS,
                homeName = getString(context, "homeName"),
                requiredPerms = Perms.CREATE_HOME_WITH_COORDINATES,
                x = getDouble(context, "x"),
                y = getDouble(context, "y"),
                z = getDouble(context, "z"),
                pitch = getFloat(context, "pitch"),
                yaw = getFloat(context, "yaw")
            )
            return 0
        }
    }

     inner class CreateHomeForAnotherPlayer : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            val targetPlayerName = getString(context, "playerName")
            val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
            if (targetPlayer != null) addHomeToPlayer(targetPlayer, getString(context, "homeName"), Perms.CREATE_HOME_FOR_ANOTHER_PLAYER)
            else context.source?.sendFeedback(Text.literal("Player not found"), false)
            return 0
        }
    }

     inner class CreateHomeForAnotherPlayerWithCoordinates : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            val targetPlayerName = getString(context, "playerName")
            val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
            if (targetPlayer != null)
                addHomeToPlayer(
                    playerEntity = targetPlayer,
                    homeName = getString(context, "homeName"),
                    requiredPerms = Perms.CREATE_HOME_FOR_ANOTHER_PLAYER_WITH_COORDINATES,
                    x = getDouble(context, "x"),
                    y = getDouble(context, "y"),
                    z = getDouble(context, "z"),
                    pitch = getFloat(context, "pitch"),
                    yaw = getFloat(context, "yaw"),
                )
            else context.source?.sendFeedback(Text.literal("Player not found"), false)

            return 0
        }
    }
}