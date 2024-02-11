package net.flectone.chat.reborn.module.extra.spit;

import net.flectone.chat.reborn.model.spit.Spit;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.entity.Player;


public class SpitModule extends FModule {

    public SpitModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new SpitListener(this));
    }

    public void spit(Player player) {
        new Spit(player, this.toString()).spawn();
    }
}
