package ru.flectone.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.flectone.custom.FCommands;

import java.util.List;

public class CommandIgnore implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        FCommands fCommand = new FCommands(commandSender, command.getName(), s, strings);

        if(fCommand.isConsoleMessage()) return true;

        if(fCommand.checkCountArgs(1)) return true;

        if(fCommand.isYourselfCommand()){
            fCommand.sendMeMessage("ignore.myself");
            return true;
        }

        if(!fCommand.isRealOfflinePlayer(strings[0])){
            fCommand.sendMeMessage("ignore.no_player");
            return true;
        }

        String playerName = strings[0];
        OfflinePlayer ignoredPlayer = Bukkit.getOfflinePlayer(playerName);

        List<String> ignoreList = fCommand.getFPlayer().getIgnoreList();
        String ignoredPlayerUUID = ignoredPlayer.getUniqueId().toString();

        if(ignoreList.contains(ignoredPlayerUUID)){
            fCommand.sendMeMessage("ignore.success_unignore", "<player>", ignoredPlayer.getName());
            ignoreList.remove(ignoredPlayerUUID);
        } else {
            fCommand.sendMeMessage("ignore.success_ignore", "<player>", ignoredPlayer.getName());
            ignoreList.add(ignoredPlayerUUID);
        }

        fCommand.getFPlayer().saveIgnoreList(ignoreList);

        return true;
    }
}
