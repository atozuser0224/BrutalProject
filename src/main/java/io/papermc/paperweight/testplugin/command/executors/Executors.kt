@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin.command.executors

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paperweight.testplugin.command.BrigadierDSL
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Brigadier command executor
 *
 * @param S The type of the sender
 * @param R The type of the command return value
 */
typealias CommandExecutor<S, R> = (S, CommandContext<CommandSourceStack>) -> R

@PublishedApi
internal object NoCommandProvided : Command<CommandSourceStack> {
  private val plugin: JavaPlugin = JavaPlugin.getProvidingPlugin(NoCommandProvided::class.java)

  override fun run(context: CommandContext<CommandSourceStack>): Int {
    plugin.slF4JLogger.warn(
        """No command executor provided for "{}" with sender {}""", context.input, context.source)
    return 0
  }
}

/**
 * Add a command executor that is able to return an arbitrary integer result upon completion.
 *
 * Make sure to always add more specific command executors last. Otherwise the specific one will
 * never be executed. Example:
 * ```
 * executesResulting<CommandSender> { ... }
 * executesResulting<Player> { ... }
 * ```
 *
 * @param S The type of the command sender
 * @param block The code to execute
 * @receiver The command node to add the executor to
 */
@BrigadierDSL
inline fun <reified S : CommandSender> ArgumentBuilder<CommandSourceStack, *>.executesResulting(
    crossinline block: CommandExecutor<S, Int>
) {
  val command = this.command ?: NoCommandProvided
  executes {
    val sender = it.source.sender

    if (sender is S) {
      block(sender, it)
    } else {
      command.run(it)
    }
  }
}

/**
 * Add a command executor that always returns a successful integer result.
 *
 * Make sure to always add more specific command executors last. Otherwise the specific one will
 * never be executed. Example:
 * ```
 * executes<CommandSender> { ... }
 * executes<Player> { ... }
 * ```
 *
 * @param S The type of the command sender
 * @param block The code to execute
 * @receiver The command node to add the executor to
 */
@BrigadierDSL
inline fun <reified S : CommandSender> ArgumentBuilder<CommandSourceStack, *>.executes(
    crossinline block: CommandExecutor<S, Unit>
) {
  executesResulting<S> { player, commandContext ->
    block(player, commandContext)
    Command.SINGLE_SUCCESS
  }
}
