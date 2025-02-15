package net.azisaba.rcgacha.gacha.rarity

import net.azisaba.rcgacha.gacha.WeightedChooser
import org.jetbrains.annotations.NotNull
import java.util.UUID

class RarityWeightedChooser : WeightedChooser<String>() {
    val rarityInfoMap: HashMap<String, RarityData> = hashMapOf()
    val playerRollCountMap: HashMap<String, HashMap<UUID, Int>> = hashMapOf()

    fun addRarity(
        rarityName: String,
        rarityData: RarityData,
    ) {
        playerRollCountMap[rarityName] = hashMapOf()
        rarityInfoMap[rarityName] = rarityData
        addItem(rarityName, rarityData.base)
    }

    fun removeRarity(rarityName: String) {
        playerRollCountMap.remove(rarityName)
        rarityInfoMap.remove(rarityName)
        removeItem(rarityName)
    }

    fun getCustomizedMap(playerUuid: UUID): MutableMap<String, Int> =
        itemWeightMap.toMutableMap().apply {
            for ((k, v) in rarityInfoMap) {
                val currentRolls = getRollCount(k, playerUuid) ?: 0
                if (currentRolls >= v.pityStart) {
                    this@apply.computeIfPresent(k) { _, b -> b + (currentRolls - (v.pityStart - 1)) }
                }
            }
        }

    @NotNull
    fun getRollCount(
        rarityName: String,
        playerUuid: UUID,
    ) = getRarityRollCountMap(rarityName)?.getOrDefault(playerUuid, 0)

    @NotNull
    fun addRollCount(
        rarityName: String,
        playerUuid: UUID,
    ): Int {
        val rollCountMap = (getRarityRollCountMap(rarityName) ?: error("Failed to get roll count. name: $rarityName"))
        rollCountMap.compute(playerUuid) { _, v ->
            if (v == null) {
                1
            } else {
                v + 1
            }
        }
        return rollCountMap.getOrDefault(playerUuid, 0)
    }

    fun resetRollCount(
        rarityName: String,
        playerUuid: UUID,
    ) {
        playerRollCountMap[rarityName]?.remove(playerUuid)
    }

    @NotNull
    private fun getRarityRollCountMap(rarityName: String): HashMap<UUID, Int>? {
        if (!playerRollCountMap.containsKey(rarityName)) {
            playerRollCountMap[rarityName] = hashMapOf()
        }
        return playerRollCountMap[rarityName]
    }

    private fun getPityEnd(rarityName: String) =
        (rarityInfoMap[rarityName] ?: error("Failed to get rarity info. name: $rarityName")).pityEnd

    fun roll(playerUuid: UUID): RarityData {
        val weightMap = getCustomizedMap(playerUuid)

        var randValue = random.nextInt(weightMap.values.sum())
        for ((k, v) in weightMap) {
            randValue -= v
            if (randValue < 0) {
                return getRarityData(k)
            } else {
                if (addRollCount(k, playerUuid) >= getPityEnd(k)) {
                    resetRollCount(k, playerUuid)
                    return getRarityData(k)
                }
            }
        }
        throw RuntimeException("This exception won't be happen.")
    }

    private fun getRarityData(rarityName: String) = rarityInfoMap[rarityName] ?: error("Failed to get rarity info")

    fun roll(
        playerUuid: UUID,
        draws: Int,
    ): List<RarityData> {
        val result = mutableListOf<RarityData>()
        for (i in 0..<draws) {
            result.add(roll(playerUuid))
        }
        return result
    }
}
