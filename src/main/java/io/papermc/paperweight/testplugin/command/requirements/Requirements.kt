@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin.command.requirements

import com.mojang.brigadier.builder.ArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.RemoteConsoleCommandSender
import org.bukkit.entity.Player

/**
 * Combines the previous predicate with an is [ConsoleCommandSender] check.
 *
 * @param allowRcon If `true` [RemoteConsoleCommandSender] will also be allowed
 * @receiver The command node to add the requirement to
 */
fun ArgumentBuilder<CommandSourceStack, *>.requiresConsole(allowRcon: Boolean = true) {
  val predicate = this.requirement
  requires {
    predicate.test(it) &&
        (it.sender is ConsoleCommandSender || allowRcon && it.sender is RemoteConsoleCommandSender)
  }
}

/**
 * Combines the previous predicate with an is [Player] check.
 *
 * @receiver The command node to add the requirement to
 */
fun ArgumentBuilder<CommandSourceStack, *>.requiresPlayer() {
  val predicate = this.requirement
  requires { predicate.test(it) && it.sender is Player }
}

/**
 * Combines the previous predicate with a permission check on the
 * [CommandSender][org.bukkit.command.CommandSender]
 *
 * @receiver The command node to add the requirement to
 */
fun ArgumentBuilder<CommandSourceStack, *>.requiresPermission(permission: String) {
  val predicate = this.requirement
  requires { predicate.test(it) && it.sender.hasPermission(permission) }
}

/**
 * Combines the previous predicate with an additional arbitrary test
 *
 * @receiver The command node to add the requirement to
 */
fun ArgumentBuilder<CommandSourceStack, *>.andRequires(test: (CommandSourceStack) -> Boolean) {
  val predicate = this.requirement
  requires { predicate.test(it) && test(it) }
}
