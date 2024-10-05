package io.papermc.paperweight.testplugin

import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import io.papermc.paperweight.testplugin.command.arguments.command
import io.papermc.paperweight.testplugin.command.arguments.greedyString
import io.papermc.paperweight.testplugin.command.arguments.literal
import io.papermc.paperweight.testplugin.command.arguments.player
import io.papermc.paperweight.testplugin.command.executors.executes
import io.papermc.paperweight.testplugin.command.executors.get
import io.papermc.paperweight.testplugin.command.executors.getPlayer
import io.papermc.paperweight.testplugin.command.requirements.requiresPlayer
import io.papermc.paperweight.testplugin.command.suggestions.SuggestionProviders
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream


fun command(plugin: JavaPlugin){
  command("example") {
    literal("playersOnly") {
      requiresPlayer()
      executes<Player> { player, _ ->
        player.sendMessage("You are allowed to use this")
      }
    }
    literal("suggest") {
      greedyString("suggestion") {
        executes<CommandSender> { _, context ->
          val suggestion: String = context["suggestion"]
          Bukkit.getConsoleSender().sendPlainMessage("Someone suggested: $suggestion")
        }
        executes<ConsoleCommandSender> { sender, context ->
          val suggestion: String = context["suggestion"]
          sender.sendPlainMessage("You suggested: $suggestion")
        }
      }
    }
    literal("greet") {
      player("to-greet") {
        suggests(SuggestionProviders.onlinePlayers(includeSelf = false))
        executes<CommandSender> { sender, context ->
          context.getPlayer("to-greet").sendPlainMessage("${sender.name} is greeting you")
        }
      }
    }
  }.register(plugin)
}
