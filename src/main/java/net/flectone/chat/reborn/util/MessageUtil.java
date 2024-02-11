package net.flectone.chat.reborn.util;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.model.file.FConfiguration;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.color.ColorModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.module.player.name.NameModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageUtil {

    @NotNull
    public static String formatAll(@Nullable Player sender, @Nullable Player recipient, @NotNull String string) {
        return formatAll(sender, recipient, string, false);
    }

    @NotNull
    public static String formatAll(@Nullable Player sender, @NotNull String string) {
        return formatAll(sender, sender, string, false);
    }

    @NotNull
    public static String formatAll(@Nullable Player sender, @Nullable Player recipient, @NotNull String string, boolean needPapiPerm) {
        HashMap<String, String> colors = null;

        if (sender != null && recipient != null) {
            string = formatPAPI(sender, recipient, string, needPapiPerm);

            FModule fModule = FlectoneChatReborn.getPlugin().getModuleManager().get(ColorModule.class);
            if (fModule instanceof ColorModule colorModule) {
                colors = colorModule.getColors(recipient);
            }
        }

        if (colors == null) {
            colors = new HashMap<>();

            FConfiguration config = FlectoneChatReborn.getPlugin().getFileManager().getConfig();

            List<String> colorsKey = config.getCustomList(recipient, "color.list");

            for (String colorKey : colorsKey) {
                colors.put(colorKey, config.getVaultString(recipient, "color.list." + colorKey));
            }
        }

        for (Map.Entry<String, String> entry : colors.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }

        return IridiumColorAPI.process(ColorUtil.translateHexToColor(string));
    }

    @NotNull
    public static String formatPAPI(@Nullable Player sender, @NotNull Player recipient, @NotNull String string, boolean needPermission) {
        if (sender != null && !sender.hasPermission("flectonechatreborn.placeholders") && needPermission) return string;
        return IntegrationsModule.setPlaceholders(sender, recipient, string);
    }

    @NotNull
    public static String formatPlayerString(@Nullable CommandSender commandSender, @NotNull String string) {
        if (!(commandSender instanceof Player player)
                || FlectoneChatReborn.getPlugin().getPlayerManager().get(player) == null) {
            String commandSenderName = commandSender == null ? "CONSOLE" : commandSender.getName();
            return string
                    .replace("<player_name_tab>", commandSenderName)
                    .replace("<player_name_real>", commandSenderName)
                    .replace("<player_name_display>", commandSenderName)
                    .replace("<player_prefix>", "")
                    .replace("<player_suffix>", "")
                    .replace("<vault_prefix>", "")
                    .replace("<world_prefix>", "")
                    .replace("<stream_prefix>", "")
                    .replace("<player>", commandSenderName)
                    .replace("<vault_suffix>", "")
                    .replace("<afk_suffix>", "");
        }

        FModule fModule = FlectoneChatReborn.getPlugin().getModuleManager().get(NameModule.class);
        if (fModule instanceof NameModule nameModule) {
            if (string.contains("<player_name_tab>")) {
                string = string.replace("<player_name_tab>", nameModule.getTab(player));
            }

            if (string.contains("<player_name_real>")) {
                string = string.replace("<player_name_real>", nameModule.getReal(player));
            }

            if (string.contains("<player_name_display>")) {
                string = string.replace("<player_name_display>", nameModule.getDisplay(player));
            }

            if (string.contains("<player_prefix>")) {
                string = string.replace("<player_prefix>", nameModule.getPrefix(player));
            }

            if (string.contains("<player_suffix>")) {
                string = string.replace("<player_suffix>", nameModule.getSuffix(player));
            }
        }

        if (isInvisible(player)) {
            String invisibleName = FlectoneChatReborn.getPlugin().getFileManager().getLocale().getVaultString(player, "player.name.invisible");

            return string
                    .replace("<player_prefix>", "")
                    .replace("<player_suffix>", "")
                    .replace("<vault_prefix>", "")
                    .replace("<world_prefix>", "")
                    .replace("<stream_prefix>", "")
                    .replace("<player>", invisibleName)
                    .replace("<vault_suffix>", "")
                    .replace("<afk_suffix>", "");
        }

        FPlayer fPlayer = FlectoneChatReborn.getPlugin().getPlayerManager().get(player);

        return string
                .replace("<vault_prefix>", PlayerUtil.getPrefix(player))
                .replace("<world_prefix>", fPlayer.getWorldPrefix())
                .replace("<stream_prefix>", fPlayer.getStreamPrefix())
                .replace("<player>", fPlayer.getMinecraftName())
                .replace("<vault_suffix>", PlayerUtil.getSuffix(player))
                .replace("<afk_suffix>", fPlayer.getAfkSuffix());
    }

    public static String joinArray(@NotNull String[] strings, int start, @NotNull String delimiter) {
        return Arrays.stream(strings, start, strings.length)
                .collect(Collectors.joining(delimiter));
    }

    public static boolean isInvisible(@NotNull Player player) {
        return FlectoneChatReborn.getPlugin().getFileManager().getConfig().getVaultBoolean(player, "player.name.hide-invisible")
                && player.hasPotionEffect(PotionEffectType.INVISIBILITY)
                && player.hasPermission("flectonechatreborn.player.name.invisible");
    }
}
