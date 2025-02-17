package net.azisaba.rcgacha.config

import kotlinx.serialization.Serializable

@Serializable
data class RcGachaConfig(
    val version: Int = 0,
)
