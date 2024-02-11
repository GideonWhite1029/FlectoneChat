package net.flectone.chat.reborn.listener;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.FTicker;

import java.util.HashMap;

public class FPlayerTicker extends FTicker {

    public FPlayerTicker(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        super.delay = 20 * 60 * 60;
        super.period = delay;
        runTaskTimer();
    }

    @Override
    public void run() {
        FlectoneChatReborn.getPlugin().getDatabase().execute(() ->
                FlectoneChatReborn.getPlugin().getDatabase().clearExpiredData());

        var temp = new HashMap<>(playerManager.getPlayerHashMap());

        playerManager.getPlayerHashMap().forEach((key, fPlayer) -> {
            if (fPlayer == null || !fPlayer.getOfflinePlayer().isOnline()) {
                temp.remove(key);
            }
        });

        playerManager.getPlayerHashMap().clear();
        playerManager.getPlayerHashMap().putAll(temp);
    }
}
