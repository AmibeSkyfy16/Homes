package ch.skyfy.homes.config

enum class Perms2(s: String) {
    CREATE_HOME("homes.commands.homes.create"),
    CREATE_HOME_WITH_COORDINATES(""),
    DELETE_HOME(""),
    TELEPORT_HOME(""),
    LIST_HOME(""),

    CREATE_HOME_FOR_ANOTHER_PLAYER(""),
    CREATE_HOME_FOR_ANOTHER_PLAYER_WITH_COORDINATES(""),
    DELETE_HOME_FOR_ANOTHER_PLAYER(""),
    TELEPORT_HOME_TO_ANOTHER_PLAYER(""),
    LIST_HOME_FOR_ANOTHER_PLAYER(""),

    ALL("")
}