package net.flectone.chat.reborn.util;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.model.file.FConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeUtil {

    public static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static String convertTime(@Nullable Player player, long time) {
        String timeInSeconds = String.valueOf((time- System.currentTimeMillis()) / 1000).substring(1);
        return convertTime(player, Integer.parseInt(timeInSeconds));
    }

    @NotNull
    public static String convertTime(@Nullable Player player, int timeInSeconds) {
        if (timeInSeconds < 0) return "";

        int days = (timeInSeconds / 86400);
        int hours = (timeInSeconds / 3600) % 24;
        int minutes = (timeInSeconds / 60) % 60;
        int seconds = timeInSeconds % 60;

        FConfiguration locale = FlectoneChatReborn.getPlugin().getFileManager().getLocale();

        StringBuilder stringBuilder = new StringBuilder();

        if (days > 0) {
            stringBuilder
                    .append(" ")
                    .append(days)
                    .append(locale.getVaultString(player,"commands.format.day"));
        }
        if (hours > 0) {
            stringBuilder
                    .append(" ")
                    .append(hours)
                    .append(locale.getVaultString(player,"commands.format.hour"));
        }
        if (minutes > 0) {
            stringBuilder
                    .append(" ")
                    .append(minutes)
                    .append(locale.getVaultString(player,"commands.format.minute"));
        }
        if (seconds > 0) {
            stringBuilder
                    .append(" ")
                    .append(seconds)
                    .append(locale.getVaultString(player,"commands.format.second"));
        }

        return stringBuilder.substring(1);
    }

}
