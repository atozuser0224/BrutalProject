package io.papermc.paperweight.testplugin.structure.impl

import io.papermc.paperweight.testplugin.structure.CustomStructure
import io.papermc.paperweight.testplugin.structure.WeightItem
import io.papermc.paperweight.testplugin.structure.item
import org.bukkit.Material
import org.joml.Vector3i
import kotlin.random.Random

object IceStorage : CustomStructure{
  override fun getSchematic(): List<String> = listOf("frontStrorage","backStrorage")

  override fun getItems(): List<WeightItem> = listOf(
    WeightItem(Material.COOKED_CHICKEN.item,25,5..11),
    WeightItem(Material.IRON_INGOT.item,5,2..5),
    WeightItem(Material.DIAMOND.item,1,1..1),
    WeightItem(Material.COOKED_RABBIT.item,25,11..24),
    WeightItem(Material.COAL.item,15,7..21)
  )

  override fun getWeight(): Int = 401

  override fun getOffset(): Vector3i = Vector3i(0,-15,0)
  override fun getDensity(): Pair<Int, Int> = 3 to 10
}

