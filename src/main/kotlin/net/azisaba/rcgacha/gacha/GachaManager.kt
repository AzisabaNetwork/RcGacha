package net.azisaba.rcgacha.gacha

import net.azisaba.rcgacha.gacha.rarity.RarityData
import net.azisaba.rcgacha.gacha.rarity.RarityWeightedChooser
import org.jetbrains.annotations.NotNull
import java.util.UUID

class GachaManager {
    val rarityChooser: RarityWeightedChooser = RarityWeightedChooser()
    val rarityChooserMap: HashMap<String, WeightedChooser<String>> = hashMapOf()

    fun addRarity(
        rarityName: String,
        rarityData: RarityData,
    ) {
        rarityChooser.addRarity(rarityName, rarityData)
        rarityChooserMap[rarityName] = WeightedChooser()
    }

    fun removeRarity(rarityName: String) {
        rarityChooser.removeRarity(rarityName)
        rarityChooserMap.remove(rarityName)
    }

    fun addItem(
        rarityName: String,
        itemName: String,
        weight: Int,
    ) {
        getChooser(rarityName).addItem(itemName, weight)
    }

    fun removeItem(
        rarityName: String,
        itemName: String,
    ) {
        getChooser(rarityName).removeItem(itemName)
    }

    fun roll(playerUUID: UUID): GachaResult {
        val rarityData = rarityChooser.roll(playerUUID)
        val itemName = getChooser(rarityData.name).roll()
        return GachaResult(itemName, rarityData.name, rarityData.mmSkillName)
    }

    fun roll(
        playerUUID: UUID,
        draws: Int,
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
