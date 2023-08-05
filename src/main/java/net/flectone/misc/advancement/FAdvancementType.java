package net.flectone.misc.advancement;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum FAdvancementType {
    UNKNOWN,
    TASK,
    GOAL,
    CHALLENGE;

    public static FAdvancementType getType(@Nullable String name) {
        if (name == null) return UNKNOWN;

        return Arrays.stream(values()).parallel()
                .filter(type -> type.toString().equalsIgnoreCase(name))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
