package net.flectone.chat.reborn.module.playerMessage.book;


import net.flectone.chat.reborn.module.FModule;

public class BookModule extends FModule {
    public BookModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new BookListener(this));
    }
}
