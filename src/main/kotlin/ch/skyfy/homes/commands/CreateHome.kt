package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Home
import ch.skyfy.homes.config.Player
import ch.skyfy.homes.utils.getGroupRules
import ch.skyfy.jsonconfiglib.updateIterableNested
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import com.mojang.brigadier.arguments.FloatArgumentType.getFloat
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun addHomeToPlayer(
    playerEntity: PlayerEntity,
    homeName: String,
    x: Double = playerEntity.x,
    y: Double = playerEntity.y,
    z: Double = playerEntity.z,
    pitch: Float = playerEntity.pitch,
    yaw: Float = playerEntity.yaw
) : Int {

    val player = Configs.PLAYERS_HOMES.serializableData.players.find { playerEntity.uuidAsString == it.uuid } ?: return 0
    val rule = getGroupRules(player) ?: return 0

    // Check for home duplication
    player.homes.find { homeName == it.name }?.let {
        playerEntity.sendMessage(Text.literal("You already have a home named $homeName").setStyle(Style.EMPTY.withColor(Formatting.RED)))
        return 0
    }

    // Check for maxHomes rule
    if (player.homes.size + 1 > rule.maxHomes) {
        playerEntity.sendMessage(Text.literal("You can't have more than ${rule.maxHomes} homes").setStyle(Style.EMPTY.withColor(Formatting.RED)))
        return 0
    }

    Configs.PLAYERS_HOMES.updateIterableNested(Player::homes, player.homes) { it.add(Home(x, y, z, pitch, yaw, homeName)) }

    playerEntity.sendMessage(Text.literal("The home of name «$homeName»  at coordinate ${String.format("%.2f", x)} ${String.format("%.2f", y)} ${String.format("%.2f", z)} has been added").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
    return 0
}

class CreateHome : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        return addHomeToPlayer(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"))
    }
}

class CreateHomeWithCoordinates : AbstractCommand()  {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        return addHomeToPlayer(
            playerEntity = context.source?.player ?: return SINGLE_SUCCESS,
            homeName = getString(context, "homeName"),
            x = getDouble(context, "x"),
            y = getDouble(context, "y"),
            z = getDouble(context, "z"),
            pitch = getFloat(context, "pitch"),
            yaw = getFloat(context, "yaw"),
        )
    }
}

class CreateHomeForAnotherPlayer : AbstractCommand() {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) addHomeToPlayer(targetPlayer, getString(context, "homeName"))
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return SINGLE_SUCCESS
    }
}

class CreateHomeForAnotherPlayerWithCoordinates : AbstractCommand()  {
    override fun runImpl(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null)
            addHomeToPlayer(
                playerEntity = targetPlayer,
                homeName = getString(context, "homeName"),
                x = getDouble(context, "x"),
                y = getDouble(context, "y"),
                z = getDouble(context, "z"),
                pitch = getFloat(context, "pitch"),
                yaw = getFloat(context, "yaw"),
            )
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return SINGLE_SUCCESS
    }
}