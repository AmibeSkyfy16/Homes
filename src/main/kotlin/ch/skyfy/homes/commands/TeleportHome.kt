@file:Suppress("UNUSED_PARAMETER")

package ch.skyfy.homes.commands

import ch.skyfy.homes.callbacks.EntityMoveCallback
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Perms
import ch.skyfy.homes.utils.hasPermission
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.*
import net.minecraft.entity.Entity
import net.minecraft.entity.MovementType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
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

class TeleportHome(override val coroutineContext: CoroutineContext = Dispatchers.Default) : CoroutineScope {

    private val teleporting: MutableSet<Triple<ServerPlayerEntity, MutableList<CoroutineScope>, Vec3d>> = mutableSetOf()

    private val cooldowns: MutableMap<String, Long> = mutableMapOf()

    init {
        EntityMoveCallback.EVENT.register(this::onPlayerMove)
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        val teleportHome =
            literal("homes")
                .then(
                    literal("player").then(
                        argument("playerName", StringArgumentType.string()).then(
                            literal("teleport").then(
                                argument("homeName", StringArgumentType.string()).executes(TeleportHomeToAnotherPlayer())
                            )
                        )
                    )
                ).then(
                    literal("teleport").then(
                        argument("homeName", StringArgumentType.string()).executes(TeleportHome())
                    )
                )
        dispatcher.register(teleportHome)
    }

    private fun onPlayerMove(entity: Entity, movementType: MovementType, movement: Vec3d): ActionResult {
        if (entity is ServerPlayerEntity) {
            teleporting.find { triple -> triple.first === entity }?.let {
                if (isDistanceGreaterThan(it.third, entity.pos, 2)) {
                    entity.sendMessage(Text.literal("You moved ! teleportation cancelled !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                    it.second.first().cancel()
                    teleporting.remove(it)
                }
            }
        }
        return ActionResult.PASS
    }

    private fun teleportHome(spe: ServerPlayerEntity, homeName: String, requiredPerms: Perms) {

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

            val t = teleporting.find { it.first === spe }

            if (t != null) {
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

            val triple = Triple(spe, mutableListOf(this), Vec3d(spe.pos.x, spe.pos.y, spe.pos.z))
            teleporting.add(triple)

            runBlocking {
//                triple.second.add(this)
                repeat(player.standStill) {
                    spe.sendMessage(Text.literal("$it seconds left before teleporting").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), true)
                    delay(1000L)
                }
            }

            cooldowns[spe.uuidAsString] = currentTimeMillis()
            teleporting.remove(triple)

            spe.server.execute {
                spe.teleport(spe.world as ServerWorld?, home.x, home.y, home.z, home.pitch, home.yaw)
                spe.sendMessage(Text.literal("You've arrived at your destination ($homeName)").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
            }
        }
    }

    private fun isDistanceGreaterThan(startPos: Vec3d, nowPos: Vec3d, greaterThan: Int): Boolean {
        return (startPos.x.coerceAtLeast(nowPos.x) - startPos.x.coerceAtMost(nowPos.x) > greaterThan) ||
                (startPos.y.coerceAtLeast(nowPos.y) - startPos.y.coerceAtMost(nowPos.y) > greaterThan) ||
                (startPos.z.coerceAtLeast(nowPos.z) - startPos.z.coerceAtMost(nowPos.z) > greaterThan)
    }

    inner class TeleportHome : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            teleportHome(context.source?.player ?: return SINGLE_SUCCESS, getString(context, "homeName"), Perms.TELEPORT_HOME)
            return 0
        }

    }

    inner class TeleportHomeToAnotherPlayer : Command<ServerCommandSource> {
        override fun run(context: CommandContext<ServerCommandSource>): Int {
            val targetPlayerName = getString(context, "playerName")
            val targetPlayer = context.source?.server?.playerManager?.getPlayer(targetPlayerName)
            if (targetPlayer != null) teleportHome(targetPlayer, getString(context, "homeName"), Perms.TELEPORT_HOME_TO_ANOTHER_PLAYER)
            else context.source?.sendFeedback(Text.literal("Player not found"), false)
            return 0
        }
    }

}