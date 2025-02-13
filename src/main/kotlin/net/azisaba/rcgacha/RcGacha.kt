package net.azisaba.rcgacha

import com.charleskorn.kaml.Yaml
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RcGacha : JavaPlugin() {
    override fun onEnable() {
        // mkdir data folder
        dataFolder.mkdirs()

        if (!getConfigFile().exists()) {
            saveDefaultConfig()
            logger.info("Please edit config & reload plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        val config = loadConfig()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun saveDefaultConfig() {
        saveConfig(RcGachaConfig())
    }

    private fun saveConfig(config: RcGachaConfig) {
        getConfigFile().writeText(
            Yaml.default.encodeToString(RcGachaConfig.serializer(), config),
        )
    }

    private fun loadConfig(): RcGachaConfig = Yaml.default.decodeFromString(RcGachaConfig.serializer(), getConfigFile().readText())

    private fun getConfigFile(): File = File(dataFolder, "config.yml")
}
