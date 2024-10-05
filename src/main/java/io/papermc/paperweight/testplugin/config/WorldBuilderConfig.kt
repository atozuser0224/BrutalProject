package io.papermc.paperweight.testplugin.config

import io.papermc.paperweight.testplugin.fastnoise.FastNoiseLite
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.LimitedRegion
import org.bukkit.generator.WorldInfo
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


class CustomChunkGenerator : ChunkGenerator() {
  private val terrainNoise: FastNoiseLite = FastNoiseLite()
  private val detailNoise: FastNoiseLite = FastNoiseLite()
  private val layers: HashMap<Int?, List<Material?>?> = object : HashMap<Int?, List<Material?>?>() {
    init {
      put(0, listOf(Material.SNOW_BLOCK, Material.POWDER_SNOW))
      put(1, listOf(Material.STONE, Material.PACKED_ICE, Material.FROSTED_ICE))
      put(2, listOf(Material.PACKED_ICE, Material.FROSTED_ICE))
      put(3, listOf(Material.BEDROCK))
    }
  }
  init {
    // Set frequencies, lower frequency = slower change.
    terrainNoise.SetFrequency(0.001f)
    detailNoise.SetFrequency(0.05f)

    // Fractal pattern (optional).
    terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm)
    terrainNoise.SetFractalOctaves(5)
  }

  override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
    var y = chunkData.minHeight
    while (y < 130 && y < chunkData.maxHeight) {
      for (x in 0..15) {
        for (z in 0..15) {
          val noise2 = (terrainNoise.GetNoise((x + (chunkX * 16)).toFloat(), (z + (chunkZ * 16)).toFloat()) * 2) + (detailNoise.GetNoise((x + (chunkX * 16)).toFloat(), (z + (chunkZ * 16)).toFloat()) / 10)
          val noise3 = detailNoise.GetNoise((x + (chunkX * 16)).toFloat(), y.toFloat(), (z + (chunkZ * 16)).toFloat())
          val currentY = (65 + (noise2 * 30))

          if (y < 1) {
            chunkData.setBlock(x, y, z, layers[3]!![random.nextInt(layers[3]!!.size)]!!)
          } else if (y < currentY) {
            val distanceToSurface = abs((y - currentY).toDouble()).toFloat() // The absolute y distance to the world surface.
            val function: Double = .1 * distanceToSurface.pow(2.0f) - 1 // A second grade polynomial offset to the noise max and min (1, -1).

            if (noise3 > min(function, -.3)) {
              // Set grass if the block closest to the surface.
              if (distanceToSurface < 1 && y > 63) {
                chunkData.setBlock(x, y, z, layers[0]!![0]!!)
              } else if (distanceToSurface < 5) {
                chunkData.setBlock(x, y, z, layers[1]!![random.nextInt(layers[1]!!.size)]!!)
              } else {
                var neighbour: Material? = Material.STONE
                val neighbourBlocks: List<Material?> = ArrayList(listOf(chunkData.getType(max((x - 1).toDouble(), 0.0).toInt(), y, z), chunkData.getType(x, max((y - 1).toDouble(), 0.0).toInt(), z), chunkData.getType(x, y, max((z - 1).toDouble(), 0.0).toInt()))) // A list of all neighbour blocks.

                // Randomly place vein anchors.
                if (random.nextFloat() < 0.002) {
                  neighbour = layers[2]!![random.nextInt(layers[2]!!.size).coerceAtMost(random.nextInt(layers[2]!!.size))] // A basic way to shift probability to lower values.
                }

                // If the current block has an ore block as neighbour, try the current block.
                if ((!layers[2]?.let { Collections.disjoint(neighbourBlocks, it) }!!)) {
                  for (neighbourBlock in neighbourBlocks) {
                    if (layers[2]!!.contains(neighbourBlock) && random.nextFloat() < -0.01 * layers[2]!!.indexOf(neighbourBlock) + 0.4) {
                      neighbour = neighbourBlock
                    }
                  }
                }

                chunkData.setBlock(x, y, z, neighbour!!)
              }
            }
          } else if (y < 62) {
            chunkData.setBlock(x, y, z, Material.ICE)
          }
        }
      }
      y++
    }
  }

  override fun getDefaultPopulators(world: World): MutableList<BlockPopulator> {
    return listOf(TreeTrunkPopulator() as BlockPopulator,OrePopulator() as BlockPopulator).toMutableList()
  }
  override fun shouldGenerateMobs(): Boolean {
    return true // 몬스터 생성 허용
  }
}


class TreeTrunkPopulator : BlockPopulator() {
  override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, limitedRegion: LimitedRegion) {
    // 청크 내에서 나무 줄기를 생성할 좌표를 무작위로 선택
    val x = random.nextInt(16) + (chunkX * 16)
    val z = random.nextInt(16) + (chunkZ * 16)
    val y = limitedRegion.getHighestBlockYAt(x, z) // 해당 위치에서 가장 높은 블록 위에서 나무 줄기를 생성
    if (limitedRegion.getType(x,y-1,z) == Material.SNOW_BLOCK){
      val trunkHeight = random.nextInt(4) + 2

      // 나무 줄기를 생성 (y축 방향으로)
      for (i in 0 until trunkHeight) {
        limitedRegion.setType(x, y + i, z, Material.SPRUCE_LOG) // 나무 줄기 블록 설정
      }
    }
  }
}
class OrePopulator : BlockPopulator() {
  override fun populate(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, limitedRegion: LimitedRegion) {
    // 청크 내 무작위 좌표에 광석을 생성
    generateOre(Material.COAL_ORE, random, chunkX, chunkZ, limitedRegion, 20, 17, 128, 0) // 석탄 광석
    generateOre(Material.IRON_ORE, random, chunkX, chunkZ, limitedRegion, 20, 9, 64, 0) // 철광석
    generateOre(Material.GOLD_ORE, random, chunkX, chunkZ, limitedRegion, 2, 9, 32, 0) // 금광석
    generateOre(Material.DIAMOND_ORE, random, chunkX, chunkZ, limitedRegion, 1, 8, 16, 0) // 다이아몬드 광석
    generateOre(Material.REDSTONE_ORE, random, chunkX, chunkZ, limitedRegion, 8, 8, 16, 0) // 레드스톤
    generateOre(Material.LAPIS_ORE, random, chunkX, chunkZ, limitedRegion, 1, 7, 32, 16) // 청금석
  }

  // 광석을 생성하는 메서드
  private fun generateOre(
    material: Material, random: Random, chunkX: Int, chunkZ: Int, limitedRegion: LimitedRegion,
    veinSize: Int, amountPerChunk: Int, maxY: Int, minY: Int
  ) {
    // 지정된 양의 광석을 해당 청크에 스폰
    for (i in 0 until amountPerChunk) {
      // 무작위 위치 (청크 내에서 x, z를 설정)
      val x = random.nextInt(16) + chunkX * 16
      val z = random.nextInt(16) + chunkZ * 16
      // 광석의 깊이를 설정 (최대 Y 높이와 최소 Y 높이 사이에서 설정)
      val y = random.nextInt(maxY - minY) + minY

      // 하나의 광맥(vein)을 생성
      for (j in 0 until veinSize) {
        val oreX = x + random.nextInt(2) - random.nextInt(2)
        val oreY = y + random.nextInt(2) - random.nextInt(2)
        val oreZ = z + random.nextInt(2) - random.nextInt(2)

        // 해당 좌표가 월드 범위 내인지 확인하고, 그 위치가 돌인지 확인
        if (limitedRegion.isInRegion(oreX, oreY, oreZ) && limitedRegion.getType(oreX, oreY, oreZ) == Material.STONE) {
          // 돌이 있을 때만 광석을 설정
          limitedRegion.setType(oreX, oreY, oreZ, material)
        }
      }
    }
  }
}
