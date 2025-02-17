package net.azisaba.rcgacha

import co.aikar.commands.PaperCommandManager
import com.charleskorn.kaml.Yaml
import net.azisaba.rcgacha.command.RcGachaCommand
import net.azisaba.rcgacha.config.GachaConfig
import net.azisaba.rcgacha.gacha.GachaManager
import net.azisaba.rcgacha.util.toRarityData
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RcGacha : JavaPlugin() {
    lateinit var config: GachaConfig
    lateinit var gachaManager: GachaManager
    lateinit var commandManager: PaperCommandManager

    override fun onEnable() {
        // mkdir data folder
        dataFolder.mkdirs()

        // if a config file wasn't found
        if (!getConfigFile().exists()) {
            saveDefaultConfig()
            logger.info("Wrote new config file. Please edit it.")
        }

        // get fresh config
        loadConfig()

        // update gacha data
        updateGachaData()

        commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")

        RcGachaCommand(this).register(commandManager)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun saveDefaultConfig() {
        saveConfig(GachaConfig())
    }

    override fun saveConfig() {
        saveConfig(config)
    }

    private fun saveConfig(config: GachaConfig) {
        getConfigFile().writeText(
            Yaml.default.encodeToString(GachaConfig.serializer(), config),
        )
    }

    fun updateConfig() {
        reloadConfig()
        updateGachaData()
    }

    private fun loadConfig() {
        config = Yaml.default.decodeFromString(GachaConfig.serializer(), getConfigFile().readText())
    }

    private fun updateGachaData() {
        logger.info("Updating gacha data...")
        gachaManager = GachaManager()
        for ((k, v) in config.rarities) {
            gachaManager.addRarity(k, toRarityData(k, v))
            gachaManager.addItemByMap(k, v.items)
        }
        logger.info("Gacha data was updated!")
    }

    private fun getConfigFile(): File = File(dataFolder, "config.yml")
}
