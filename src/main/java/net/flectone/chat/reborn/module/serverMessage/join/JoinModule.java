package net.flectone.chat.reborn.module.serverMessage.join;

import net.flectone.chat.reborn.builder.FComponentBuilder;
import net.flectone.chat.reborn.component.FPlayerComponent;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.model.player.Settings;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JoinModule extends FModule {

    public JoinModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new JoinListener(this));
    }

    public void sendAll(@NotNull Player sender, @NotNull String message) {
        PlayerUtil.getPlayersWithFeature(this + ".enable")
                .stream()
                .filter(this::isEnabledFor)
                .forEach(player -> {
                    FPlayer fPlayer = playerManager.get(player);
                    if (fPlayer == null) return;
                    if (fPlayer.getSettings() == null) return;

                    String join = fPlayer.getSettings().getValue(Settings.Type.JOIN);
                    boolean enabled = join == null || Integer.parseInt(join) != -1;
                    if (!enabled) return;

                    if (fPlayer.getIgnoreList().contains(sender.getUniqueId())) return;

                    sendMessage(sender, player, message);

                    fPlayer.playSound(sender, player, this.toString());
                });

        sendMessage(sender, null, message);
    }

    public void sendMessage(@NotNull Player sender, @Nullable Player player, @NotNull String message) {
        FComponentBuilder fComponentBuilder = new FComponentBuilder(message);

        fComponentBuilder.replace("<player>", (componentBuilder, color) ->
                componentBuilder.append(new FPlayerComponent(sender, player, color + sender.getName()).get()));

        if (player != null) {
            player.spigot().sendMessage(fComponentBuilder.build(sender, player));
        } else {
            FPlayer.sendToConsole(fComponentBuilder.build(sender, null));
        }
    }
}
