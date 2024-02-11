package net.flectone.chat.reborn.module.server.tab;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.FTicker;
import net.flectone.chat.reborn.util.PlayerUtil;

public class TabTicker extends FTicker {

    public TabTicker(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        super.period = config.getInt("default." + getModule() + ".update.rate");
        runTaskTimer();
    }

    @Override
    public void run() {
        PlayerUtil.getPlayersWithFeature(getModule() + ".enable")
                .stream()
                .filter(player -> getModule().isEnabledFor(player))
                .forEach(player -> ((TabModule) getModule()).update(player));
    }
}
