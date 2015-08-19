package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.bot.Bot;
import com.github.otbproject.otbproject.channel.Channel;
import com.github.otbproject.otbproject.filter.FilterAction;
import com.github.otbproject.otbproject.filter.FilterGroup;
import com.github.otbproject.otbproject.filter.FilterProcessor;
import com.github.otbproject.otbproject.messages.receive.PackagedMessage;
import com.github.otbproject.otbproject.messages.send.MessagePriority;
import com.github.otbproject.otbproject.user.UserLevel;

import java.util.Optional;

public class TimeoutProcessor {
    public static boolean doTimeouts(Channel channel, PackagedMessage packagedMessage) {
        // TODO implement and remove if statement
        if (false) { // So I can work on an implementation without changing behaviour
            Optional<FilterGroup> optional = FilterProcessor.process(channel.getFilterMap(), packagedMessage.message, packagedMessage.userLevel);
            if (optional.isPresent()) {
                FilterGroup filterGroup = optional.get();
                performFilterAction(packagedMessage, filterGroup.getAction());
                sendFilterMessage(channel, packagedMessage, filterGroup);
            } else {
                return false;
            }
        }
        return false;
    }

    private static void performFilterAction(PackagedMessage packagedMessage, FilterAction action) {
        switch (action) {
            case BAN:
                Bot.getBot().ban(packagedMessage.channel, packagedMessage.user);
                break;
            case TIMEOUT:
                Bot.getBot().timeout(packagedMessage.channel, packagedMessage.user, 600); // TODO get actual time from somewhere (config?)
                break;
            case STRIKE:
                // TODO handle strike number
                break;
            case PURGE:
                Bot.getBot().timeout(packagedMessage.channel, packagedMessage.user, 1);
                break;
        }
    }

    private static void sendFilterMessage(Channel channel, PackagedMessage packagedMessage, FilterGroup filterGroup) {
        // TODO handle message for timeout
        String responseCommand = "";
        switch (filterGroup.getAction()) {
            case BAN:
            case TIMEOUT:
            case PURGE:
            case WARN:
                responseCommand = filterGroup.getResponseCommand();
                break;
            case STRIKE:
                // TODO handle strike number
                break;
        }
        PackagedMessage responseMessage = new PackagedMessage(responseCommand, packagedMessage.user, packagedMessage.channel, packagedMessage.destinationChannel, UserLevel.INTERNAL, MessagePriority.LOW);
        channel.receiveMessage(packagedMessage);
    }
}
