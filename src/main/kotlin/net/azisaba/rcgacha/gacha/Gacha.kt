package net.azisaba.rcgacha.gacha

import net.azisaba.rcgacha.gacha.rarity.RarityData
import net.azisaba.rcgacha.gacha.rarity.RarityWeightedChooser
import org.jetbrains.annotations.NotNull
import java.util.UUID

class Gacha {
    val rarityChooser: RarityWeightedChooser = RarityWeightedChooser()
    val rarityChooserMap: HashMap<String, WeightedChooser<String>> = hashMapOf()

    fun addRarity(
        @NotNull rarityName: String,
        @NotNull rarityData: RarityData,
    ): Gacha {
        rarityChooser.addRarity(rarityName, rarityData)
        rarityChooserMap[rarityName] = WeightedChooser()
        return this
    }

    fun removeRarity(rarityName: String): Gacha {
        rarityChooser.removeRarity(rarityName)
        rarityChooserMap.remove(rarityName)
        return this
    }

    fun addItem(
        @NotNull rarityName: String,
        @NotNull itemName: String,
        @NotNull weight: Int,
    ): Gacha {
        getChooser(rarityName).addItem(itemName, weight)
        return this
    }

    fun addItemByMap(
        @NotNull rarityName: String,
        @NotNull itemMap: Map<String, Int>,
    ): Gacha {
        getChooser(rarityName).addItemByMap(itemMap)
        return this
    }

    fun removeItem(
        @NotNull rarityName: String,
        @NotNull itemName: String,
    ): Gacha {
        getChooser(rarityName).removeItem(itemName)
        return this
    }

    @NotNull
    fun roll(playerUUID: UUID): GachaResult {
        val rarityData = rarityChooser.roll(playerUUID)
        val itemName = getChooser(rarityData.name).roll()
        return GachaResult(itemName, rarityData.name, rarityData.mmSkillName)
    }

    @NotNull
    fun roll(
        @NotNull playerUUID: UUID,
        @NotNull draws: Int,
    ): List<GachaResult> {
        val rarityDataList = rarityChooser.roll(playerUUID, draws)
        val resultList = mutableListOf<GachaResult>()
        for (rarityData in rarityDataList) {
            resultList.add(GachaResult(getChooser(rarityData.name).roll(), rarityData.name, rarityData.mmSkillName))
        }
        return resultList
    }

    fun getAllRarityMap(): Map<String, RarityData> = rarityChooser.rarityInfoMap.toMap()

    @NotNull
    private fun getChooser(rarityName: String): WeightedChooser<String> =
        rarityChooserMap[rarityName] ?: error("This chooser wasn't registered in correct way")
}
