package net.azisaba.rcgacha.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import io.lumine.mythic.bukkit.MythicBukkit
import net.azisaba.rcgacha.RcGacha
import net.azisaba.rcgacha.util.failComponent
import net.azisaba.rcgacha.util.prefixed
import net.azisaba.rcgacha.util.prefixedFail
import net.azisaba.rcgacha.util.prefixedSuccess
import net.azisaba.rcitemlogging.RcItemLogging
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import java.util.stream.Collectors

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

    @Subcommand("roll-single")
    @Description("Roll gacha once.")
    @CommandPermission("rcgacha.cmd.rcgacha.roll.single")
    @CommandCompletion("@players @gachaname")
    fun rollSingle(
        sender: CommandSender,
        targetPlayerName: String,
        gachaName: String,
    ) {
        val player = plugin.server.getPlayer(targetPlayerName) ?: error("Failed to get player. name: $targetPlayerName")
        val result =
            plugin.gachaManager.roll(gachaName, player.uniqueId).getOrElse {
                sender.sendMessage(failComponent("Failed to roll gacha."))
                return
            }
        sender.sendMessage(prefixedSuccess(result.toString()))
        player.inventory.addItem(
            MythicBukkit.inst().itemManager.getItemStack(result.itemName),
        )
        RcItemLogging.getApi().put(
            "rcgacha_roll_single",
            "#system",
            player.name,
            "Player got ${result.itemName}x1",
            player.uniqueId,
        )
        if (!MythicBukkit.inst().apiHelper.castSkill(player, result.mmSkillName)) {
            sender.sendMessage(failComponent("Failed to execute skill: ${result.mmSkillName}"))
        }
    }

    @Subcommand("roll-many")
    @Description("Let's roll gacha!")
    @CommandPermission("rcgacha.cmd.rcgacha.roll.many")
    @CommandCompletion("@players @gachaname @range:1-20")
    fun roll(
        sender: CommandSender,
        targetPlayerName: String,
        gachaName: String,
        @Default("1") count: Int,
    ) {
        val player = plugin.server.getPlayer(targetPlayerName) ?: error("Failed to get player. name: $targetPlayerName")
        val result =
            plugin.gachaManager.roll(gachaName, player.uniqueId, count).getOrElse {
                sender.sendMessage(failComponent("Failed to roll gacha."))
                return
            }
        sender.sendMessage(prefixedSuccess("Gacha Result"))
        result
            .stream()
            .collect(Collectors.groupingBy({ r -> r.rarityName }, Collectors.counting()))
            .toSortedMap()
            .pollFirstEntry()
            .also {
                val skillName = plugin.gachaManager.getRarityData(gachaName, it.key).mmSkillNameForBulk
                if (!MythicBukkit.inst().apiHelper.castSkill(player, skillName)) {
                    sender.sendMessage(failComponent("Failed to execute skill: $skillName"))
                }
            }
        result
            .stream()
            .collect(Collectors.groupingBy({ r -> r.itemName }, Collectors.counting()))
            .forEach { (k, v) ->
                player.inventory.addItem(
                    MythicBukkit.inst().itemManager.getItemStack(k).apply {
                        amount = v.toInt()
                    },
                )
                RcItemLogging.getApi().put(
                    "rcgacha_roll_single",
                    "#system",
                    player.name,
                    "Player got $k +$v",
                    player.uniqueId,
                )
            }
    }

    @Subcommand("new-gacha")
    @Description("Create new gacha file")
    @CommandPermission("rcgacha.cmd.rcgacha.new.gacha")
    fun generateNewGachaFile(
        sender: CommandSender,
        newGachaName: String,
    ) {
        plugin.gachaManager.saveExampleGachaData(newGachaName)
        sender.sendMessage(prefixedSuccess("New gacha file: $newGachaName.yml was created."))
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
        sender.sendMessage(prefixed("Reloading configuration..."))
        plugin.updateConfig()
        sender.sendMessage(prefixedSuccess("Configuration reload completed."))
    }

    @Subcommand("reload gacha")
    @Description("Reload RcGacha's gacha data file")
    @CommandPermission("rcgacha.cmd.rcgacha.reload.gacha")
    fun reloadGacha(sender: CommandSender) {
        sender.sendMessage(prefixed("Reloading gacha data..."))
        plugin.updateGachaData()
        sender.sendMessage(prefixedSuccess("Gacha data reload completed."))
    }

    @Subcommand("list gacha")
    @Description("List all gacha name")
    @CommandPermission("rcgacha.cmd.rcgacha.list.gacha")
    fun listGacha(sender: CommandSender) {
        sender.sendMessage(prefixed("=== Gacha List ==="))
        plugin.gachaManager.getAllGachaNames().forEach { sender.sendMessage(prefixed(it)) }
        sender.sendMessage(prefixed("=================="))
    }

    @Subcommand("list rarity")
    @Description("check registered all rarity name")
    @CommandPermission("rcgacha.cmd.rcgacha.list.rarity")
    @CommandCompletion("@gachaname")
    fun listAllName(
        sender: CommandSender,
        gachaName: String,
    ) {
        val rarityNames =
            plugin.gachaManager
                .getGacha(gachaName)
                .getOrElse({
                    sender.sendMessage(failComponent("Failed to roll gacha."))
                    return
                })
                .getAllRarityMap()

        sender.sendMessage(Component.text("=== Rarity List ==="))
        rarityNames.forEach { (k, v) -> sender.sendMessage("$k: $v") }
        sender.sendMessage(Component.text("==================="))
    }
}
