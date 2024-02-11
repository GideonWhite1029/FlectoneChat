package net.flectone.chat.reborn.module.extra.itemSign;

import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.model.player.FPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ItemUnsignListener extends FListener {

    public ItemUnsignListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void unsignEvent(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getLocation().getWorld() == null) return;

        Player player = event.getPlayer();
        if (!config.getVaultBoolean(player, getModule() + ".unsign.enable")) return;
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player, "unsign")) return;

        String configBlock = config.getVaultString(player, getModule() + ".unsign.block");
        if (!clickedBlock.getType().toString().equalsIgnoreCase(configBlock)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("unsign");
            return;
        }

        if (fPlayer.isHaveCooldown(getModule() + ".unsign")) {
            fPlayer.sendCDMessage("unsign", "unsign");
            return;
        }

        boolean dropDyeEnabled = config.getVaultBoolean(player, getModule() + ".unsign.drop-dye");
        boolean isCompleted = ((ItemSignModule) getModule()).unsign(player, clickedBlock.getLocation(),
                player.getInventory(), dropDyeEnabled);

        event.setCancelled(isCompleted);

        if (isCompleted) fPlayer.playSound(getModule() + ".unsign");
    }
}
