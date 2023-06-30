package ru.flectone.utils;

import ru.flectone.Main;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ObjectUtils {

    public static String convertTimeToString(long time){
        String timeoutSecondsString = String.valueOf((time - System.currentTimeMillis()) / 1000).substring(1);
        int timeoutSeconds = Integer.parseInt(timeoutSecondsString);

        int days = (timeoutSeconds / 86400);
        int hours = (timeoutSeconds / 3600) % 24;
        int minutes = (timeoutSeconds / 60) % 60;
        int seconds = timeoutSeconds % 60;

        String finalString = (days > 0 ? " " + days + Main.locale.getString("online.format.day") : "")
                + (hours > 0 ? " " + hours + Main.locale.getString("online.format.hour") : "")
                + (minutes > 0 ? " " + minutes + Main.locale.getString("online.format.minute") : "")
                + (seconds > 0 ? " " + seconds + Main.locale.getString("online.format.second") : "");

        return finalString.substring(1);
    }

    public static String toString(String[] strings){
        return toString(strings, 0);
    }

    public static String toString(String[] strings, int start){
        return Arrays.stream(strings, start, strings.length)
                .collect(Collectors.joining(" "));

    }
}
