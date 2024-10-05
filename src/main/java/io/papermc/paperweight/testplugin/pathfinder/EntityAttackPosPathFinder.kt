package io.papermc.paperweight.testplugin.pathfinder

import io.papermc.paperweight.testplugin.util.hungry
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player
import org.bukkit.craftbukkit.entity.CraftMob
import org.bukkit.persistence.PersistentDataContainer
import java.util.*


class EntityAttackPosPathFinder(private val mob: Mob, private val speed : Double = 1.0) : Goal() {
  private var targetPlayer: Player? = null

  init {
    this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET)) // 이동과 시야 제어 플래그 설정
  }
  override fun canUse(): Boolean {
    val craftMob = mob.bukkitEntity as CraftMob
    val pdc: PersistentDataContainer = craftMob.persistentDataContainer
    if ((mob.type.category == MobCategory.CREATURE || mob.type.category == MobCategory.AMBIENT||pdc.hungry < 50 )){
      val players = mob.level().getEntitiesOfClass(Player::class.java, mob.boundingBox.inflate(10.0)).filter { !it.isCreative } // 탐색 반경 설정
      if (players.isNotEmpty()) {
        this.targetPlayer = players[0] // 첫 번째 플레이어를 타겟으로 설정
        return true
      }
    }
    return false
  }
  override fun tick() {
    this.targetPlayer?.let {
      // 플레이어를 향해 이동
      mob.navigation.moveTo(it, this.speed)


      // 플레이어가 일정 거리 안에 들어오면 공격
      if (mob.distanceToSqr(it) < 2.0) {
        mob.doHurtTarget(it) // 기본 공격
        this.targetPlayer!!.hurt(mob.level().damageSources().mobAttack(this.mob), 5.0f);      }
    }
  }
}
