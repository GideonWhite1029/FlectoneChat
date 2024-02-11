package net.flectone.chat.reborn.module.server.brand;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BrandModule extends FModule {

    private static final HashMap<Player, Integer> PLAYER_INDEX_MAP = new HashMap<>();
    private static final HashMap<String, List<String>> GROUP_BRAND_LIST = new HashMap<>();

    private static Field playerChannelsField;
    private static String channel;

    public BrandModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        try {
            Class.forName("org.bukkit.entity.Dolphin");
            channel = "minecraft:brand";
        } catch (ClassNotFoundException ignored) {
            channel = "MC|Brand";
        }

        try {
            Method registerMethod = FlectoneChatReborn.getPlugin().getServer().getMessenger().getClass()
                    .getDeclaredMethod("addToOutgoing", Plugin.class, String.class);
            registerMethod.setAccessible(true);
            registerMethod.invoke(FlectoneChatReborn.getPlugin().getServer().getMessenger(), FlectoneChatReborn.getPlugin(), channel);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error while attempting to register plugin message channel", e);
        }

        actionManager.add(new BrandListener(this));

        if (config.getBoolean("default." + this + ".update.enable")) {
            actionManager.add(new BrandTicker(this));
        }
    }

    public String incrementIndexAndGet(Player player) {
        String playerGroup = PlayerUtil.getPrimaryGroup(player);
        List<String> brandList = GROUP_BRAND_LIST.get(playerGroup);
        if (brandList == null) {
            brandList = locale.getVaultStringList(player, this + ".message");
            if (brandList.isEmpty()) return "";

            GROUP_BRAND_LIST.put(playerGroup, brandList);
        }

        Integer index = PLAYER_INDEX_MAP.get(player);
        if (index == null) index = 0;

        index++;
        index = index % brandList.size();
        PLAYER_INDEX_MAP.put(player, index);

        return MessageUtil.formatAll(player, brandList.get(index));
    }

    public void setBrand(@NotNull Player player, @NotNull String message) {
        if (playerChannelsField == null) {
            try {
                playerChannelsField = player.getClass().getDeclaredField("channels");
                playerChannelsField.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        try {
            Set<String> channels = (Set<String>) playerChannelsField.get(player);
            channels.add(channel);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        updateBrand(player, message);
    }

    public void updateBrand(@NotNull Player player, @NotNull String brandString) {
        try {
            player.sendPluginMessage(FlectoneChatReborn.getPlugin(), channel,
                    new PacketSerializer(brandString + ChatColor.RESET).toArray());
        } catch (Throwable e) {
            FlectoneChatReborn.warning("Failed to send a custom server brand. If the error is repeated, disable the server.brand module");
            e.printStackTrace();
        }
    }
}
