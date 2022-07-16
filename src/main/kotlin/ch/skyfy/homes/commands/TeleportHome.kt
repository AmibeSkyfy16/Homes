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
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TeleportHome(override val coroutineContext: CoroutineContext = Dispatchers.Default) : CoroutineScope {

    data class Tp(val serverPlayerEntity: ServerPlayerEntity, val coroutineScope: CoroutineScope, val startPos: Vec3d)

    private val teleporting: MutableSet<Tp> = mutableSetOf()

    private val lastTeleports: MutableMap<String, Long> = mutableMapOf()

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
            teleporting.find { it.serverPlayerEntity == entity }?.let {
                if (isDistanceGreaterThan(it.startPos, entity.pos, 2)) {
                    entity.sendMessage(Text.literal("You moved ! teleportation cancelled !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                    it.coroutineScope.cancel()
                    teleporting.remove(it)
                }
            }
        }
        return ActionResult.PASS
    }

    private fun teleportHome(serverPlayerEntity: ServerPlayerEntity, homeName: String, requiredPerms: Perms) {

        val player = Configs.PLAYERS_HOMES.data.players.find { serverPlayerEntity.uuidAsString == it.uuid } ?: return

        // Check for permission
        if (!hasPermission(player, requiredPerms)) {
            serverPlayerEntity.sendMessage(Text.literal("/homes teleport <homeName> command required ${requiredPerms.name} permission"))
            return
        }

        player.homes.find { it.name == homeName }.let {
            if (it == null) {
                serverPlayerEntity.sendMessage(Text.literal("Home $homeName does not exist !").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                return
            }

            launch {
                if (teleporting.stream().noneMatch { tp -> tp.serverPlayerEntity == serverPlayerEntity }) {
                    lastTeleports[serverPlayerEntity.uuidAsString]?.let {value ->
                        if(TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - value)) <= player.cooldown){
                            serverPlayerEntity.sendMessage(Text.literal("You have to wait before another tp"))
                            return@launch
                        }
                    }

                    val tp = Tp(serverPlayerEntity, this, serverPlayerEntity.pos)
                    teleporting.add(tp)
                    delay(player.standStill.toDuration(DurationUnit.SECONDS))
                    lastTeleports[serverPlayerEntity.uuidAsString] = System.currentTimeMillis()
                    teleporting.remove(tp)
                }

                serverPlayerEntity.server.execute {
                    serverPlayerEntity.teleport(serverPlayerEntity.world as ServerWorld?, it.x, it.y, it.z, it.pitch, it.yaw)
                }
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