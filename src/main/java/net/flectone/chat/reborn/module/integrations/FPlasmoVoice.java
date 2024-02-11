package net.flectone.chat.reborn.module.integrations;

import com.google.inject.Inject;
import net.flectone.chat.reborn.FlectoneChatReborn;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.AddonLoaderScope;
import su.plo.voice.api.addon.ServerAddonsLoader;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.mute.MuteDurationUnit;

import java.util.UUID;

@Addon(id = "flectonechat", scope = AddonLoaderScope.SERVER, version = "1.0.0", authors = {"TheFaser, fxd"})
public class FPlasmoVoice implements FIntegration, AddonInitializer {

    @Inject
    private PlasmoVoiceServer voiceServer;

    public FPlasmoVoice() {
        init();
    }

    public void mute(@NotNull Player player, @Nullable UUID moderator, int time, @NotNull String reason) {
        if (voiceServer == null) return;

        voiceServer.getMuteManager().mute(player.getUniqueId(), moderator, time, MuteDurationUnit.SECOND, reason, true);
    }

    public void unmute(@NotNull Player player) {
        if (voiceServer == null) return;

        voiceServer.getMuteManager().unmute(player.getUniqueId(), true);
    }

    @Override
    public void init() {
        ServerAddonsLoader.INSTANCE.load(this);
    }

    @Override
    public void onAddonInitialize() {
        FlectoneChatReborn.info("PlasmoVoice detected and hooked");
    }
}
