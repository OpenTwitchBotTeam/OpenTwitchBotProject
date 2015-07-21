package com.github.otbproject.otbproject.bot;

import com.github.otbproject.otbproject.App;

public class BotRunnable implements Runnable {
    @Override
    public void run() {
        try {
            App.logger.info("Bot Started");
            Bot.getBot().startBot();
            App.logger.info("Bot Stopped");
        } catch (BotInitException e) {
            App.logger.catching(e);
        }
    }
}
