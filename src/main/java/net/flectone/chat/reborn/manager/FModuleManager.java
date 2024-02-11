package net.flectone.chat.reborn.manager;

import lombok.Getter;
import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.listener.*;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.autoMessage.AutoMessageModule;
import net.flectone.chat.reborn.module.chatBubble.ChatBubbleModule;
import net.flectone.chat.reborn.module.color.ColorModule;
import net.flectone.chat.reborn.module.commands.CommandsModule;
import net.flectone.chat.reborn.module.extra.ExtraModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.module.player.PlayerModule;
import net.flectone.chat.reborn.module.playerMessage.PlayerMessageModule;
import net.flectone.chat.reborn.module.server.ServerModule;
import net.flectone.chat.reborn.module.serverMessage.ServerMessageModule;
import net.flectone.chat.reborn.module.sounds.SoundsModule;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

@Getter
public class FModuleManager {

    private final HashMap<Class<?>, FModule> moduleHashMap = new HashMap<>();

    public Collection<FModule> getModules() {
        return moduleHashMap.values();
    }

    public void put(FModule fModule) {
        moduleHashMap.put(fModule.getClass(), fModule);
    }

    public void init() {
        new CommandsModule("commands");
        new ExtraModule("extra");
        new PlayerModule("player");
        new SoundsModule("sounds");
        new ServerModule("server");
        new IntegrationsModule("integrations");
        new ChatBubbleModule("chat-bubble");
        new AutoMessageModule("auto-message");
        new ServerMessageModule("server-message");
        new PlayerMessageModule("player-message");
        new ColorModule("color");

        FActionManager actionManager = FlectoneChatReborn.getPlugin().getActionManager();
        actionManager.add(new FPlayerActionListener(null));
        actionManager.add(new MarkSpawnListener(null));
        actionManager.add(new ChatBubbleSpawnListener(null));
        actionManager.add(new FPlayerTicker(null));
        actionManager.add(new SpitHitListener(null));
    }

    @Nullable
    public FModule get(Class<?> clazz) {
        return moduleHashMap.get(clazz);
    }
}
