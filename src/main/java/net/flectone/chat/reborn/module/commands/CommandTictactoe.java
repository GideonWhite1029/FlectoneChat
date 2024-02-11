package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.component.FComponent;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.model.tictactoe.TicTacToe;
import net.flectone.chat.reborn.module.FCommand;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CommandTictactoe extends FCommand {

    public CommandTictactoe(FModule module, String name) {
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

        if (args.length == 0) {
            sendUsageMessage(commandSender, alias);
            return true;
        }

        CmdSettings cmdSettings = new CmdSettings(commandSender, command);

        if (cmdSettings.isConsole()) {
            sendErrorMessage(commandSender, getModule() + ".console");
            return true;
        }

        TicTacToe ticTacToe = TicTacToe.get(args[0]);
        if (ticTacToe != null && ticTacToe.isEnded()) {
            sendErrorMessage(commandSender, this + ".game.ended");
            return true;
        }

        FPlayer secondFPlayer = playerManager.getOffline(args[0]);

        if ((secondFPlayer == null && ticTacToe == null) || cmdSettings.getFPlayer() == secondFPlayer) {
            sendErrorMessage(commandSender, getModule() + ".null-player");
            return true;
        }

        if (cmdSettings.isHaveCooldown()) {
            cmdSettings.getFPlayer().sendCDMessage(alias, command.getName());
            return true;
        }

        if (cmdSettings.isMuted()) {
            cmdSettings.getFPlayer().sendMutedMessage(command.getName());
            return true;
        }

        if (cmdSettings.isDisabled()) {
            sendErrorMessage(commandSender, getModule() + ".you-disabled");
            return true;
        }

        if (ticTacToe == null) {
            boolean isFirstPlayer = Math.random() > 0.5;

            UUID firstPlayer;
            UUID secondPlayer;

            if (isFirstPlayer) {
                firstPlayer = cmdSettings.getFPlayer().getUuid();
                secondPlayer = secondFPlayer.getUuid();
            } else {
                firstPlayer = secondFPlayer.getUuid();
                secondPlayer = cmdSettings.getFPlayer().getUuid();
            }

            ticTacToe = new TicTacToe(firstPlayer, secondPlayer);

            String sendMessage = locale.getVaultString(cmdSettings.getSender(), this + ".send-message");
            sendMessage = MessageUtil.formatAll(cmdSettings.getSender(),
                    MessageUtil.formatPlayerString(secondFPlayer.getPlayer(), sendMessage));

            commandSender.sendMessage(sendMessage);

            String getMessage = locale.getVaultString(secondFPlayer.getPlayer(), this + ".get-message")
                    .replace("<player>", cmdSettings.getFPlayer().getMinecraftName());

            getMessage = MessageUtil.formatAll(secondFPlayer.getPlayer(), getMessage);

            String hoverMessage = locale.getVaultString(secondFPlayer.getPlayer(), this + ".hover-message");
            hoverMessage = MessageUtil.formatAll(secondFPlayer.getPlayer(), hoverMessage);

            FComponent textComponent = new FComponent(getMessage)
                    .addRunCommand("/tictactoe " + ticTacToe.getUuid() + " yes")
                    .addHoverText(hoverMessage);

            secondFPlayer.getPlayer().spigot().sendMessage(textComponent.get());

            return true;
        }

        secondFPlayer = ticTacToe.getSecondFPlayer(cmdSettings.getFPlayer().getUuid());
        if (secondFPlayer == null) return true;

        if (!secondFPlayer.isOnline()) {
            sendErrorMessage(commandSender, getModule() + ".null-player");
            ticTacToe.setEnded(true);
            return true;
        }

        if (args[1].equalsIgnoreCase("yes") && !ticTacToe.isAccepted()) {
            ticTacToe.setAccepted(true);

            cmdSettings.getFPlayer().getPlayer().spigot().sendMessage(ticTacToe.build(cmdSettings.getFPlayer()));
            secondFPlayer.getPlayer().spigot().sendMessage(ticTacToe.build(secondFPlayer));
            return true;
        }

        if (ticTacToe.isBusy(Integer.parseInt(args[1])) || !ticTacToe.isNextPlayer(secondFPlayer.getUuid())) {
            sendErrorMessage(commandSender, this + ".game.wrong");
            return true;
        }

        ticTacToe.setMark(cmdSettings.getFPlayer().getUuid(), Integer.parseInt(args[1]));

        String message = null;
        if (ticTacToe.hasWinningTrio(cmdSettings.getSender())) {
            message = locale.getVaultString(cmdSettings.getSender(), this + ".game.win");
            message = MessageUtil.formatPlayerString(cmdSettings.getSender(), message);

            ticTacToe.setEnded(true);

        } else if (ticTacToe.checkDraw()) {
            message = locale.getVaultString(cmdSettings.getSender(),this + ".game.draw");
            ticTacToe.setEnded(true);
        }

        if (message != null) {
            cmdSettings.getFPlayer().getPlayer().sendMessage(MessageUtil.formatAll(cmdSettings.getSender(), message));
            secondFPlayer.getPlayer().sendMessage(MessageUtil.formatAll(secondFPlayer.getPlayer(), message));
        }

        sendFormattedMessage(cmdSettings.getFPlayer().getPlayer(), ticTacToe.build(cmdSettings.getFPlayer()));
        sendFormattedMessage(secondFPlayer.getPlayer(), ticTacToe.build(secondFPlayer));

        cmdSettings.getFPlayer().playSound(cmdSettings.getSender(), cmdSettings.getSender(), this.toString());
        secondFPlayer.playSound(secondFPlayer.getPlayer(), secondFPlayer.getPlayer(), this.toString());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        tabCompleteClear();

        switch (args.length) {
            case 1 -> isOnlinePlayer(args[0]);
            case 2 -> isDigitInArray(args[1], "", 3, 10);
        }

        return getSortedTabComplete();
    }
}
