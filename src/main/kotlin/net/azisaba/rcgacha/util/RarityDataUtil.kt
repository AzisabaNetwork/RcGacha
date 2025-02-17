package net.azisaba.rcgacha.util

import net.azisaba.rcgacha.config.RarityConfig
import net.azisaba.rcgacha.gacha.rarity.RarityData

fun toRarityData(
    name: String,
    config: RarityConfig,
) = RarityData(
    name = name,
    base = config.base,
    pityStart = config.start,
    pityEnd = config.end,
    mmSkillName = config.mmSkillName,
    mmSkillNameForBulk = config.mmSkillNameForBulk,
)
