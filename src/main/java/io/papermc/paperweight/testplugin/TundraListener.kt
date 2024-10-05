package io.papermc.paperweight.testplugin

import io.papermc.paperweight.testplugin.pathfinder.BreakSpecificBlockGoal
import io.papermc.paperweight.testplugin.pathfinder.EntityAttackPosPathFinder
import io.papermc.paperweight.testplugin.structure.CopyStructure
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.random.Random


class TundraListener(val plugin: Plugin) : Listener {
  @EventHandler
  fun onCreatureSpawn(e : CreatureSpawnEvent){
    e.isCancelled = false
    val nmsEntity: Mob = (e.entity as CraftLivingEntity).handle as Mob

    nmsEntity.goalSelector.addGoal(1, EntityAttackPosPathFinder(nmsEntity, 1.0))
    // 특정 블록을 찾고 파괴하는 커스텀 패스파인더를 추가합니다.
    nmsEntity.goalSelector.addGoal(0, BreakSpecificBlockGoal(nmsEntity, Blocks.CAMPFIRE,16))
  }
  @EventHandler
  fun onChunkLoad(e : ChunkLoadEvent){
    val r = Random.nextInt(0,1000)
    if (r > 990 && e.isNewChunk){
      Bukkit.getLogger().info("spawn")
      val file = File(plugin.dataFolder, "/sche/frontStrorage.schem")
      val file2 = File(plugin.dataFolder, "/sche/backStrorage.schem")
      val centerX = (e.chunk.x * 16 + 8)  // 청크 시작 X좌표 + 8
      val centerZ = (e.chunk.z * 16 + 8)  // 청크 시작 Z좌표 + 8

      // 지면 레벨을 기준으로 Y 좌표 설정 (Y = 64, 또는 다른 적절한 값 사용 가능)
      val groundY = e.world.getHighestBlockYAt(centerX, centerZ).toDouble()
      CopyStructure(file2, Location(e.world,centerX.toDouble(),groundY-15,centerZ.toDouble()))
      CopyStructure(file, Location(e.world,centerX.toDouble(),groundY-15,centerZ.toDouble()))
    }
  }
}

