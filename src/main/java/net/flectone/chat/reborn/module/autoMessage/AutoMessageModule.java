package net.flectone.chat.reborn.module.autoMessage;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.builder.MessageBuilder;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.model.player.Settings;
import net.flectone.chat.reborn.model.sound.FSound;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.sounds.SoundsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.PlayerUtil;
import net.flectone.chat.reborn.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoMessageModule extends FModule {

    private final HashMap<Player, Integer> MESSAGE_INDEX_MAP = new HashMap<>();
    private final HashMap<String, List<String>> MESSAGE_MAP = new HashMap<>();

    public AutoMessageModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new AutoMessageTicker(this));
    }

    public void send(@NotNull Player player) {
        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;
        if (hasNoPermission(player)) return;

        String autoMessage = fPlayer.getSettings().getValue(Settings.Type.AUTO_MESSAGE);
        boolean enabled = autoMessage == null || Integer.parseInt(autoMessage) != -1;
        if (!enabled) return;

        List<String> features = config.getVaultStringList(player, this + ".features");
        String message = incrementIndexAndGet(MESSAGE_MAP, MESSAGE_INDEX_MAP, player);

        Bukkit.getScheduler().runTaskAsynchronously(FlectoneChatReborn.getPlugin(), () -> {
            MessageBuilder messageBuilder = new MessageBuilder(player, null, message, features, true);

            player.spigot().sendMessage(messageBuilder.buildMessage(null, player, ""));

            FModule fModule = moduleManager.get(SoundsModule.class);
            if (fModule instanceof SoundsModule soundsModule) {
                soundsModule.play(new FSound(player, player, this.toString()));
            }
        });
    }

    @NotNull
    private String incrementIndexAndGet(@NotNull HashMap<String, List<String>> map, @NotNull HashMap<Player,
                                        Integer> indexMap, @NotNull Player player) {

        List<String> tabList = getMessageMap(map, player);

        if (tabList.isEmpty()) return "";

        Integer index = indexMap.get(player);
        if (index == null) index = 0;

        boolean isRandom = config.getVaultBoolean(player, this + ".random");

        if (isRandom) {
            index = RandomUtil.nextInt(0, tabList.size());
        } else {
            index++;
            index = index % tabList.size();
        }

        indexMap.put(player, index);

        return MessageUtil.formatAll(player, tabList.get(index));
    }

    private List<String> getMessageMap(HashMap<String, List<String>> map, Player player) {
        String playerGroup = PlayerUtil.getPrimaryGroup(player);
        List<String> messageList = map.get(playerGroup);
        if (messageList != null) return messageList;

        messageList = new ArrayList<>();

        List<String> tempList = locale.getVaultStringList(player, this + ".message");

        if (tempList.isEmpty()) {
            return messageList;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String tempString : tempList) {
            if (tempString.equals("<next>")) {
                messageList.add(stringBuilder.substring(0, stringBuilder.length() - 1));
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(tempString).append("\n");
            }
        }

        messageList.add(!stringBuilder.isEmpty() ? stringBuilder.substring(0, stringBuilder.length() - 1) : stringBuilder.toString());

        map.put(playerGroup, messageList);
        return messageList;
    }
}
