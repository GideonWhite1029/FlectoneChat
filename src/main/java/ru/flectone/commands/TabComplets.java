package ru.flectone.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.flectone.Main;
import ru.flectone.custom.FCommands;
import ru.flectone.utils.FileResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplets implements TabCompleter {

    public static final String[] chatColorValues = {"BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> wordsList = new ArrayList<>();
        String commandName = command.getName().replace(" ", "");

        //command
        switch(commandName){
            case "chatcolor":
                if(args.length == 1){
                    isStartsWith(args[0], "default", wordsList);
                    isStartsWith(args[0], "#1abaf0", wordsList);
                    isStartsWith(args[0], "&b", wordsList);
                } else if(args.length == 2){
                    isStartsWith(args[1], "#77d7f7", wordsList);
                    isStartsWith(args[1], "&f", wordsList);
                }
                break;

            case "mail-clear":
                if(args.length == 1){
                    for(OfflinePlayer player : Bukkit.getOfflinePlayers()){
                        isStartsWith(args[0], player.getName(), wordsList);
                    }
                } else if(sender instanceof Player && args.length == 2 && FCommands.isRealOfflinePlayer(args[0])){
                    String key = Bukkit.getOfflinePlayer(args[0]).getUniqueId() + "." + ((Player) sender).getPlayer().getUniqueId();
                    List<String> list = Main.mails.getStringList(key);
                    for(int x = 0; x < list.size(); x++){
                        isStartsWith(args[1], String.valueOf(x + 1), wordsList);
                    }
                }
                break;

            case "mail":
                if(args.length == 2) {
                    isStartsWith(args[1], "(message)", wordsList);
                }
            case "ignore":
            case "firstonline":
            case "lastonline":
                if(args.length == 1){
                    for(OfflinePlayer player : Bukkit.getOfflinePlayers()){
                        isStartsWith(args[0], player.getName(), wordsList);
                    }
                }
                break;

            case "msg":{
                if(args.length == 2) {
                    isStartsWith(args[1], "(message)", wordsList);
                }
            }
            case "ping":
                if(args.length == 1){
                    for(Player player : Bukkit.getOnlinePlayers()){
                        isStartsWith(args[0], player.getName(), wordsList);
                    }
                }
                break;

            case "reply":
            case "me":
            case "try":
                if(args.length == 1){
                    isStartsWith(args[0], "(message)", wordsList);
                }
                break;

            case "try-cube":
                for(int x = 1; x <= Main.getInstance().getConfig().getInt("try-cube.max_amount"); x++){
                    isStartsWith(args[0], String.valueOf(x), wordsList);
                }
                break;
            case "mark":
                if(args.length == 1){
                    for(String color : chatColorValues){
                        isStartsWith(args[0], color, wordsList);
                    }
                }
                break;

            case "stream":
                if(args.length == 1){
                    isStartsWith(args[0], "start", wordsList);
                    isStartsWith(args[0], "end", wordsList);
                } else if(args.length == 2 && args[0].equalsIgnoreCase("start")){
                    isStartsWith(args[1], "https://flectone.net", wordsList);
                }
                break;

            case "flectonechat":
                if(args.length == 1){
                    isStartsWith(args[0], "reload", wordsList);
                    isStartsWith(args[0], "config", wordsList);
                    isStartsWith(args[0], "locale", wordsList);
                } else if(args.length == 2){

                    if(args[0].equalsIgnoreCase("config")){
                        addKeysFile(Main.config, wordsList, args[1]);
                    }
                    if(args[0].equalsIgnoreCase("locale")){
                        addKeysFile(Main.locale, wordsList, args[1]);
                    }

                } else if(args.length == 3) {
                    isStartsWith(args[2], "set", wordsList);
                } else if(args.length == 4){
                    isStartsWith(args[3], "string", wordsList);
                    isStartsWith(args[3], "integer", wordsList);
                    isStartsWith(args[3], "boolean", wordsList);
                }
                break;
        }
        Collections.sort(wordsList);
        return wordsList;
    }

    private void isStartsWith(String arg, String string, List<String> wordsList){
        if(string.toLowerCase().startsWith(arg.toLowerCase()) || arg.replace(" ", "").isEmpty()){
            wordsList.add(string);
        }
    }

    private void addKeysFile(FileResource fileResource, List<String> wordsList, String arg){
        for(String key : fileResource.getKeys()){

            if(fileResource.getString(key).contains("root='YamlConfiguration'")) continue;

            isStartsWith(arg, key, wordsList);
        }
    }
}
