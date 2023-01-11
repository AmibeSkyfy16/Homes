package ch.skyfy.homes.features

import ch.skyfy.homes.commands.TeleportHome
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import kotlin.reflect.KClass

abstract class ConditionalFeature {

    val map: Map<KClass<Any>, String> = mutableMapOf()

    fun <S> impl(kClass: KClass<out S>, context: CommandContext<ServerCommandSource>) : Boolean where S : Command<ServerCommandSource> {
        return when(kClass){
            TeleportHome::class ->  teleportHomeImpl(context)
            else -> {false}
        }
    }

    abstract fun teleportHomeImpl(context: CommandContext<ServerCommandSource>): Boolean

}