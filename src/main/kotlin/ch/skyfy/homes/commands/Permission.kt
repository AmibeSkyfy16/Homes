package ch.skyfy.homes.commands

import com.mojang.brigadier.Command
import net.minecraft.server.command.ServerCommandSource

abstract class Permission(open val permission: String) : Command<ServerCommandSource>