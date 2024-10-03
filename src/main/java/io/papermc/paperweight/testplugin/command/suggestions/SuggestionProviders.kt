@file:Suppress("UnstableApiUsage")

package io.papermc.paperweight.testplugin.command.suggestions

import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import java.util.concurrent.CompletableFuture
import kotlin.collections.forEach
import kotlin.text.startsWith
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Different suggestion providers to be used with
 * [RequiredArgumentBuilder.suggests][com.mojang.brigadier.builder.RequiredArgumentBuilder.suggests]
 * which make the implementation simpler.
 *
 * Do keep in mind that a custom [SuggestionProvider] is not a replacement for a
 * [CustomArgumentType][io.papermc.paper.command.brigadier.argument.CustomArgumentType] which also
 * performs input parsing and validation.
 */
object SuggestionProviders {
  /**
   * Suggester that never suggests anything.
   *
   * @return empty suggester
   */
  fun empty() = SuggestionProvider<CommandSourceStack> { _, builder -> builder.buildFuture() }

  /**
   * Suggester that always suggests the provided strings.
   *
   * @param strings the strings to suggest
   * @return static string suggester
   */
  fun static(vararg strings: String) =
      SuggestionProvider<CommandSourceStack> { _, builder ->
        strings.forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that always suggests the provided strings.
   *
   * @param strings the strings to suggest
   * @return static string suggester
   */
  fun static(strings: Iterable<String>) =
      SuggestionProvider<CommandSourceStack> { _, builder ->
        strings.forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that always suggests the provided strings with tooltips.
   *
   * When a tooltip is [Component.empty], the suggestion will be without a tooltip.
   *
   * @param strings the strings and tooltips to suggest
   * @return static string suggester
   */
  fun static(vararg strings: Pair<String, Component>) =
      SuggestionProvider<CommandSourceStack> { _, builder ->
        strings.forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that always suggests the provided strings with tooltips.
   *
   * When a tooltip is [Component.empty], the suggestion will be without a tooltip.
   *
   * @param strings the strings and tooltips to suggest
   * @return static string suggester
   */
  @JvmName("staticWithTooltips")
  fun static(strings: Iterable<Pair<String, Component>>) =
      SuggestionProvider<CommandSourceStack> { _, builder ->
        strings.forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that retrieves the suggestions based on the suggestion info.
   *
   * @param suggestions function that produces suggestions for the given input
   * @return dynamic string suggester
   */
  fun dynamic(suggestions: (SuggestionInfo) -> Iterable<String>) =
      SuggestionProvider<CommandSourceStack> { context, builder ->
        suggestions(SuggestionInfo(context.source.sender, builder.remaining, builder.input))
            .forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that retrieves the suggestions based on the suggestion info asynchronously.
   *
   * @param suggestions function that produces suggestions for the given input asynchronously
   * @return dynamic string suggester
   */
  @JvmName("dynamicAsync")
  fun dynamic(suggestions: (SuggestionInfo) -> CompletableFuture<Iterable<String>>) =
      SuggestionProvider<CommandSourceStack> { context, builder ->
        suggestions(SuggestionInfo(context.source.sender, builder.remaining, builder.input))
            .thenApply {
              it.forEach { builder.suggestIfApplicable(it) }
              builder.build()
            }
      }

  /**
   * Suggester that retrieves the suggestions and tooltips based on the suggestion info.
   *
   * When a tooltip is [Component.empty], the suggestion will be without a tooltip.
   *
   * @param suggestions function that produces suggestions and tooltips for the given input
   * @return dynamic string suggester
   */
  @JvmName("dynamicWithTooltips")
  fun dynamic(suggestions: (SuggestionInfo) -> Iterable<Pair<String, Component>>) =
      SuggestionProvider<CommandSourceStack> { context, builder ->
        suggestions(SuggestionInfo(context.source.sender, builder.remaining, builder.input))
            .forEach { builder.suggestIfApplicable(it) }
        builder.buildFuture()
      }

  /**
   * Suggester that retrieves the suggestions and tooltips based on the suggestion info
   * asynchronously.
   *
   * When a tooltip is [Component.empty], the suggestion will be without a tooltip.
   *
   * @param suggestions function that produces suggestions and tooltips for the given input
   *   asynchronously
   * @return dynamic string suggester
   */
  @JvmName("dynamicWithTooltipsAsync")
  fun dynamic(
      suggestions: (SuggestionInfo) -> CompletableFuture<Iterable<Pair<String, Component>>>
  ) =
      SuggestionProvider<CommandSourceStack> { context, builder ->
        suggestions(SuggestionInfo(context.source.sender, builder.remaining, builder.input))
            .thenApply {
              it.forEach { builder.suggestIfApplicable(it) }
              builder.build()
            }
      }

  /**
   * Suggester that suggests online players that the sender can see.
   *
   * The visibility is checked with [Player.canSee] if the sender is actually a player.
   *
   * @param includeSelf Whether the sender themself should be included in the suggestions if he is a
   *   player
   * @return online player suggester
   */
  fun onlinePlayers(includeSelf: Boolean) =
      SuggestionProvider<CommandSourceStack> { context, builder ->
        val sender = context.source.sender
        val nonPlayer = sender !is Player

        sender.server.onlinePlayers.forEach {
          if ( (includeSelf || sender != it)) {
            builder.suggestIfApplicable(it.name)
          }
        }

        builder.buildFuture()
      }

  private fun SuggestionsBuilder.shouldSuggest(suggestion: String) =
      suggestion.startsWith(remaining, ignoreCase = true)

  private fun SuggestionsBuilder.suggestIfApplicable(suggestion: String) {
    if (shouldSuggest(suggestion)) {
      suggest(suggestion)
    }
  }

  private fun SuggestionsBuilder.suggestIfApplicable(suggestion: Pair<String, Component>) {
    if (shouldSuggest(suggestion.first)) {
      if (Component.empty() === suggestion.second) {
        suggest(suggestion.first)
      } else {
        suggest(suggestion.first, MessageComponentSerializer.message().serialize(suggestion.second))
      }
    }
  }
}
