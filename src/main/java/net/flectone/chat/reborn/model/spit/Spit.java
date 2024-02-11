package net.flectone.chat.reborn.model.spit;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.manager.FPlayerManager;
import net.flectone.chat.reborn.model.player.FPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Spit {


    private final World world;
    private final Location location;
    private final Player player;
    private final String action;

    private final FPlayerManager playerManager;

    public Spit(@NotNull Player player, @NotNull String action) {
        this.location = player.getEyeLocation();
        this.world = player.getWorld();
        this.player = player;
        this.action = action;

        location.setY(location.getY() - 0.3);

        playerManager = FlectoneChatReborn.getPlugin().getPlayerManager();
    }

    public void spawn() {
        LlamaSpit spit = (LlamaSpit) world.spawnEntity(location, EntityType.LLAMA_SPIT);
        spit.setVelocity(location.getDirection());
        spit.setShooter(player);

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        fPlayer.playSound(location, action);
    }

}
