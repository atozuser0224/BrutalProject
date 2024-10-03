@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin.command.executors

import com.destroystokyo.paper.profile.PlayerProfile
import com.google.common.collect.Range
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.range.DoubleRangeProvider
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.math.BlockPosition
import io.papermc.paper.registry.TypedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * Gets an argument value from the CommandContext
 *
 * @param T The type of the argument
 * @param name The name of the argument
 * @return The value of the argument
 */
inline operator fun <reified T> CommandContext<CommandSourceStack>.get(name: String): T =
    this.getArgument(name, T::class.java)

// Resolvable arguments

/**
 * Gets an argument value from the CommandContext that has been created with
 * [entity("name") { ... }][de.md5lukas.paper.brigadier.arguments.entity].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getEntity(name: String): Entity = getEntities(name).first()

/**
 * Gets an argument value from the CommandContext that has been created with
 * [entities("name") { ... }][de.md5lukas.paper.brigadier.arguments.entities].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getEntities(name: String): List<Entity> =
    this.getArgument(name, EntitySelectorArgumentResolver::class.java).resolve(source)

/**
 * Gets an argument value from the CommandContext that has been created with
 * [player("name") { ... }][de.md5lukas.paper.brigadier.arguments.player].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getPlayer(name: String): Player = getPlayers(name).first()

/**
 * Gets an argument value from the CommandContext that has been created with
 * [player("name") { ... }][de.md5lukas.paper.brigadier.arguments.players].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getPlayers(name: String): List<Player> =
    this.getArgument(name, PlayerSelectorArgumentResolver::class.java).resolve(source)

/**
 * Gets an argument value from the CommandContext that has been created with
 * [players("name") { ... }][de.md5lukas.paper.brigadier.arguments.playerProfiles].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getPlayerProfiles(name: String): Collection<PlayerProfile> =
    this.getArgument(name, PlayerProfileListResolver::class.java).resolve(source)

/**
 * Gets an argument value from the CommandContext that has been created with
 * [blockPosition("name") { ... }][de.md5lukas.paper.brigadier.arguments.blockPosition].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getBlockPosition(name: String): BlockPosition =
    this.getArgument(name, BlockPositionResolver::class.java).resolve(source)

// Other special arguments

/**
 * Gets an argument value from the CommandContext that has been created with
 * [resourceKey("name") { ... }][de.md5lukas.paper.brigadier.arguments.resourceKey].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
@Suppress("UNCHECKED_CAST")
fun <T> CommandContext<CommandSourceStack>.getResourceKey(name: String): TypedKey<T> =
    this.getArgument(name, TypedKey::class.java) as TypedKey<T>

/**
 * Gets an argument value from the CommandContext that has been created with
 * [integerRange("name") { ... }][de.md5lukas.paper.brigadier.arguments.integerRange].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getIntegerRange(name: String): Range<Int> =
    this.getArgument(name, IntegerRangeProvider::class.java).range()

/**
 * Gets an argument value from the CommandContext that has been created with
 * [doubleRange("name") { ... }][de.md5lukas.paper.brigadier.arguments.doubleRange].
 *
 * @param name The name of the argument
 * @return The value of the argument
 */
fun CommandContext<CommandSourceStack>.getDoubleRange(name: String): Range<Double> =
    this.getArgument(name, DoubleRangeProvider::class.java).range()
