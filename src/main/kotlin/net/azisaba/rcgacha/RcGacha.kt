package net.azisaba.rcgacha

import co.aikar.commands.PaperCommandManager
import com.charleskorn.kaml.Yaml
import net.azisaba.rcgacha.command.RcGachaCommand
import net.azisaba.rcgacha.config.RcGachaConfig
import net.azisaba.rcgacha.gacha.GachaManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RcGacha : JavaPlugin() {
    lateinit var config: RcGachaConfig

    lateinit var gachaManager: GachaManager
    lateinit var commandManager: PaperCommandManager
    lateinit var gachaFolder: File

    override fun onEnable() {
        gachaFolder = File(dataFolder, "gacha")

        // mkdir data folder
        dataFolder.mkdirs()

        // if a config file wasn't found
        if (!getConfigFile().exists()) {
            saveDefaultConfig()
            logger.info("Wrote new config file. Please edit it.")
        }

        // get fresh config
        loadConfig()

        gachaManager = GachaManager(logger, gachaFolder)

        // update gacha data
        updateGachaData()

        commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")

        commandManager.commandCompletions.registerAsyncCompletion("gachaname") {
            gachaManager.getAllGachaNames()
        }

        RcGachaCommand(this).register(commandManager)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun saveDefaultConfig() {
        saveConfig(RcGachaConfig())
    }

    override fun saveConfig() {
        saveConfig(config)
    }

    private fun saveConfig(config: RcGachaConfig) {
        getConfigFile().writeText(
            Yaml.default.encodeToString(RcGachaConfig.serializer(), config),
        )
    }

    private fun loadConfig() {
        config = Yaml.default.decodeFromString(RcGachaConfig.serializer(), getConfigFile().readText())
    }

    fun updateConfig() {
        reloadConfig()
    }

    fun updateGachaData() {
        logger.info("Updating gacha data...")
        gachaManager.loadAllGacha()
        logger.info("Gacha data was updated!")
    }

    private fun getConfigFile(): File = File(dataFolder, "config.yml")
}
