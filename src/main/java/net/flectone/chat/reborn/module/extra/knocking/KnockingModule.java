package net.flectone.chat.reborn.module.extra.knocking;

import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KnockingModule extends FModule {

    public KnockingModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new KnockingListener(this));
    }

    public void knock(@NotNull Player player, @NotNull Location location, @NotNull String name)  {
        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        fPlayer.playSound(location, name);
    }
}
