@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package io.papermc.paperweight.testplugin.structure

import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BaseBlock
import com.sk89q.worldedit.world.block.BlockTypes
import com.sk89q.worldedit.world.registry.BlockMaterial
import net.minecraft.world.level.block.entity.BlockEntity


import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random


fun copyStructure(file: File, location: Location, str: CustomStructure): Clipboard? {
  val worldEditWorld = BukkitAdapter.adapt(location.world)
  val loc = BlockVector3.at(location.blockX, location.blockY, location.blockZ)
  val format = ClipboardFormats.findByFile(file) ?: throw IllegalArgumentException("Invalid file format")

  FileInputStream(file).use { inputStream ->
    val clipboard = format.getReader(inputStream).use { it.read() }
    val editSession = WorldEdit.getInstance().newEditSession(worldEditWorld)
    try {
      val operation = ClipboardHolder(clipboard)
        .createPaste(editSession)
        .to(loc)
        .ignoreAirBlocks(false)
        .build()

      Operations.complete(operation)
    } finally {
      editSession.close()
    }
    return clipboard
  }
}
data class WeightItem(
  val item : ItemStack,
  val weight : Int,
  val amount : IntRange,
)

val Material.item
  get() = ItemStack(this,1)

fun selectItemBasedOnWeight(items: List<WeightItem>): WeightItem {
  val totalWeight = items.sumOf { it.weight }
  val randomValue = Random.nextDouble() * totalWeight
  var cumulativeWeight = 0.0

  for (item in items) {
    cumulativeWeight += item.weight
    if (randomValue <= cumulativeWeight) {
      return item
    }
  }
  throw IllegalArgumentException()
}

