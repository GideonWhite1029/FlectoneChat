package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.component.FComponent;
import net.flectone.chat.reborn.manager.PollManager;
import net.flectone.chat.reborn.model.poll.Poll;
import net.flectone.chat.reborn.model.sound.FSound;
import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.module.sounds.SoundsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandPoll extends FCommand {

    private static final String CREATE_PERMISSION = "flectonechatreborn.commands.poll.create";

    public CommandPoll(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias,
                             @NotNull String[] args) {

        if (args.length < 2) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        String trigger = args[0].toLowerCase();
        if (!trigger.equals("vote") && (!trigger.equals("create") || !commandSender.hasPermission(CREATE_PERMISSION))) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);

        if (cmdSettings.isHaveCooldown()) {
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return true;
        }

        if (cmdSettings.isMuted()) {
            cmdSettings.getFPlayer().sendMutedMessage(command.getName());
            return true;
        }

        if (trigger.equals("create")) {

            Poll poll = new Poll(cmdSettings.getSender(), MessageUtil.joinArray(args, 1, " "));

            String formatString = locale.getVaultString(commandSender, this + ".message")
                    .replace("<id>", String.valueOf(poll.getId()));

            sendGlobalMessage(cmdSettings.getSender(), cmdSettings.getItemStack(), formatString, poll.getMessage(), false);

            String voteId = String.valueOf(poll.getId());

            Bukkit.getOnlinePlayers().parallelStream()
                    .forEach(player -> {

                        ComponentBuilder componentBuilder = new ComponentBuilder();

                        String agreeString = locale.getVaultString(commandSender, this + ".format.agree");
                        agreeString = MessageUtil.formatAll(cmdSettings.getSender(), player, agreeString);

                        componentBuilder.append(new FComponent(agreeString)
                                        .addRunCommand("/poll vote " + voteId + " agree")
                                        .get())
                                .append(" ", ComponentBuilder.FormatRetention.NONE);

                        String disagreeString = locale.getVaultString(commandSender, this + ".format.disagree");
                        disagreeString = MessageUtil.formatAll(cmdSettings.getSender(), player, disagreeString);

                        componentBuilder.append(new FComponent(disagreeString)
                                .addRunCommand("/poll vote " + voteId + " disagree")
                                .get());

                        player.spigot().sendMessage(componentBuilder.create());

                        FModule fModule = moduleManager.get(SoundsModule.class);
                        if (fModule instanceof SoundsModule soundsModule) {
                            soundsModule.play(new FSound(cmdSettings.getSender(), player, this.toString()));
                        }
                    });

            commands.set(getName() + ".last-id", poll.getId());
            commands.save();

            IntegrationsModule.sendDiscordPoll(cmdSettings.getSender(), poll.getMessage(), poll.getId());

            return true;
        }


        if (args.length < 3 || !StringUtils.isNumeric(args[1])
                || (!args[2].equalsIgnoreCase("agree") && !args[2].equalsIgnoreCase("disagree"))) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        int id = Integer.parseInt(args[1]);
        Poll poll = PollManager.get(id);

        if (poll == null || poll.isExpired()) {
            sendErrorMessage(commandSender, this + ".expired-message");
            return true;
        }

        if (cmdSettings.isConsole()) {
            sendErrorMessage(commandSender, this + ".console");
            return true;
        }

        String voteType = args[2].toLowerCase();

        int voteCounts = poll.vote(cmdSettings.getFPlayer(), voteType);
        if (voteCounts == 0) {
            sendErrorMessage(commandSender, this + ".already-message");
            return true;
        }

        String message = locale.getVaultString(commandSender, this + "." + voteType + "-message")
                .replace("<vote_type>", voteType)
                .replace("<id>", String.valueOf(id))
                .replace("<count>", String.valueOf(voteCounts));

        sendFormattedMessage(commandSender, MessageUtil.formatAll(cmdSettings.getSender(), message));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        tabCompleteClear();
        switch (args.length) {
            case 1 -> {
                isStartsWith(args[0], "vote");

                if (commandSender.hasPermission(CREATE_PERMISSION)) {
                    isStartsWith(args[0], "create");
                }
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("create")) {
                    isTabCompleteMessage(commandSender, args[1], "message");
                } else  {
                    PollManager.getPOLL_MAP().values()
                            .parallelStream()
                            .filter(poll -> !poll.isExpired())
                            .forEach(poll -> isStartsWith(args[1], String.valueOf(poll.getId())));
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("vote")) {
                    isStartsWith(args[2], "agree");
                    isStartsWith(args[2], "disagree");
                }
            }
        }

        return getSortedTabComplete();
    }
}
