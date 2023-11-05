package net.flectone.chat.module.serverMessage.quit;

import net.flectone.chat.builder.FComponentBuilder;
import net.flectone.chat.component.FPlayerComponent;
import net.flectone.chat.manager.FActionManager;
import net.flectone.chat.manager.FPlayerManager;
import net.flectone.chat.model.player.FPlayer;
import net.flectone.chat.model.player.Settings;
import net.flectone.chat.module.FModule;
import net.flectone.chat.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuitModule extends FModule {
    public QuitModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        FActionManager.add(new QuitListener(this));
    }

    public void sendAll(@NotNull Player sender, @NotNull String message) {
        PlayerUtil.getPlayersWithFeature(this + ".enable").forEach(player -> {

            FPlayer fPlayer = FPlayerManager.get(player);
            if (fPlayer == null) return;

            String quit = fPlayer.getSettings().getValue(Settings.Type.QUIT);
            boolean enabled = quit == null || Integer.parseInt(quit) != -1;
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
