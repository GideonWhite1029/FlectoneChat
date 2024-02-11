package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandFirstonline extends FCommand {

    public CommandFirstonline(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {
        if (args.length == 0) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        String selectedPlayer = args[0];
        FPlayer fPlayer = playerManager.getOffline(selectedPlayer);
        if (fPlayer == null) {
            sendErrorMessage(commandSender, getModule() + ".null-player");
            return true;
        }

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);
        if (cmdSettings.isHaveCooldown()) {
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return true;
        }

        OfflinePlayer player = fPlayer.getOfflinePlayer();

        String message = locale.getVaultString(commandSender, this + ".message")
                .replace("<player>", selectedPlayer)
                .replace("<time>", TimeUtil.convertTime(cmdSettings.getSender(), player.getFirstPlayed()));

        message = IntegrationsModule.setPlaceholders(player, player, message);
        message = MessageUtil.formatAll(cmdSettings.getSender(), message);

        sendFormattedMessage(commandSender, message);

        if (!cmdSettings.isConsole()) {
            cmdSettings.getFPlayer().playSound(cmdSettings.getSender(), cmdSettings.getSender(), this.toString());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        tabCompleteClear();
        if (args.length == 1) {
            isConfigModePlayer(args[0]);
        }

        return getSortedTabComplete();
    }
}
