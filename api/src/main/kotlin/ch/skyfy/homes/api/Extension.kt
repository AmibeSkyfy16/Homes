package ch.skyfy.homes.api

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import kotlin.reflect.KClass

abstract class Extension {

    companion object {

        private val extensions: MutableMap<String, KClass<*>> = mutableMapOf()

        fun <S : Extension> registerExtension(name: String, kClass: KClass<S>){
            extensions[name] = kClass
        }

        fun getExtension(name: String) : Extension? {
            try {
                return extensions[name]?.objectInstance as Extension
            } catch (_: Exception) { }
            return null
        }

    }

    open fun createHome(context: CommandContext<ServerCommandSource>) = true
    open fun deleteHome(context: CommandContext<ServerCommandSource>) = true
    open fun listHome(context: CommandContext<ServerCommandSource>) = true
    open fun teleportHome(context: CommandContext<ServerCommandSource>) = true


}