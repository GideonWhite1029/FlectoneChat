package net.flectone.chat.reborn.module.serverMessage;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.serverMessage.advancement.AdvancementModule;
import net.flectone.chat.reborn.module.serverMessage.death.DeathModule;
import net.flectone.chat.reborn.module.serverMessage.greeting.GreetingModule;
import net.flectone.chat.reborn.module.serverMessage.join.JoinModule;
import net.flectone.chat.reborn.module.serverMessage.quit.QuitModule;

public class ServerMessageModule extends FModule {

    public ServerMessageModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        new JoinModule(this, "join");
        new QuitModule(this, "quit");
        new AdvancementModule(this, "advancement");
        new DeathModule(this, "death");
        new GreetingModule(this, "greeting");
    }
}
