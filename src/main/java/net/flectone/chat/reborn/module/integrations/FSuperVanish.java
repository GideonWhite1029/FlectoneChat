package net.flectone.chat.reborn.module.integrations;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.manager.FModuleManager;
import net.flectone.chat.reborn.model.file.FConfiguration;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.serverMessage.join.JoinModule;
import net.flectone.chat.reborn.module.serverMessage.quit.QuitModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class FSuperVanish implements Listener, FIntegration {

    private FModuleManager moduleManager;
    private FConfiguration locale;

    public FSuperVanish() {
        init();
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(this, FlectoneChatReborn.getPlugin());
        FlectoneChatReborn.info("SuperVanish detected and hooked");

        FlectoneChatReborn plugin = FlectoneChatReborn.getPlugin();
        moduleManager = plugin.getModuleManager();
        locale = plugin.getFileManager().getLocale();
    }

    @EventHandler
    public void onHide(@NotNull PlayerHideEvent event) {
        if (event.isCancelled()) return;

        FModule fModule = moduleManager.get(QuitModule.class);
        if (fModule instanceof QuitModule quitModule) {
            Player player = event.getPlayer();
            String string = locale.getVaultString(player, "server-message.quit.message");
            quitModule.sendAll(player, string);
            event.setSilent(true);
        }
    }

    @EventHandler
    public void onShow(@NotNull PlayerShowEvent event) {
        if (event.isCancelled()) return;

        FModule fModule = moduleManager.get(JoinModule.class);
        if (fModule instanceof JoinModule joinModule) {
            Player player = event.getPlayer();
            String string = locale.getVaultString(player, "server-message.join.message");
            joinModule.sendAll(player, string);
            event.setSilent(true);
        }
    }
}
