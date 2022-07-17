@file:Suppress("UNUSED_PARAMETER")

package ch.skyfy.homes.commands

import ch.skyfy.homes.HomesMod
import ch.skyfy.homes.callbacks.EntityMoveCallback
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.utils.hasPermission
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.*
import net.minecraft.entity.Entity
import net.minecraft.entity.MovementType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec3d
import java.lang.System.currentTimeMillis
import kotlin.coroutines.CoroutineContext

open class TeleportHomeImpl(override val coroutineContext: CoroutineContext = Dispatchers.Default) : CoroutineScope {

    private val teleporting: MutableMap<String, Pair<CoroutineScope, Vec3d>> = mutableMapOf()

    private val cooldowns: MutableMap<String, Long> = mutableMapOf()

    init {
        EntityMoveCallback.EVENT.register(::onPlayerMove)
    }

    private fun onPlayerMove(entity: Entity, movementType: MovementType, movement: Vec3d): ActionResult {
        if (entity is ServerPlayerEntity) {
            if (teleporting.containsKey(entity.uuidAsString)) {
                val value = teleporting[entity.uuidAsString]!!
                if (isDistanceGreaterThan(value.second, entity.pos, 2)) {
                    value.first.cancel()
                    teleporting.remove(entity.uuidAsString)
                    HomesMod.LOGGER.info("CANCELLED TELEPORT")
                    entity.sendMessage(Text.literal("You moved ! teleportation cancelled !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                    return ActionResult.PASS
                }
            }
        }
        return ActionResult.PASS
    }

    fun teleportHome(spe: ServerPlayerEntity, homeName: String, requiredPerms: Perms) {

        val player = Configs.PLAYERS_HOMES.data.players.find { spe.uuidAsString == it.uuid } ?: return

        // Check for permission
        if (!hasPermission(player, requiredPerms)) {
            spe.sendMessage(Text.literal("/homes teleport <homeName> command required ${requiredPerms.name} permission"))
            return
        }

        val home = player.homes.find { it.name == homeName }
        if (home == null) {
            spe.sendMessage(Text.literal("Home $homeName does not exist !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
            return
        }

        launch {

            if (teleporting.containsKey(spe.uuidAsString)) {
                spe.sendMessage(Text.literal("A teleportation is already in progress").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                return@launch
            }

            val startTime = cooldowns[spe.uuidAsString]

            if (startTime != null) {
                val elapsed = (currentTimeMillis() - startTime) / 1000L
                if (elapsed < player.cooldown) {
                    spe.sendMessage(Text.literal("You have to wait ${player.cooldown - elapsed} seconds before next tp").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                    return@launch
                }
            }

            teleporting.putIfAbsent(spe.uuidAsString, Pair(this@launch, Vec3d(spe.pos.x, spe.pos.y, spe.pos.z)))

            repeat(player.standStill) { second ->
                spe.sendMessage(Text.literal("${player.standStill - second} seconds left before teleporting").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), true)
                delay(1000L)
            }

            cooldowns[spe.uuidAsString] = currentTimeMillis()
            teleporting.remove(spe.uuidAsString)

            spe.server.execute {
                spe.teleport(spe.world as ServerWorld?, home.x, home.y, home.z, home.yaw, home.pitch)
                spe.sendMessage(Text.literal("You've arrived at your destination ($homeName)").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
            }
        }
    }

    private fun isDistanceGreaterThan(startPos: Vec3d, nowPos: Vec3d, greaterThan: Int): Boolean {
        return (startPos.x.coerceAtLeast(nowPos.x) - startPos.x.coerceAtMost(nowPos.x) > greaterThan) ||
                (startPos.y.coerceAtLeast(nowPos.y) - startPos.y.coerceAtMost(nowPos.y) > greaterThan) ||
                (startPos.z.coerceAtLeast(nowPos.z) - startPos.z.coerceAtMost(nowPos.z) > greaterThan)
    }

}

class TeleportHome : TeleportHomeImpl(), Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        teleportHome(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"), Perms.TELEPORT_HOME)
        return 0
    }

}

class TeleportHomeToAnotherPlayer : TeleportHomeImpl(), Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val targetPlayerName = getString(context, "playerName")
        val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
        if (targetPlayer != null) teleportHome(targetPlayer, getString(context, "homeName"), Perms.TELEPORT_HOME_TO_ANOTHER_PLAYER)
        else context.source?.sendFeedback(Text.literal("Player not found"), false)
        return 0
    }
}