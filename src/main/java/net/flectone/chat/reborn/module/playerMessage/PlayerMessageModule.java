package net.flectone.chat.reborn.module.playerMessage;

import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.playerMessage.anvil.AnvilModule;
import net.flectone.chat.reborn.module.playerMessage.book.BookModule;
import net.flectone.chat.reborn.module.playerMessage.chat.ChatModule;
import net.flectone.chat.reborn.module.playerMessage.formatting.FormattingModule;
import net.flectone.chat.reborn.module.playerMessage.patterns.PatternsModule;
import net.flectone.chat.reborn.module.playerMessage.sign.SignModule;
import net.flectone.chat.reborn.module.playerMessage.swearProtection.SwearProtectionModule;

public class PlayerMessageModule extends FModule {

    public PlayerMessageModule(String name) {
        super(name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        new PatternsModule(this, "patterns");
        new SwearProtectionModule(this, "swear-protection");
        new FormattingModule(this, "formatting");
        new ChatModule(this, "chat");
        new SignModule(this, "sign");
        new BookModule(this, "book");
        new AnvilModule(this, "anvil");
    }
}
