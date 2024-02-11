package net.flectone.chat.reborn.module.server.tab.playerList;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.FTicker;
import net.flectone.chat.reborn.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerListTicker extends FTicker {

    public PlayerListTicker(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        super.period = 20L;
        registerPingObjective();
        runTaskTimer();
    }

    public void registerPingObjective() {
        Scoreboard scoreboard = FlectoneChatReborn.getPlugin().getScoreBoard();
        if (scoreboard.getObjective("playerList") != null) return;
        Objective objective = scoreboard.registerNewObjective("playerList", "dummy", "FlectoneChat");
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    public void unregisterPingObjective() {
        Objective objective = FlectoneChatReborn.getPlugin().getScoreBoard().getObjective("playerList");
        if (objective == null) return;
        objective.unregister();
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Objective objective = FlectoneChatReborn.getPlugin().getScoreBoard().getObjective("playerList");
            if (objective == null) return;
            Score score = objective.getScore(player.getName());

            String mode = config.getVaultString(player, getModule() + ".mode");
            score.setScore(PlayerUtil.getObjectiveScore(player, mode));
        });
    }

    @Override
    public void cancel() {
        unregisterPingObjective();
        super.cancel();
    }
}