package net.flectone.chat.reborn.module.server.brand;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.FTicker;
import net.flectone.chat.reborn.util.PlayerUtil;

public class BrandTicker extends FTicker {

    public BrandTicker(FModule module) {
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
                .filter(player -> !getModule().hasNoPermission(player))
                .filter(player -> getModule().isEnabledFor(player))
                .forEach(player ->
                        ((BrandModule) getModule()).updateBrand(player,
                                ((BrandModule) getModule()).incrementIndexAndGet(player)));
    }
}
