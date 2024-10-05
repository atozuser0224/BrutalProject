package io.papermc.paperweight.testplugin.structure

import com.sk89q.worldedit.world.block.BlockState
import io.papermc.paperweight.testplugin.structure.impl.IceStorage
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.plugin.Plugin
import java.io.File

object StructureManager {
  private var index : Int = 0
  private val list : List<CustomStructure> = listOf(IceStorage)
  fun addChunk(plugin: Plugin,chunk: Chunk){
    index++

    val str = list.filter { it.getWeight() % index == 0 }.random()
    val centerX = (chunk.x * 16 + 8)+str.getOffset().x  // 청크 시작 X좌표 + 8
    val centerZ = (chunk.z * 16 + 8)+str.getOffset().z  // 청크 시작 Z좌표 + 8

    // 지면 레벨을 기준으로 Y 좌표 설정 (Y = 64, 또는 다른 적절한 값 사용 가능)
    val groundY = (chunk.world.getHighestBlockYAt(centerX, centerZ)+str.getOffset().y).toDouble()
    str.getSchematic().map {it ->
      val file = File(plugin.dataFolder, "/sche/${it}.schem")
      val che = parseSchematic(file)
      CopyStructure(file, Location(chunk.world,centerX.toDouble(),groundY,centerZ.toDouble()))
      che
    }.forEach {
      it.BlockEntities.forEach {item->
        if (item.Id == "minecraft:chest"){
          val chest = chunk.world.getBlockAt(centerX,groundY.toInt(),centerZ)
          (chest.state as? Chest)?.blockInventory?.let {chest->
            repeat(chest.size){n->

            }
          }
        }
      }
    }
  }

}
