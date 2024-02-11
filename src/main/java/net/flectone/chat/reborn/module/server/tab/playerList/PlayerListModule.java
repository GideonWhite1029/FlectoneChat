package net.flectone.chat.reborn.module.server.tab.playerList;

import net.flectone.chat.reborn.module.FModule;

public class PlayerListModule extends FModule {
    public PlayerListModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;

        actionManager.add(new PlayerListTicker(this));
    }
}
