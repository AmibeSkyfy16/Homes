package ch.skyfy.homes.commands

import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Player
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource.suggestMatching
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

class HomesCmd {

    private fun <S : ServerCommandSource> homesListForAnotherPlayer(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val targetPlayerName = StringArgumentType.getString(commandContext, "playerName")
        val spe = commandContext.source.server.playerManager.getPlayer(targetPlayerName) ?: return suggestionsBuilder.buildFuture()
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { it.uuid == spe.uuidAsString} ?: return suggestionsBuilder.buildFuture()
        return homesListImpl(suggestionsBuilder, player)
    }

    private fun <S : ServerCommandSource> homesList(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val spe = commandContext.source.player ?: return suggestionsBuilder.buildFuture()
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { it.uuid == spe.uuidAsString} ?: return suggestionsBuilder.buildFuture()
        return homesListImpl(suggestionsBuilder, player)
    }

    private fun homesListImpl(suggestionsBuilder: SuggestionsBuilder?, player: Player) : CompletableFuture<Suggestions>{
        return suggestMatching(player.homes.map { it.name }, suggestionsBuilder)
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val veryBigCommand = literal("homes")
            .then(
                literal("player").then(
                    argument("playerName", StringArgumentType.string()).suggests { context, suggestionBuilder ->
                        EntityArgumentType.players().listSuggestions(context, suggestionBuilder)
                    }.then( // /homes player <playerName> create
                        literal("create").then(
                            argument("homeName", StringArgumentType.string()).executes(CreateHomeForAnotherPlayer()).then(
                                argument("x", DoubleArgumentType.doubleArg()).then(
                                    argument("y", DoubleArgumentType.doubleArg()).then(
                                        argument("z", DoubleArgumentType.doubleArg()).then(
                                            argument("yaw", FloatArgumentType.floatArg()).then(
                                                argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeForAnotherPlayerWithCoordinates())
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).then( // /homes player <playerName> delete
                        literal("delete").then(
                            argument("homeName", StringArgumentType.string()).suggests(this::homesListForAnotherPlayer).executes(DeleteHomeForAnotherPlayer())
                        )
                    ).then( // /homes player <playerName> teleport
                        literal("teleport").then(
                            argument("homeName", StringArgumentType.string()).suggests(this::homesListForAnotherPlayer).executes(TeleportHomeToAnotherPlayer())
                        )
                    ).then( // /homes player <playerName> list
                        literal("list").executes(ListHomeForAnotherPlayer())
                    )
                )
            ).then( // /homes create
                literal("create").then(
                    argument("homeName", StringArgumentType.string()).executes(CreateHome()).then(
                        argument("x", DoubleArgumentType.doubleArg()).then(
                            argument("y", DoubleArgumentType.doubleArg()).then(
                                argument("z", DoubleArgumentType.doubleArg()).then(
                                    argument("yaw", FloatArgumentType.floatArg()).then(
                                        argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeWithCoordinates())
                                    )
                                )
                            )
                        )
                    )
                )
            ).then( // /homes delete
                literal("delete").then(
                    argument("homeName", StringArgumentType.string()).suggests(this::homesList).executes(DeleteHome())
                )
            ).then( // /homes teleport
                literal("teleport").then(
                    argument("homeName", StringArgumentType.string()).suggests(this::homesList).executes(TeleportHome())
                )
            ).then( // /homes list
                literal("list").executes(ListHome())
            ).then( // /homes reloadConfigs
                literal("reloadConfigs").executes(ReloadConfig())
            )
        dispatcher.register(veryBigCommand)
    }

}