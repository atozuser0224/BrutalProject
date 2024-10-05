package io.papermc.paperweight.testplugin.structure

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.joml.Vector3i
import java.io.File
import kotlin.random.Random

interface CustomStructure {
  fun getSchematic() : List<String>
  fun getItems() : List<WeightItem>
  fun getWeight() : Int
  fun getOffset() : Vector3i
  fun getDensity() : Pair<Int,Int>
}
