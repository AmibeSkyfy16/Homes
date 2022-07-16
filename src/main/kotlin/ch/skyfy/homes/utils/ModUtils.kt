package ch.skyfy.homes.utils

import ch.skyfy.homes.HomesMod
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

fun setupConfigDirectory(){
    try {
        if(!HomesMod.CONFIG_DIRECTORY.exists()) HomesMod.CONFIG_DIRECTORY.createDirectory()
    } catch (e: java.lang.Exception) {
        HomesMod.LOGGER.fatal("An exception occurred. Could not create the root folder that should contain the configuration files")
        throw RuntimeException(e)
    }
}