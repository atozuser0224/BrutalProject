package io.papermc.paperweight.testplugin.structure

import com.google.gson.Gson
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

fun CopyStructure(file : File,location: Location) {
  val world = BukkitAdapter.adapt(location.world)

  // 플레이어 위치
  val loc = BlockVector3.at(location.blockX, location.blockY, location.blockZ)
  val format = ClipboardFormats.findByFile(file)?:throw IllegalArgumentException()
  try {
    FileInputStream(file).use { inputStream ->
      val reader = format.getReader(inputStream)
      val clipboard = reader?.read()  // 스키메틱 파일 읽기

      // EditSession을 통해 스키메틱 복사
      val editSession: EditSession = WorldEdit.getInstance().newEditSession(world)

      try {
        val operation = ClipboardHolder(clipboard)
          .createPaste(editSession)
          .to(loc)  // 플레이어 위치에 붙여넣기
          .ignoreAirBlocks(false)  // 공기 블록 복사 여부
          .build()

        // 스키메틱 적용 실행
        Operations.complete(operation)

      } finally {
        // 반드시 세션을 닫아야 함
        editSession.close()
      }
    }
  }catch (e: Exception) {
    e.printStackTrace()
  }
  throw IllegalArgumentException()
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
data class Schematic(
  val PaletteMax: Int,
  val Palette: Map<String, Int>,
  val Version: Int,
  val Length: Short,
  val Metadata: Metadata,
  val Height: Short,
  val DataVersion: Int,
  val BlockData: ByteArray,
  val BlockEntities: List<BlockEntity>,
  val Width: Short,
  val Offset: List<Int>,
  val Entities: List<Any>
)

data class Metadata(
  val FAWEVersion: Int,
  val WEOffsetX: Int,
  val WEOffsetY: Int,
  val WEOffsetZ: Int
)

data class BlockEntity(
  val Pos: List<Int>,
  val spawn_data: SpawnData?,
  val Items: List<Any>,
  val Id: String
)

data class SpawnData(
  val entity: Map<String, Any>
)
fun parseSchematic(file: File): Schematic {
  val gson = Gson()
  val reader = file.bufferedReader()
  return gson.fromJson(reader, Schematic::class.java)
}
