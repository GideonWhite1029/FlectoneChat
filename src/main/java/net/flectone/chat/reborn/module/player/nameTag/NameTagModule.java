package net.flectone.chat.reborn.module.player.nameTag;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.module.player.name.NameModule;
import net.flectone.chat.reborn.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameTagModule extends FModule {

    private Scoreboard scoreboard;

    public NameTagModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        scoreboard = FlectoneChatReborn.getPlugin().getScoreBoard();
    }

    @NotNull
    public Team getTeam(@NotNull Player player) {
        String playerName = player.getName();
        String sortName = playerName;
        if (config.getVaultBoolean(player, this + ".sort.enable")) {
            sortName = getSortName(player);
        }

        Team bukkitTeam = scoreboard.getTeam(sortName);

        Team team = bukkitTeam != null ? bukkitTeam : scoreboard.registerNewTeam(sortName);

        if (!team.hasEntry(playerName)) {
            team.addEntry(playerName);
        }

        setVisibility(player, team);

        team.setColor(ChatColor.WHITE);

        updateTeam(player, team);

        return team;
    }

    public void updateTeam(@NotNull Player player, @NotNull Team team) {
        FModule fModule = moduleManager.get(NameModule.class);
        if (!(fModule instanceof NameModule nameModule)) return;

        if (config.getVaultBoolean(player, this + ".prefix.enable")) {
            team.setPrefix(nameModule.getPrefix(player));
        }

        if (config.getVaultBoolean(player, this + ".suffix.enable")) {
            team.setSuffix(nameModule.getSuffix(player));
        }
    }

    @NotNull
    public String getSortName(@NotNull Player player) {
        String playerName = player.getName();
        if (Bukkit.getBukkitVersion().startsWith("1.16.5")) return playerName;
        if (!config.getVaultBoolean(player, this + ".sort.enable")) return playerName;
        if (!isEnabledFor(player)) return playerName;
        if (hasNoPermission(player, "sort")) return playerName;

        int rank = IntegrationsModule.getPrimaryGroupWeight(player);

        return PlayerUtil.generateSortString(rank, player.getName());
    }

    public void setVisibility(@Nullable Player player, @Nullable Team team) {
        if (player == null) return;
        if (team == null) return;
        if (!isEnabledFor(player)) return;
        if (hasNoPermission(player, "visible")) return;

        boolean isVisible = config.getVaultBoolean(player, this + ".visible");

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, isVisible
                ? Team.OptionStatus.ALWAYS
                : Team.OptionStatus.NEVER);
    }
}
