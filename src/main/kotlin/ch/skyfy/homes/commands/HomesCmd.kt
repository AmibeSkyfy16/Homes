package ch.skyfy.homes.commands

import ch.skyfy.homes.config.CommandsAndPermissionsInformation
import ch.skyfy.homes.config.Configs
import ch.skyfy.homes.config.Player
import ch.skyfy.jsonconfiglib.updateMap
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.command.CommandSource.suggestMatching
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.AdvancementCommand
import net.minecraft.server.command.CommandManager.*
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

class HomesCmd {

    private fun <S : ServerCommandSource> homesListForAnotherPlayer(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val targetPlayerName = StringArgumentType.getString(commandContext, "playerName")
        val spe = commandContext.source.server.playerManager.getPlayer(targetPlayerName) ?: return suggestionsBuilder.buildFuture()
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { it.uuid == spe.uuidAsString } ?: return suggestionsBuilder.buildFuture()
        return homesListImpl(suggestionsBuilder, player)
    }

    private fun <S : ServerCommandSource> homesList(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val spe = commandContext.source.player ?: return suggestionsBuilder.buildFuture()
        val player = Configs.PLAYERS_HOMES.serializableData.players.find { it.uuid == spe.uuidAsString } ?: return suggestionsBuilder.buildFuture()
        return homesListImpl(suggestionsBuilder, player)
    }

    private fun homesListImpl(suggestionsBuilder: SuggestionsBuilder?, player: Player): CompletableFuture<Suggestions> {
        return suggestMatching(player.homes.map { it.name }, suggestionsBuilder)
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val veryBigCommand = literal("homes").requires { source -> source.hasPermissionLevel(0) }
            .then(
                literal("player").requires { source -> source.hasPermissionLevel(4) }.then(
                    argument("playerName", StringArgumentType.string()).suggests { context, suggestionBuilder ->
                        EntityArgumentType.players().listSuggestions(context, suggestionBuilder)
                    }.then( // /homes player <playerName> create
                        literal("create").then(
                            argument("homeName", StringArgumentType.string()).executes(CreateHomeForAnotherPlayer("homes.commands.homes.player.create")).then(
                                argument("x", DoubleArgumentType.doubleArg()).then(
                                    argument("y", DoubleArgumentType.doubleArg()).then(
                                        argument("z", DoubleArgumentType.doubleArg()).then(
                                            argument("yaw", FloatArgumentType.floatArg()).then(
                                                argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeForAnotherPlayerWithCoordinates("homes.commands.homes.player.create.with-coordinates"))
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ).then( // /homes player <playerName> delete
                        literal("delete").then(
                            argument("homeName", StringArgumentType.string()).suggests(this::homesListForAnotherPlayer).executes(DeleteHomeForAnotherPlayer("homes.commands.homes.player.delete"))
                        )
                    ).then( // /homes player <playerName> teleport <homeName>
                        literal("teleport").then(
                            argument("homeName", StringArgumentType.string()).suggests(this::homesListForAnotherPlayer).executes(TeleportHomeToAnotherPlayer("homes.commands.homes.player.teleport"))
                        )
                    ).then( // /homes player <playerName> list
                        literal("list").executes(ListHomeForAnotherPlayer("homes.commands.homes.player.list"))
                    )
                )
            ).then( // /homes create
                literal("create").then(
                    argument("homeName", StringArgumentType.string()).executes(CreateHome("homes.commands.homes.create")).then(
                        argument("x", DoubleArgumentType.doubleArg()).requires { source -> source.hasPermissionLevel(4) }.then(
                            argument("y", DoubleArgumentType.doubleArg()).then(
                                argument("z", DoubleArgumentType.doubleArg()).then(
                                    argument("yaw", FloatArgumentType.floatArg()).then(
                                        argument("pitch", FloatArgumentType.floatArg()).executes(CreateHomeWithCoordinates("homes.commands.homes.create.with-coordinates"))
                                    )
                                )
                            )
                        )
                    )
                )
            ).then( // /homes delete
                literal("delete").then(
                    argument("homeName", StringArgumentType.string()).suggests(this::homesList).executes(DeleteHome("homes.commands.homes.delete"))
                )
            ).then( // /homes teleport
                literal("teleport").then(
                    argument("homeName", StringArgumentType.string()).suggests(this::homesList).executes(TeleportHome("homes.commands.homes.teleport"))
                )
            ).then( // /homes list
                literal("list").executes(ListHome("homes.commands.homes.list"))
            ).then( // /homes reloadConfigs
                literal("reloadConfigs").executes(ReloadConfig())
            )
        dispatcher.register(veryBigCommand)


//        dispatcher.root.children.forEach {
//            if (it.name != "homes") return@forEach
//            registerCommandsInConfig(it)
//        }

//        Configs.COMMANDS_AND_PERMISSIONS_LIST.updateMap(CommandsAndPermissionsInformation::map) { it.putAll(map) }

    }

//    private val map = mutableMapOf<String, String>()

//    private fun registerCommandsInConfig(origin: CommandNode<ServerCommandSource>, children: CommandNode<ServerCommandSource> = origin, deep: Int = 0, sb: StringBuilder = StringBuilder(children.name)): StringBuilder {
//        if (deep > 0) sb.append(" " + children.usageText)
//
//
//        if (children.children.isEmpty()) {
//
//            var perm = ""
//            if (children.command is Permission) perm = (children.command as Permission).permission
//            map[sb.toString()] = perm
//
//            return sb.clear().append(origin.name)
//        }
//
//        if (children.command is Permission) map[sb.toString()] = (children.command as Permission).permission
//
//        children.children.forEach { registerCommandsInConfig(origin, it, deep + 1, sb) }
//        return sb
//    }

}