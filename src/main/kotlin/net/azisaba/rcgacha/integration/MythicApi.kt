package net.azisaba.rcgacha.integration

import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.inventory.ItemStack

val MythicApi: MythicBukkit
    get() = MythicBukkit.inst()

fun getMythicStack(
    id: String,
    amount: Int,
): ItemStack = MythicApi.itemManager.getItemStack(id, amount) ?: error("Failed to get item stack")
