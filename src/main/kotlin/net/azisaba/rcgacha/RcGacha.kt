package net.azisaba.rcgacha

import co.aikar.commands.PaperCommandManager
import com.charleskorn.kaml.Yaml
import net.azisaba.rcgacha.command.RcGachaCommand
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RcGacha : JavaPlugin() {
    lateinit var config: RcGachaConfig
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
        refreshConfig()

        commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")

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

    fun refreshConfig() {
        config = Yaml.default.decodeFromString(RcGachaConfig.serializer(), getConfigFile().readText())
    }

    private fun getConfigFile(): File = File(dataFolder, "config.yml")
}
