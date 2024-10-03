@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paperweight.testplugin.command.arguments.command
import io.papermc.paperweight.testplugin.command.arguments.greedyString
import io.papermc.paperweight.testplugin.command.arguments.literal
import io.papermc.paperweight.testplugin.command.arguments.player
import io.papermc.paperweight.testplugin.command.executors.executes
import io.papermc.paperweight.testplugin.command.executors.get
import io.papermc.paperweight.testplugin.command.executors.getPlayer
import io.papermc.paperweight.testplugin.command.requirements.requiresPlayer
import io.papermc.paperweight.testplugin.command.suggestions.SuggestionProviders
import io.papermc.paperweight.testplugin.config.CustomChunkGenerator
import kotlinx.coroutines.delay
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin
import org.checkerframework.checker.nullness.qual.NonNull
import org.checkerframework.framework.qual.DefaultQualifier


@DefaultQualifier(NonNull::class)
class TestPlugin : JavaPlugin(), Listener {

    override fun onEnable() {
      server.pluginManager.registerSuspendingEvents(this, this)
      command(this)
    }

  override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
    return CustomChunkGenerator()
  }
}

fun LiteralCommandNode<CommandSourceStack>.register(plugin: JavaPlugin){
  plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
    val commands: Commands = event.registrar()
    commands.register(
      this,
      "some bukkit help description string",
      listOf("an-alias")
    )
  }
}




