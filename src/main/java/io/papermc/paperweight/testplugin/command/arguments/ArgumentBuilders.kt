@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin.command.arguments

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paperweight.testplugin.command.BrigadierDSL
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate
import io.papermc.paper.command.brigadier.argument.range.DoubleRangeProvider
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.entity.LookAnchor
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import java.util.UUID
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import org.bukkit.GameMode
import org.bukkit.HeightMap
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.block.BlockState
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

/**
 * Entrypoint to the Brigadier DSL. Creates the literal root node of the command.
 *
 * The used name will be used to execute the command. With a name of `test`, the player must enter
 * `/test`.
 *
 * @param name The name of the command
 * @param block The body to configure the command node
 */
@BrigadierDSL
inline fun command(
    name: String,
    block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit,
): LiteralCommandNode<CommandSourceStack> = Commands.literal(name).also(block).build()

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.literal(
    name: String,
    block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit,
) {
  then(Commands.literal(name).also(block))
}

@BrigadierDSL
inline fun <T> ArgumentBuilder<CommandSourceStack, *>.argument(
    name: String,
    type: ArgumentType<T>,
    block: RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit,
): ArgumentBuilder<*, *>? = then(Commands.argument(name, type).also(block))

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.word(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, String>.() -> Unit,
) = argument(name, StringArgumentType.word(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.string(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, String>.() -> Unit,
) = argument(name, StringArgumentType.string(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.greedyString(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, String>.() -> Unit,
) = argument(name, StringArgumentType.greedyString(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.boolean(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Boolean>.() -> Unit,
) = argument(name, BoolArgumentType.bool(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.integer(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    block: RequiredArgumentBuilder<CommandSourceStack, Int>.() -> Unit,
) = argument(name, IntegerArgumentType.integer(min, max), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.long(
    name: String,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    block: RequiredArgumentBuilder<CommandSourceStack, Long>.() -> Unit,
) = argument(name, LongArgumentType.longArg(min, max), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.float(
    name: String,
    min: Float = -Float.MAX_VALUE,
    max: Float = Float.MAX_VALUE,
    block: RequiredArgumentBuilder<CommandSourceStack, Float>.() -> Unit,
) = argument(name, FloatArgumentType.floatArg(min, max), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.double(
    name: String,
    min: Double = -Double.MAX_VALUE,
    max: Double = Double.MAX_VALUE,
    block: RequiredArgumentBuilder<CommandSourceStack, Double>.() -> Unit,
) = argument(name, DoubleArgumentType.doubleArg(min, max), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.entity(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, EntitySelectorArgumentResolver>.() -> Unit,
) = argument(name, ArgumentTypes.entity(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.entities(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, EntitySelectorArgumentResolver>.() -> Unit,
) = argument(name, ArgumentTypes.entities(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.player(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver>.() -> Unit,
) = argument(name, ArgumentTypes.player(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.players(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver>.() -> Unit,
) = argument(name, ArgumentTypes.players(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.playerProfiles(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, PlayerProfileListResolver>.() -> Unit,
) = argument(name, ArgumentTypes.playerProfiles(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.blockPosition(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, BlockPositionResolver>.() -> Unit,
) = argument(name, ArgumentTypes.blockPosition(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.blockState(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, BlockState>.() -> Unit,
) = argument(name, ArgumentTypes.blockState(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.itemStack(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, ItemStack>.() -> Unit,
) = argument(name, ArgumentTypes.itemStack(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.itemPredicate(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, ItemStackPredicate>.() -> Unit,
) = argument(name, ArgumentTypes.itemPredicate(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.namedColor(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, NamedTextColor>.() -> Unit,
) = argument(name, ArgumentTypes.namedColor(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.component(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Component>.() -> Unit,
) = argument(name, ArgumentTypes.component(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.style(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Style>.() -> Unit,
) = argument(name, ArgumentTypes.style(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.signedMessage(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, SignedMessageResolver>.() -> Unit,
) = argument(name, ArgumentTypes.signedMessage(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.scoreboardDisplaySlot(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, DisplaySlot>.() -> Unit,
) = argument(name, ArgumentTypes.scoreboardDisplaySlot(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.namespacedKey(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, NamespacedKey>.() -> Unit,
) = argument(name, ArgumentTypes.namespacedKey(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.key(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Key>.() -> Unit,
) = argument(name, ArgumentTypes.key(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.integerRange(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, IntegerRangeProvider>.() -> Unit,
) = argument(name, ArgumentTypes.integerRange(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.doubleRange(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, DoubleRangeProvider>.() -> Unit,
) = argument(name, ArgumentTypes.doubleRange(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.world(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, World>.() -> Unit,
) = argument(name, ArgumentTypes.world(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.gameMode(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, GameMode>.() -> Unit,
) = argument(name, ArgumentTypes.gameMode(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.heightMap(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, HeightMap>.() -> Unit,
) = argument(name, ArgumentTypes.heightMap(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.uuid(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, UUID>.() -> Unit,
) = argument(name, ArgumentTypes.uuid(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.objectiveCriteria(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Criteria>.() -> Unit,
) = argument(name, ArgumentTypes.objectiveCriteria(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.entityAnchor(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, LookAnchor>.() -> Unit,
) = argument(name, ArgumentTypes.entityAnchor(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.time(
    name: String,
    mintime: Int = 0,
    block: RequiredArgumentBuilder<CommandSourceStack, Int>.() -> Unit,
) = argument(name, ArgumentTypes.time(mintime), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.templateMirror(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, Mirror>.() -> Unit,
) = argument(name, ArgumentTypes.templateMirror(), block)

@BrigadierDSL
inline fun ArgumentBuilder<CommandSourceStack, *>.templateRotation(
    name: String,
    block: RequiredArgumentBuilder<CommandSourceStack, StructureRotation>.() -> Unit,
) = argument(name, ArgumentTypes.templateRotation(), block)

@BrigadierDSL
inline fun <T> ArgumentBuilder<CommandSourceStack, *>.resource(
    name: String,
    registryKey: RegistryKey<T>,
    block: RequiredArgumentBuilder<CommandSourceStack, T>.() -> Unit,
) = argument(name, ArgumentTypes.resource(registryKey), block)

@BrigadierDSL
inline fun <T> ArgumentBuilder<CommandSourceStack, *>.resourceKey(
    name: String,
    registryKey: RegistryKey<T>,
    block: RequiredArgumentBuilder<CommandSourceStack, TypedKey<T>>.() -> Unit,
) = argument(name, ArgumentTypes.resourceKey(registryKey), block)
