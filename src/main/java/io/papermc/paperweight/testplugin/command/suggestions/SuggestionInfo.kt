package io.papermc.paperweight.testplugin.command.suggestions

import org.bukkit.command.CommandSender

/**
 * Class containing basic information relevant for the creation of suggestions.
 *
 * @property sender The user that is entering the command
 * @property currentArgument The text that the user has already entered for the current argument
 * @property fullInput The entire command that the user as already entered
 */
data class SuggestionInfo
internal constructor(
    val sender: CommandSender,
    val currentArgument: String,
    val fullInput: String,
)
