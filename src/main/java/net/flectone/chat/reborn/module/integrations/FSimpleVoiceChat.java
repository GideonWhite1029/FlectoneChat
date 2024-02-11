package net.flectone.chat.reborn.module.integrations;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.component.FComponent;
import net.flectone.chat.reborn.manager.FPlayerManager;
import net.flectone.chat.reborn.model.file.FConfiguration;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.TimeUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class FSimpleVoiceChat implements FIntegration, VoicechatPlugin {

    private FPlayerManager playerManager;
    private FConfiguration locale;

    public FSimpleVoiceChat() {
        init();
    }

    @Override
    public void init() {
        BukkitVoicechatService service = FlectoneChatReborn.getPlugin().getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) return;

        service.registerPlugin(this);

        FlectoneChatReborn.info("SimpleVoiceChat detected and hooked");

        FlectoneChatReborn plugin = FlectoneChatReborn.getPlugin();
        playerManager = plugin.getPlayerManager();
        locale = plugin.getFileManager().getLocale();
    }

    public static VoicechatApi voicechatApi;

    /**
     * @return the unique ID for this voice chat plugin
     */
    @NotNull
    @Override
    public String getPluginId() {
        return "FlectoneChat";
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(@NotNull VoicechatApi api) {
        voicechatApi = api;
    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(@NotNull EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacketEvent);
    }

    private void onMicrophonePacketEvent(@NotNull MicrophonePacketEvent event) {
        if (event.getSenderConnection() == null) return;

        Player player = (Player) event.getSenderConnection().getPlayer().getPlayer();
        FPlayer fPlayer = playerManager.get(player);
        if(fPlayer == null || !fPlayer.isMuted()) return;

        event.cancel();
        String formatMessage = locale.getVaultString(player, "commands.muted")
                .replace("<time>", TimeUtil.convertTime(player, fPlayer.getMute().getRemainingTime()))
                .replace("<reason>", fPlayer.getMute().getReason())
                .replace("<moderator>", fPlayer.getMute().getModeratorName());

        formatMessage = formatMessage.replace(System.lineSeparator(), "");
        formatMessage = MessageUtil.formatAll(player, formatMessage);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, FComponent.fromLegacyText(formatMessage));
    }
}
