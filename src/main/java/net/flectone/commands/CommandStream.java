package net.flectone.commands;

import net.flectone.integrations.discordsrv.FDiscordSRV;
import net.flectone.managers.HookManager;
import net.flectone.misc.commands.FCommand;
import net.flectone.misc.commands.FTabCompleter;
import net.flectone.utils.ObjectUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.flectone.managers.FileManager.locale;

public class CommandStream implements FTabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        FCommand fCommand = new FCommand(commandSender, command.getName(), s, strings);

        if (strings.length < 1 || !strings[0].equalsIgnoreCase("start") && !strings[0].equalsIgnoreCase("end")) {
            fCommand.sendUsageMessage();
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("start")) {
            fCommand.sendMeMessage("command.stream.start.need-url");
            return true;
        }

        if (!fCommand.isConsole()) {
            if (fCommand.getFPlayer() != null && !fCommand.getFPlayer().isStreaming() && strings[0].equalsIgnoreCase("end")) {
                fCommand.sendMeMessage("command.stream.end.not");
                return true;
            }

            if (fCommand.getFPlayer().isStreaming() && strings[0].equalsIgnoreCase("start")) {
                fCommand.sendMeMessage("command.stream.start.already");
                return true;
            }

            if (strings[0].equalsIgnoreCase("end")) {
                fCommand.getFPlayer().setStreaming(false);
                fCommand.getFPlayer().setDisplayName();
                fCommand.sendMeMessage("command.stream.end.message");
                return true;
            }

            if (fCommand.isHaveCD() || fCommand.isMuted()) return true;

            fCommand.getFPlayer().setStreaming(true);
        }

        StringBuilder stringBuilder = new StringBuilder();

        locale.getStringList("command.stream.start.message").parallelStream()
                .forEachOrdered(string -> {

                    string = string
                            .replace("<player>", fCommand.getSenderName())
                            .replace("<links>", ObjectUtil.toString(strings, 1, "\n") + " ");

                    stringBuilder.append(string);
                    stringBuilder.append("\n");

                });

        if (HookManager.enabledDiscordSRV) {
            FDiscordSRV.sendModerationMessage(stringBuilder.toString());
        }

        fCommand.sendGlobalMessage(stringBuilder.toString());

        if(fCommand.getFPlayer() == null) return true;

        fCommand.getFPlayer().setDisplayName();

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        wordsList.clear();

        switch (strings.length) {
            case 1 -> {
                isStartsWith(strings[0], "start");
                isStartsWith(strings[0], "end");
            }
            default -> {
                if (!strings[0].equalsIgnoreCase("start")) break;

                isStartsWith(strings[1], "https://flectone.net");
            }
        }

        Collections.sort(wordsList);

        return wordsList;
    }

    @NotNull
    @Override
    public String getCommandName() {
        return "stream";
    }
}
