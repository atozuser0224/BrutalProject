package io.papermc.paperweight.testplugin

import io.papermc.paperweight.testplugin.pathfinder.BreakSpecificBlockGoal
import io.papermc.paperweight.testplugin.pathfinder.EntityAttackPosPathFinder
import io.papermc.paperweight.testplugin.structure.StructureManager
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
    if (e.isNewChunk){
      StructureManager.addChunk(plugin,e.chunk)
    }
  }
}

