package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.model.player.Moderation;
import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandUnmute extends FCommand {

    public CommandUnmute(FModule module, String name) {
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

        database.execute(() -> asyncOnCommand(commandSender, command, alias, args));

        return true;
    }

    public void asyncOnCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias,
                               @NotNull String[] args) {

        if (args.length == 0) {
            sendUsageMessage(commandSender, alias);
            return;
        }

        FPlayer fPlayer = playerManager.getOffline(args[0]);

        if (fPlayer == null) {
            sendErrorMessage(commandSender, getModule() + ".null-player");
            return;
        }

        database.getMute(fPlayer);

        Moderation mute = fPlayer.getMute();

        if (mute == null || mute.isExpired()) {
            sendErrorMessage(commandSender, this + ".not-muted");
            return;
        }

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);

        if (cmdSettings.isHaveCooldown()){
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return;
        }

        fPlayer.unmute();

        String message = locale.getVaultString(cmdSettings.getSender(), this + ".message")
                .replace("<player>", fPlayer.getMinecraftName());

        sendFormattedMessage(commandSender, MessageUtil.formatAll(cmdSettings.getSender(), message));

        if (!cmdSettings.isConsole()) {
            cmdSettings.getFPlayer().playSound(cmdSettings.getSender(), cmdSettings.getSender(), this.toString());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        tabCompleteClear();
        if (args.length == 1) {
            playerManager.getMutedPlayers().forEach(string ->
                    isStartsWith(args[0], string));
        }

        return getSortedTabComplete();
    }
}
