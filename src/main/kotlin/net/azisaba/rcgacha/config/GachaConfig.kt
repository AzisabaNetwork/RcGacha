package net.azisaba.rcgacha.config

import kotlinx.serialization.Serializable

@Serializable
data class RarityConfig(
    val base: Int = 10,
    val start: Int = 70,
    val end: Int = 100,
    val mmSkillName: String = "none",
    val items: Map<String, Int> = mapOf("stone" to 1),
)

@Serializable
data class GachaConfig(
    val rarities: Map<String, RarityConfig> =
        mapOf(
            "1" to RarityConfig(),
            "2" to RarityConfig(),
            "3" to RarityConfig(),
        ),
)
