package net.azisaba.rcgacha.gacha

import co.aikar.commands.annotation.Default
import com.charleskorn.kaml.Yaml
import net.azisaba.rcgacha.config.GachaConfig
import net.azisaba.rcgacha.extension.getChildFile
import net.azisaba.rcgacha.gacha.rarity.RarityData
import net.azisaba.rcgacha.util.toRarityData
import java.io.File
import java.util.UUID
import java.util.logging.Logger

class GachaManager(
    private val logger: Logger,
    private val gachaDataFolder: File,
) {
    init {
        if (gachaDataFolder.mkdir()) {
            saveExampleGachaData("_example")
        }
    }

    private val gachaMap = mutableMapOf<String, Gacha>()

    fun loadAllGacha() {
        logger.info("Loading gacha files...")
        gachaMap.clear()
        var successCount = 0
        gachaDataFolder.walk().forEach {
            // if it isn't yml file
            if (it.extension != "yml") {
                logger.warning("${it.name} skipped. This isn't yml file.")
                return@forEach
            }

            if (it.name.startsWith("_")) {
                logger.info("${it.name} skipped. (ignored file)")
                return@forEach
            }

            if (loadGacha(it.nameWithoutExtension)) {
                successCount++
            }
        }
        logger.info("Gacha file loading completed. success: $successCount")
    }

    fun loadGacha(gachaName: String): Boolean {
        try {
            val gachaConfig =
                Yaml.default.decodeFromString(
                    GachaConfig.serializer(),
                    gachaDataFolder.getChildFile("$gachaName.yml").readText(),
                )
            gachaMap[gachaName] =
                Gacha().apply {
                    for ((k, v) in gachaConfig.rarities) {
                        addRarity(k, toRarityData(k, v))
                        addItemByMap(k, v.items)
                    }
                }
            logger.info("$gachaName loaded.")
            return true
        } catch (e: Exception) {
            logger.warning("Failed to load $gachaName: $e")
            return false
        }
    }

    fun getGacha(gachaName: String): Result<Gacha> {
        val gacha = gachaMap[gachaName]
        return if (gacha != null) {
            Result.success(gacha)
        } else {
            Result.failure(IllegalArgumentException("No gacha name matched to $gachaName"))
        }
    }

    fun getAllGachaNames(): Set<String> = gachaMap.keys.toSet()

    fun roll(
        gachaName: String,
        playerUUID: UUID,
    ): Result<GachaResult> =
        getGacha(gachaName).fold({
            Result.success(it.roll(playerUUID))
        }) {
            Result.failure(it)
        }

    fun roll(
        gachaName: String,
        playerUuid: UUID,
        @Default("1") count: Int,
    ): Result<List<GachaResult>> =
        getGacha(gachaName).fold({
            Result.success(it.roll(playerUuid, count))
        }) {
            Result.failure(it)
        }

    fun getRarityData(
        gachaName: String,
        rarityName: String,
    ): RarityData =
        getGacha(gachaName).getOrThrow().rarityChooser.rarityInfoMap[rarityName]
            ?: error("Failed to get rarity info. gachaName: $gachaName, name: $rarityName")

    fun saveExampleGachaData(gachaName: String) {
        gachaDataFolder.getChildFile("$gachaName.yml").writeText(
            Yaml.default.encodeToString(GachaConfig.serializer(), GachaConfig()),
        )
    }
}
