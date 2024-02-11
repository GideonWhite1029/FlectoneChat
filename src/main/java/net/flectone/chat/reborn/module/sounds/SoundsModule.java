package net.flectone.chat.reborn.module.sounds;

import net.flectone.chat.reborn.model.sound.FSound;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class SoundsModule extends FModule {
    public SoundsModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();
    }

    public void play(@NotNull FSound sound) {
        if (!isEnabledFor(sound.getSender())) return;
        if (hasNoPermission(sound.getSender())) return;
        if (hasNoPermission(sound.getSender(), sound.getName())) return;
        sound.play();
    }
}
