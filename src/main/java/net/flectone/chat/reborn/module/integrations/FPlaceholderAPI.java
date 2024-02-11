package net.flectone.chat.reborn.module.integrations;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.database.sqlite.Database;
import net.flectone.chat.reborn.manager.FModuleManager;
import net.flectone.chat.reborn.manager.FPlayerManager;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.player.name.NameModule;
import net.flectone.chat.reborn.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FPlaceholderAPI extends PlaceholderExpansion implements FIntegration {

    private FPlayerManager playerManager;
    private FModuleManager moduleManager;
    private Database database;

    public FPlaceholderAPI() {
        init();
    }

    @Override
    public void init() {
        register();
        FlectoneChatReborn.info("PlaceholderAPI detected and hooked");

        FlectoneChatReborn plugin = FlectoneChatReborn.getPlugin();
        playerManager = plugin.getPlayerManager();
        moduleManager = plugin.getModuleManager();
        database = plugin.getDatabase();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "FlectoneChatReborn";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TheFaser, fxd, GideonWhite1029";
    }

    @Override
    public @NotNull String getVersion() {
        return FlectoneChatReborn.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        if (player == null) return null;

        FPlayer fPlayer = playerManager.getOffline(player.getName());
        if(fPlayer == null) return null;

        if (params.startsWith("player")) {
            FModule fModule = moduleManager.get(NameModule.class);
            if (!(fModule instanceof NameModule nameModule)) return null;
            Player onlinePlayer = fPlayer.getPlayer();
            if (onlinePlayer == null) return null;

            return switch (params.toLowerCase()) {
                case "player_name_real" -> nameModule.getReal(onlinePlayer);
                case "player_name_display" -> nameModule.getDisplay(onlinePlayer);
                case "player_name_tab" -> nameModule.getTab(onlinePlayer);
                case "player_suffix" -> nameModule.getSuffix(onlinePlayer);
                case "player_prefix" -> nameModule.getPrefix(onlinePlayer);
                default -> null;
            };
        }

        if (params.startsWith("moderation")) {
            database.getWarns(fPlayer);

            return switch (params.toLowerCase()) {
                case "moderation_ban" -> playerManager.getBannedPlayers().contains(fPlayer.getUuid()) ? "1" : "0";
                case "moderation_mute" -> playerManager.getMutedPlayers().contains(fPlayer.getMinecraftName()) ? "1" : "0";
                case "moderation_warn" -> String.valueOf(fPlayer.getCountWarns());
                default -> null;
            };
        }

        return switch (params.toLowerCase()) {
            case "lastonline" -> TimeUtil.convertTime(fPlayer.getPlayer(), player.getLastPlayed());
            case "firstonline" -> TimeUtil.convertTime(fPlayer.getPlayer(), player.getFirstPlayed());
            case "stream_prefix" -> fPlayer.getStreamPrefix();
            case "afk_suffix" -> fPlayer.getAfkSuffix();
            case "world_prefix" -> fPlayer.getWorldPrefix();
            default -> null;
        };
    }
}
