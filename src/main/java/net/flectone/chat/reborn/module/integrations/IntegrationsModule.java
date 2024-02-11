package net.flectone.chat.reborn.module.integrations;

import me.clip.placeholderapi.PlaceholderAPI;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.model.advancement.FAdvancement;
import net.flectone.chat.reborn.model.damager.PlayerDamager;
import net.flectone.chat.reborn.model.file.FConfiguration;
import net.flectone.chat.reborn.model.player.Moderation;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class IntegrationsModule extends FModule {

    private static final HashMap<String, FIntegration> INTEGRATIONS_MAP = new HashMap<>();

    private FConfiguration integrations;

    public IntegrationsModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        integrations = FlectoneChatReborn.getPlugin().getFileManager().getIntegrations();

        if (isPluginEnabled("DiscordSRV")) {
            INTEGRATIONS_MAP.put("DiscordSRV", new FDiscordSRV());
        }
        if (isPluginEnabled("PlaceholderAPI")) {
            INTEGRATIONS_MAP.put("PlaceholderAPI", new FPlaceholderAPI());
        }
        if (isPluginEnabled("InteractiveChat")) {
            INTEGRATIONS_MAP.put("InteractiveChat", new FInteractiveChat());
        }
        if (isPluginEnabled("LuckPerms")) {
            INTEGRATIONS_MAP.put("LuckPerms", new FLuckPerms());
        }
        if (isPluginEnabled("SuperVanish") || isPluginEnabled("PremiumVanish")) {
            INTEGRATIONS_MAP.put("SuperVanish", new FSuperVanish());
        }
        if (isPluginEnabled("Vault")) {
            FVault fVault = new FVault();
            if (fVault.isEnabled()) {
                INTEGRATIONS_MAP.put("Vault", fVault);
            }
        }
        if (isPluginEnabled("PlasmoVoice")) {
            INTEGRATIONS_MAP.put("PlasmoVoice", new FPlasmoVoice());
        }
        if (isPluginEnabled("VoiceChat")) {
            INTEGRATIONS_MAP.put("VoiceChat", new FSimpleVoiceChat());
        }
        if (isPluginEnabled("SkinsRestorer")) {
            INTEGRATIONS_MAP.put("SkinsRestorer", new FSkinsRestorer());
        }
    }

    private static FIntegration get(String integration) {
        return INTEGRATIONS_MAP.get(integration);
    }

    private boolean isPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null
                && integrations.getBoolean(plugin + ".enable");
    }

    @Nullable
    public static String getTextureId(@NotNull Player player) {
        FIntegration skinsRestorer = get("SkinsRestorer");
        if (skinsRestorer != null) return ((FSkinsRestorer) skinsRestorer).getTextureId(player);
        return null;
    }

    public static boolean isVanished(@NotNull Player player) {
        return player.getMetadata("vanished").parallelStream()
                .anyMatch(MetadataValue::asBoolean);
    }

    @Nullable
    public static ArrayList<String> getGroups(@Nullable Player player) {
        if (player == null) return null;
        FIntegration luckPerms = get("LuckPerms");
        if (luckPerms != null) return ((FLuckPerms) luckPerms).getGroups(player);
        FIntegration vault = get("Vault");
        if (vault != null) {
            String vaultGroup = ((FVault) vault).getPrimaryGroup(player);
            if (vaultGroup == null) return null;
            return new ArrayList<>(List.of(vaultGroup));
        }
        return null;
    }

    public static boolean hasPermission(@NotNull OfflinePlayer player, @NotNull String permission) {
        FIntegration luckPerms = get("LuckPerms");
        if (luckPerms != null) return ((FLuckPerms) luckPerms).hasPermission(player, permission);
        FIntegration vault = get("Vault");
        if (vault != null) return ((FVault) vault).hasPermission(player, permission);
        return false;
    }

    public static int getPrimaryGroupWeight(@NotNull Player player) {
        FIntegration fIntegration = get("LuckPerms");
        if (fIntegration == null) return 0;
        return ((FLuckPerms) fIntegration).getPrimaryGroupWeight(player);
    }

    public static String interactiveChatMark(@NotNull String string, @NotNull UUID uuid) {
        FIntegration interactiveChat = get("InteractiveChat");
        if (interactiveChat == null) return string;
        return ((FInteractiveChat) interactiveChat).mark(string, uuid);
    }

    public static String interactiveChatCheckMention(@NotNull AsyncPlayerChatEvent event) {
        FIntegration interactiveChat = get("InteractiveChat");
        if (interactiveChat == null) return event.getMessage();
        return ((FInteractiveChat) interactiveChat).checkMention(event);
    }

    public static String replaceInteractiveChatPlaceholders(@NotNull String string) {
        FIntegration interactiveChat = get("InteractiveChat");
        if (interactiveChat == null) return string;

        return string.replaceAll("(<chat=.*>)", "[...]");
    }

    @Nullable
    public static String getPrefix(@NotNull Player player) {
        FIntegration luckPerms = get("LuckPerms");
        if (luckPerms != null) return ((FLuckPerms) luckPerms).getPrefix(player);
        FIntegration vault = get("Vault");
        if (vault != null) return ((FVault) vault).getPrefix(player);
        return null;
    }

    @Nullable
    public static String getSuffix(@NotNull Player player) {
        FIntegration luckPerms = get("LuckPerms");
        if (luckPerms != null) return ((FLuckPerms) luckPerms).getSuffix(player);
        FIntegration vault = get("Vault");
        if (vault != null) return ((FVault) vault).getSuffix(player);
        return null;
    }

    @Nullable
    public static String getPrimaryGroup(@NotNull Player player) {
        FIntegration luckPerms = get("LuckPerms");
        if (luckPerms != null) return ((FLuckPerms) luckPerms).getPrimaryGroup(player);
        FIntegration vault = get("Vault");
        if (vault != null) return ((FVault) vault).getPrimaryGroup(player);
        return null;
    }

    @NotNull
    public static String setPlaceholders(@Nullable OfflinePlayer sender, @Nullable OfflinePlayer recipient,
                                         @NotNull String string) {

        if (get("PlaceholderAPI") == null) return string;

        string = PlaceholderAPI.setPlaceholders(sender, string);

        if (!(sender instanceof Player pSender && recipient instanceof  Player pRecipient)) return string;

        return PlaceholderAPI.setRelationalPlaceholders(pSender, pRecipient, string);
    }

    public static void unregister() {
        FIntegration placeholderAPI = INTEGRATIONS_MAP.get("PlaceholderAPI");
        if (placeholderAPI != null) ((FPlaceholderAPI) placeholderAPI).unregister();

        FIntegration discordSRV = INTEGRATIONS_MAP.get("DiscordSRV");
        if (discordSRV != null) ((FDiscordSRV) discordSRV).unsubscribe();
    }

    public static void mutePlasmoVoice(@NotNull Player player, @Nullable UUID moderator, int time, @NotNull String reason) {
        FIntegration fIntegration = get("PlasmoVoice");
        if (fIntegration == null) return;
        ((FPlasmoVoice) fIntegration).mute(player, moderator, time, reason);
    }

    public static void unmutePlasmoVoice(@NotNull Player player) {
        FIntegration fIntegration = get("PlasmoVoice");
        if (fIntegration == null) return;
        ((FPlasmoVoice) fIntegration).unmute(player);
    }

    public static void sendDiscord(@NotNull Player sender, @NotNull String typeMessage, @NotNull Map<String, String> replacements) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendMessage(sender, typeMessage, replacements);
    }

    public static void sendDiscordAdvancement(@NotNull Player sender, @NotNull FAdvancement fAdvancement) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendAdvancementMessage(sender, fAdvancement);
    }

    public static void sendDiscordDeath(@NotNull Player sender, @NotNull PlayerDamager playerDamager, @NotNull String typeDeath) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendDeathMessage(sender, playerDamager, typeDeath);
    }

    public static void sendDiscordJoin(@NotNull Player sender, @NotNull String type) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendJoinMessage(sender, type);
    }

    public static void sendDiscordQuit(@NotNull Player sender, @NotNull String type) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendQuitMessage(sender, type);
    }

    public static void sendDiscordStream(@Nullable Player sender, @NotNull String urls) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendStreamMessage(sender, urls);
    }

    public static void sendDiscordBan(@Nullable OfflinePlayer sender, @NotNull Moderation ban) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendBanMessage(sender, ban);
    }

    public static void sendDiscordMute(@Nullable OfflinePlayer sender, @NotNull Moderation mute) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendMuteMessage(sender, mute);
    }

    public static void sendDiscordWarn(@Nullable OfflinePlayer sender, @NotNull Moderation warn, int count) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendWarnMessage(sender, warn, count);
    }

    public static void sendDiscordKick(@Nullable OfflinePlayer sender, @NotNull String reason,
                                       @NotNull String moderatorName) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendKickMessage(sender, reason, moderatorName);
    }

    public static void sendDiscordBroadcast(@Nullable OfflinePlayer sender, @NotNull String message) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendBroadcastMessage(sender, message);
    }

    public static void sendDiscordMaintenance(@Nullable OfflinePlayer sender, @NotNull String type) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendMaintenanceMessage(sender, type);
    }

    public static void sendDiscordPoll(@Nullable OfflinePlayer sender, @NotNull String message, int id) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendPollMessage(sender, message, id);
    }

    public static void sendDiscordMe(@Nullable OfflinePlayer sender, @NotNull String message) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendMeMessage(sender, message);
    }

    public static void sendDiscordTranslateto(@Nullable OfflinePlayer sender, @NotNull String targetLanguage,
                                              @NotNull String message) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendTranslatetoMessage(sender, targetLanguage, message);
    }

    public static void sendDiscordTry(@Nullable OfflinePlayer sender, @NotNull String message, @NotNull String percent,
                                      @NotNull String type) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendTryMessage(sender, message, percent, type);
    }

    public static void sendDiscordDice(@Nullable OfflinePlayer sender, @NotNull String cube, @NotNull String type) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendDiceMessage(sender, cube, type);
    }

    public static void sendDiscordBall(@Nullable OfflinePlayer sender, @NotNull String message, @NotNull String answer) {
        FIntegration fIntegration = get("DiscordSRV");
        if (fIntegration == null) return;
        ((FDiscordSRV) fIntegration).sendBallMessage(sender, message, answer);
    }
}
