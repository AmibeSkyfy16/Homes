package ch.skyfy.homes.api.utils

import ch.skyfy.homes.api.HomesAPIMod
import ch.skyfy.homes.api.config.Bonus
import ch.skyfy.homes.api.config.Configs
import ch.skyfy.homes.api.config.Player
import ch.skyfy.homes.api.config.Rule
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.CompletableFuture
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

fun setupConfigDirectory() {
    try {
        if (!HomesAPIMod.CONFIG_DIRECTORY.exists()) HomesAPIMod.CONFIG_DIRECTORY.createDirectory()
    } catch (e: java.lang.Exception) {
        HomesAPIMod.LOGGER.fatal("An exception occurred. Could not create the root folder that should contain the configuration files")
        throw RuntimeException(e)
    }
}

fun setupExtensionDirectory() {
    try {
        if (!HomesAPIMod.EXTENSION_DIRECTORY.exists()) HomesAPIMod.EXTENSION_DIRECTORY.createDirectory()
    } catch (e: java.lang.Exception) {
        HomesAPIMod.LOGGER.fatal("An exception occurred. Could not create the extension folder that should contain the extensions files")
        throw RuntimeException(e)
    }
}

fun <S : ServerCommandSource> getConfigFiles(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
    val list = mutableListOf<String>()
    list.add(Configs.PLAYERS_HOMES.relativePath.fileName.toString())
    list.add(Configs.RULES_CONFIG.relativePath.fileName.toString())
    list.add(Configs.BONUS_CONFIG.relativePath.fileName.toString())
    list.add("ALL")
    return CommandSource.suggestMatching(list, suggestionsBuilder)
}

fun getPlayer(playerEntity: ServerPlayerEntity): Player? = Configs.PLAYERS_HOMES.serializableData.players.firstOrNull { it.uuid == playerEntity.uuidAsString }

fun getRule(player: Player): Rule? = Configs.RULES_CONFIG.serializableData.groupsRules.firstOrNull { it.name == player.groupRuleName }?.rule

fun getBonus(rule: Rule): Bonus? = Configs.BONUS_CONFIG.serializableData.groupBonus.firstOrNull { it.name == rule.groupBonusName }?.bonus
