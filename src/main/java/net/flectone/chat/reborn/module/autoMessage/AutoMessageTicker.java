package net.flectone.chat.reborn.module.autoMessage;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.FTicker;
import net.flectone.chat.reborn.util.PlayerUtil;

public class AutoMessageTicker extends FTicker {

    public AutoMessageTicker(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        super.period = config.getInt("default." + getModule() + ".period");
        super.delay = period;
        runTaskTimer();
    }

    @Override
    public void run() {
        PlayerUtil.getPlayersWithFeature(getModule() + ".enable")
                .stream()
                .filter(player -> getModule().isEnabledFor(player))
                .forEach(player -> ((AutoMessageModule) getModule()).send(player));
    }
}
