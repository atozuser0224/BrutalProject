package io.papermc.paperweight.testplugin.pathfinder

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.*


class BreakSpecificBlockGoal(private val mob: Mob, targetBlock: Block,val radius : Int) : Goal() {
  private val targetBlock: Block = targetBlock // 찾을 블록
  private var targetBlockPos: BlockPos? = null // 블록 위치

  init {
    this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)) // 이동과 시야 제어 플래그 설정
  }

  // 패스파인더가 실행될 조건을 정의합니다.
  override fun canUse(): Boolean {
    // 주변에 목표 블록이 있는지 확인
    targetBlockPos = findNearestTargetBlock()
    return targetBlockPos != null // 찾으면 실행
  }

  // 패스파인더가 종료될 조건
  override fun canContinueToUse(): Boolean {
    // 계속해서 목표 블록을 파괴할 수 있는지 확인
    return targetBlockPos != null && mob.distanceToSqr(targetBlockPos!!.x.toDouble(), targetBlockPos!!.y.toDouble(), targetBlockPos!!.z.toDouble()) > 2
  }

  // 블록을 찾는 로직
  private fun findNearestTargetBlock(): BlockPos? {
    val level: Level = mob.level()
    val mobPos = mob.blockPosition()

    for (x in -radius..radius) {
      for (y in -radius..radius) {
        for (z in -radius..radius) {
          val pos = mobPos.offset(x, y, z)
          val blockState: BlockState = level.getBlockState(pos)

          if (blockState.`is`(targetBlock)) {
            return pos // 목표 블록 발견
          }
        }
      }
    }

    return null // 목표 블록이 없으면 null 반환
  }

  // 블록을 파괴하는 로직
  override fun tick() {
    if (targetBlockPos != null) {
      // 목표 블록을 향해 이동
      mob.navigation.moveTo(targetBlockPos!!.x.toDouble(), targetBlockPos!!.y.toDouble(), targetBlockPos!!.z.toDouble(), 1.0)


      // 일정 거리 안에 도달하면 블록을 파괴
      if (mob.distanceToSqr(targetBlockPos!!.x.toDouble(), targetBlockPos!!.y.toDouble(), targetBlockPos!!.z.toDouble()) < 2) {
        targetBlockPos?.let { mob.level().destroyBlock(it, true) } // 블록 파괴
        targetBlockPos = null // 블록을 파괴한 후 목표를 해제
      }
    }
  }
}
