package net.flectone.chat.reborn.module.server;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.server.brand.BrandModule;
import net.flectone.chat.reborn.module.server.status.StatusModule;
import net.flectone.chat.reborn.module.server.tab.TabModule;

public class ServerModule extends FModule {

    public ServerModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        new BrandModule(this, "brand");
        new StatusModule(this, "status");
        new TabModule(this, "tab");
    }
}
