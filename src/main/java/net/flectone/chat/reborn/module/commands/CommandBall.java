package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.RandomUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandBall extends FCommand {

    public CommandBall(FModule module, String name) {
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

        CmdSettings cmdSettings = processCommand(commandSender, command);

        if (cmdSettings.isHaveCooldown()) {
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return true;
        }

        if (cmdSettings.isMuted()) {
            cmdSettings.getFPlayer().sendMutedMessage(command.getName());
            return true;
        }

        if (cmdSettings.isDisabled()) {
            sendErrorMessage(commandSender, getModule() + ".you-disabled");
            return true;
        }

        List<String> answers = locale.getVaultStringList(commandSender, this + ".answers");
        if (answers.isEmpty()) return true;

        int randomPer = RandomUtil.nextInt(0, answers.size());
        String answer = answers.get(randomPer);

        String formatString = locale.getVaultString(commandSender, this + ".message");
        formatString = MessageUtil.formatPlayerString(commandSender, formatString)
                .replace("<answer>", answer);

        String message = String.join(" ", args);

        sendGlobalMessage(cmdSettings.getSender(), cmdSettings.getItemStack(), formatString, message, true);

        IntegrationsModule.sendDiscordBall(cmdSettings.getSender(), message, answer);

        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        tabCompleteClear();

        if (args.length == 1) {
            isTabCompleteMessage(commandSender, args[0], "message");
        }

        return getSortedTabComplete();
    }
}
