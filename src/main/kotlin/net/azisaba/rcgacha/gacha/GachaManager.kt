package net.azisaba.rcgacha.gacha

import net.azisaba.rcgacha.gacha.rarity.RarityData
import net.azisaba.rcgacha.gacha.rarity.RarityWeightedChooser
import org.jetbrains.annotations.NotNull
import java.util.UUID

class GachaManager {
    val rarityChooser: RarityWeightedChooser = RarityWeightedChooser()
    val rarityChooserMap: HashMap<String, WeightedChooser<String>> = hashMapOf()

    fun addRarity(
        @NotNull rarityName: String,
        @NotNull rarityData: RarityData,
    ) {
        rarityChooser.addRarity(rarityName, rarityData)
        rarityChooserMap[rarityName] = WeightedChooser()
    }

    fun removeRarity(rarityName: String) {
        rarityChooser.removeRarity(rarityName)
        rarityChooserMap.remove(rarityName)
    }

    fun addItem(
        @NotNull rarityName: String,
        @NotNull itemName: String,
        @NotNull weight: Int,
    ) {
        getChooser(rarityName).addItem(itemName, weight)
    }

    fun addItemByMap(
        @NotNull rarityName: String,
        @NotNull itemMap: Map<String, Int>,
    ) {
        getChooser(rarityName).addItemByMap(itemMap)
    }

    fun removeItem(
        @NotNull rarityName: String,
        @NotNull itemName: String,
    ) {
        getChooser(rarityName).removeItem(itemName)
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

    @NotNull
    private fun getChooser(rarityName: String): WeightedChooser<String> =
        rarityChooserMap[rarityName] ?: error("This chooser wasn't registered in correct way")
}
