package ch.skyfy.homes.api

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import kotlin.reflect.KClass

abstract class Feature {

    companion object {

        private val map: MutableMap<String, KClass<*>> = mutableMapOf()

        fun <S : Feature> registerFeature(name: String, kClass: KClass<S>){
            map[name] = kClass
        }

        fun getFeature(name: String) : Feature? {
            try {
                return map[name]?.objectInstance as Feature
            } catch (_: Exception) { }
            return null
        }

    }

    abstract fun teleportHomeImpl(context: CommandContext<ServerCommandSource>): Boolean

}