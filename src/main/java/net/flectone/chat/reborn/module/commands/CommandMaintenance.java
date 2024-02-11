package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandMaintenance extends FCommand {

    public CommandMaintenance(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;

        plugin.getActionManager().add(new MaintenanceListener(null));
        register();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {

        if (args.length == 0) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        String arg = args[0].toLowerCase();

        if (!arg.equals("off") && !arg.equals("on")) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        boolean isEnabled = commands.getBoolean(getName() + ".turned-on");

        if (arg.equals("on") && isEnabled) {
            sendErrorMessage(commandSender, this + ".already");
            return true;
        }

        if (arg.equals("off") && !isEnabled) {
            sendErrorMessage(commandSender, this + ".not");
            return true;
        }

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);

        if (cmdSettings.isHaveCooldown()) {
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return true;
        }

        if (cmdSettings.isMuted()) {
            cmdSettings.getFPlayer().sendMutedMessage(command.getName());
            return true;
        }

        isEnabled = arg.equals("on");

        if (isEnabled) {

            String kickedMessage = locale.getVaultString(commandSender, this + ".kicked-message");

            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !player.hasPermission("flectonechatreborn.commands.maintenance"))
                    .forEach(player -> player.kickPlayer(MessageUtil.formatAll(player, kickedMessage)));
        }

        String serverMessage = locale.getVaultString(commandSender, this + ".turned-" + arg);
        sendGlobalMessage(cmdSettings.getSender(), cmdSettings.getItemStack(), serverMessage, "", false);

        commands.set(getName() + ".turned-on", isEnabled);
        commands.save();

        IntegrationsModule.sendDiscordMaintenance(cmdSettings.getSender(), isEnabled ? "turn-on" : "turn-off");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        tabCompleteClear();
        if (args.length == 1) {
            isStartsWith(args[0], "on");
            isStartsWith(args[0], "off");
        }

        return getSortedTabComplete();
    }
}
