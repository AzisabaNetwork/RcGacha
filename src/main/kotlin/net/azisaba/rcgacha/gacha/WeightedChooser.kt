package net.azisaba.rcgacha.gacha

import org.jetbrains.annotations.NotNull
import kotlin.random.Random

open class WeightedChooser<T> {
    val itemWeightMap: HashMap<T, Int> = hashMapOf()
    var weightSum: Long = 0

    open fun addItem(
        @NotNull item: T,
        weight: Int,
    ) {
        if (weight <= 0) {
            throw IllegalArgumentException("weight must be positive number")
        }

        itemWeightMap[item] = weight
        updateSum()
    }

    fun addItemByMap(
        @NotNull itemMap: Map<T, Int>,
    ) {
        for ((k, v) in itemMap) {
            if (v <= 0) {
                throw IllegalArgumentException("weight must be positive number")
            }

            itemWeightMap[k] = v
        }
        updateSum()
    }

    open fun removeItem(
        @NotNull item: T,
    ) {
        if (!itemWeightMap.containsKey(item)) {
            throw RuntimeException("This key isn't found in item weight map.")
        }
        itemWeightMap.remove(item)
        updateSum()
    }

    @NotNull
    open fun roll(): T {
        var randValue = random.nextLong(weightSum)
        for ((k, v) in itemWeightMap) {
            randValue -= v
            if (randValue < 0) return k
        }
        throw RuntimeException("Won't reach here.")
    }

    @NotNull
    open fun roll(draws: Int): List<T> {
        val result = mutableListOf<T>()
        for (i in 0..<draws) {
            result.add(roll())
        }
        return result
    }

    private fun updateSum() {
        weightSum = itemWeightMap.values.sum().toLong()
    }

    companion object {
        val random: Random = Random(System.currentTimeMillis())
    }
}
