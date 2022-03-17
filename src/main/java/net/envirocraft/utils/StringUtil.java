package net.envirocraft.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class StringUtil {

    public static @NotNull String capitalize(@Nullable String input) {
        if (input == null) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase(Locale.ROOT);
    }
}
