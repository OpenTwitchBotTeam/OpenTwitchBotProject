package com.github.otbproject.otbproject.messages.send;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.bot.Control;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.config.BotConfig;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.util.ThreadUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;

// This class is not in general thread-safe. Thread safety should be enforced by the
// class using this one
public class ChannelMessageSender {
    private static final ExecutorService EXECUTOR_SERVICE;

    private final Channel channel;
    private final PriorityBlockingQueue<MessageOut> queue =
            new PriorityBlockingQueue<>(11, MessageOut.PRIORITY_COMPARATOR);
    private Future<?> future;
    private volatile boolean active = false;

    static {
        EXECUTOR_SERVICE = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setUncaughtExceptionHandler(ThreadUtil.UNCAUGHT_EXCEPTION_HANDLER)
                        .build()
        );
    }

    public ChannelMessageSender(Channel channel) {
        this.channel = channel;
    }

    public boolean start() {
        if (active) {
            return false;
        }
        active = true;
        future = EXECUTOR_SERVICE.submit(this::sendMessages);
        return true;
    }

    public boolean stop() {
        if (!active) {
            return false;
        }
        active = false;
        future.cancel(true);
        queue.clear();
        return true;
    }

    // Queue itself is thread-safe, method is not
    public boolean send(MessageOut message) {
        if (!active) {
            return false;
        }

        MessagePriority priority = message.priority;
        // Defaults to no limit
        int limit = -1;

        if (priority == MessagePriority.HIGH) {
            limit = channel.getFromConfig(config -> config.queueLimits.getHighPriorityLimit());
        } else if (priority == MessagePriority.DEFAULT) {
            limit = channel.getFromConfig(config -> config.queueLimits.getDefaultPriorityLimit());
        } else if (priority == MessagePriority.LOW) {
            limit = channel.getFromConfig(config -> config.queueLimits.getLowPriorityLimit());
        }

        // Yes, I am aware that this can be simplified, but it ends up being just
        //  about unreadable
        if ((limit >= 0) && queue.size() > limit) {
            return false;
        }

        message.recordInsertionTime();
        return queue.add(message);
    }

    public void clearQueue() {
        queue.clear();
    }

    private void sendMessages() {
        try {
            Thread.currentThread().setName(channel.getName() + " Message Sender");
            MessageOut message;
            int sleepTime = Configs.getFromBotConfig(BotConfig::getMessageSendDelayInMilliseconds);

            while (true) {
                message = queue.take();
                Control.getBot().sendMessage(channel.getName(), message.message);
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            App.logger.info("Stopped message sender for " + channel.getName());
            Thread.currentThread().interrupt();
        }
    }
}
