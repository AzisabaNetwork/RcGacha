package net.azisaba.rcgacha.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import net.azisaba.rcgacha.RcGacha
import net.azisaba.rcgacha.util.prefixed
import net.azisaba.rcgacha.util.prefixedFail
import net.azisaba.rcgacha.util.prefixedSuccess
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("rcgacha")
class RcGachaCommand(
    private val plugin: RcGacha,
) : BaseCommand() {
    // register command
    fun register(commandManager: PaperCommandManager) {
        commandManager.registerCommand(this)
    }

    @Default
    fun default(
        sender: CommandSender,
        help: CommandHelp,
    ) {
        if (sender.hasPermission("rcgacha.cmd.rcgacha.help")) {
            help(sender, help)
        } else {
            sender.sendMessage(prefixedFail("Fmm... you have no permission to use this."))
        }
    }

    @HelpCommand
    @Subcommand("help")
    @CommandPermission("rcgacha.cmd.rcgacha.help")
    fun help(
        sender: CommandSender,
        help: CommandHelp,
    ) {
        help.showHelp()
    }

    @Subcommand("roll")
    @Description("Let's roll gacha!")
    @CommandPermission("rcgacha.cmd.roll")
    fun roll(
        player: Player,
        @Default("1") count: Int,
    ) {
        val result = plugin.gachaManager.roll(player.uniqueId)
        player.sendMessage(prefixedSuccess(result.toString()))
    }

    @Subcommand("reload")
    @Description("Reload RcGacha's assets")
    @CommandPermission("rcgacha.cmd.rcgacha.reload")
    fun reload(sender: CommandSender) {
        // If add more reloadable assets, needs to fix this hardcoding.
        sender.sendMessage(prefixed("Reloadable assets: [config]"))
    }

    @Subcommand("reload config")
    @Description("Reload RcGacha's configuration file")
    @CommandPermission("rcgacha.cmd.rcgacha.reload.config")
    fun reloadConfig(sender: CommandSender) {
        sender.sendMessage(prefixedSuccess("Reloading configuration..."))
        plugin.updateConfig()
        sender.sendMessage(prefixedSuccess("Reload complete."))
    }

    @Subcommand("rarity list")
    @Description("check registered all rarity name")
    @CommandPermission("rcgacha.cmd.rcgacha.rarity.list")
    fun listAllName(sender: CommandSender) {
        val rarityNames =
            plugin.config.rarities.values
                .map { name -> Component.text("- $name") }

        sender.sendMessage(Component.text("=== Rarity List ==="))
        rarityNames.forEach { c -> sender.sendMessage(c) }
        sender.sendMessage(Component.text("==================="))
    }
}
