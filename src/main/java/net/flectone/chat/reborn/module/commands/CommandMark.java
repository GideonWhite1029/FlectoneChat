package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.model.mark.Mark;
import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandMark extends FCommand {

    public CommandMark(FModule module, String name) {
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

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);

        if (cmdSettings.isConsole()) {
            sendErrorMessage(commandSender, getModule() + ".console");
            return true;
        }

        if (cmdSettings.isMuted()) {
            cmdSettings.getFPlayer().sendMutedMessage(command.getName());
            return true;
        }

        String color = args.length != 0 ? args[0].toUpperCase() : "WHITE";
        int range = commands.getInt(getName() + ".range");

        Mark.getMark(cmdSettings.getSender(), range, color).spawn();

        cmdSettings.getFPlayer().playSound(cmdSettings.getSender(), cmdSettings.getSender(), this.toString());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        tabCompleteClear();

        if (args.length == 1) {
            Mark.COLOR_VALUES.forEach(string -> isStartsWith(args[0], string));
        }

        return getSortedTabComplete();
    }
}
