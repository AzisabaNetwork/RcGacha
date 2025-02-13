package net.azisaba.rcgacha.gacha.rarity

data class RarityData(
    val name: String,
    val base: Int,
    val pityStart: Int,
    val pityEnd: Int,
    val mmSkillName: String,
)
