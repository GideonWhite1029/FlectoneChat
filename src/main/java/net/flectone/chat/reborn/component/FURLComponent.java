package net.flectone.chat.reborn.component;

import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FURLComponent extends FComponent{

    public FURLComponent(@Nullable Player sender, @NotNull Player recipient, @NotNull String text, @NotNull String url) {
        super(text);

        if (config.getVaultBoolean(sender, "player-message.formatting.list.url.clickable")) {
            addOpenURL(url);
        }

        if (config.getVaultBoolean(sender, "player-message.formatting.list.url.hover.enable")) {
            String showText = locale.getVaultString(sender, "player-message.formatting.list.url.hover.message");
            showText = MessageUtil.formatAll(sender, recipient, showText);
            addHoverText(showText);
        }
    }
}
