package io.papermc.paperweight.testplugin.structure

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BlockState
import com.sk89q.worldedit.world.block.BlockTypes
import io.papermc.paperweight.testplugin.structure.impl.IceStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.benwoodworth.knbt.*
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

object StructureManager {
  private var index : Int = 0
  private val list : List<CustomStructure> = listOf(IceStorage)
  fun addChunk(plugin: Plugin,chunk: Chunk){
    index++
    val filterList = list.filter { it.getWeight() % index == 0 }
    if (filterList.isNotEmpty()){
      val str = filterList.random()
      val centerX = (chunk.x * 16 + 8)+str.getOffset().x  // 청크 시작 X좌표 + 8
      val centerZ = (chunk.z * 16 + 8)+str.getOffset().z  // 청크 시작 Z좌표 + 8

      val groundY = (chunk.world.getHighestBlockYAt(centerX, centerZ)+str.getOffset().y).toDouble()
      println(centerX)
      println(groundY)
      println(centerZ)
      str.getSchematic().forEach {it ->
        val file = File(plugin.dataFolder, "/sche/${it}.schem")
        copyStructure(file, Location(chunk.world,centerX.toDouble(),groundY,centerZ.toDouble()),str)?.let {clipboard->
          val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Gzip // None, Gzip, Zlib
          }

          plugin.launch(plugin.minecraftDispatcher) {

            val sche = withContext(Dispatchers.IO){
              val tag: NbtTag = file.inputStream().use { input ->
                nbt.decodeFromStream(input)
              }
              tag.nbtCompound["Schematic"]}
            sche?.nbtCompound?.get("BlockEntities")?.nbtList?.forEach {
              val pos = it.nbtCompound["Pos"]?.nbtIntArray
              val offset = sche.nbtCompound["Metadata"]?.nbtCompound


              val chestBlock = chunk.world.getBlockAt(centerX + (pos?.get(0) ?: 0) + (offset?.get("WEOffsetX")?.nbtInt?:return@launch).value,
                groundY.toInt() + (pos?.get(1) ?: 0) + ((offset["WEOffsetY"]?.nbtInt)?.value?:0),
                centerZ + (pos?.get(2) ?: 0) + ((offset["WEOffsetZ"]?.nbtInt)?.value?:0))
              println(chestBlock.type)
              if (chestBlock.type == Material.CHEST) {
                val chest = chestBlock.state as Chest
                val chestInventory = chest.inventory
                for(n in 0..<26) {
                  if (Random.nextInt(str.getDensity().second) <= str.getDensity().first) {
                    val selected = selectItemBasedOnWeight(str.getItems())
                    val stack = selected.item.apply {
                      amount = selected.amount.random()
                    }
                    chestInventory.setItem(n ,stack)
                  } else {
                    chestInventory.setItem(n,Material.AIR.item)
                  }
                }
              }
            }
          }

        }
      }
    }
  }
}
