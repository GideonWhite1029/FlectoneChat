package net.flectone.chat.reborn.module.playerMessage.sign;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.builder.MessageBuilder;
import net.flectone.chat.reborn.manager.FPlayerManager;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.commands.CommandSpy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignListener extends FListener {

    private FPlayerManager playerManager;

    public SignListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();

        playerManager = FlectoneChatReborn.getPlugin().getPlayerManager();
    }

    @EventHandler
    public void signChangeEvent(@NotNull SignChangeEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("sign");
            event.setCancelled(true);
        }

        if (fPlayer.isHaveCooldown(getModule().toString())) {
            fPlayer.sendCDMessage("sign", "sign");
            event.setCancelled(true);
            return;
        }

        fPlayer.playSound(getModule().toString());

        List<String> features = config.getVaultStringList(player, getModule() + ".features");
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        for (int x = 0; x < event.getLines().length; x++) {
            String string = event.getLine(x);

            if (string == null || string.isEmpty()) continue;


            MessageBuilder messageBuilder = new MessageBuilder(player, itemInHand, string, features);
            event.setLine(x, messageBuilder.getMessage(""));

            CommandSpy.send(player, "sign", new ArrayList<>(), CommandSpy.Type.DEFAULT,
                    messageBuilder.getMessage(""));
        }
    }
}
