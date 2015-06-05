package com.github.otbproject.otbproject.proc;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.commands.Alias;
import com.github.otbproject.otbproject.commands.Commands;
import com.github.otbproject.otbproject.commands.loader.LoadedAlias;
import com.github.otbproject.otbproject.commands.loader.LoadedCommand;
import com.github.otbproject.otbproject.commands.parser.CommandResponseParser;
import com.github.otbproject.otbproject.database.DatabaseWrapper;
import com.github.otbproject.otbproject.users.UserLevel;

import java.util.*;

public class CommandProcessor {
    public static ProcessedCommand process(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        message = message.trim();
        String commandMsg = checkAlias(db, message);
        return checkCommand(db, commandMsg, channel, user, userLevel, debug);
    }

    public static String checkAlias(DatabaseWrapper db, String message) {
        return checkAlias(db, message, new HashSet<>());
    }

    private static String checkAlias(DatabaseWrapper db, String message, HashSet<String> usedAliases) {
        String[] splitMsg = message.split(" ", 2);
        String aliasName = splitMsg[0];

        // Prevent infinite alias loop
        if (usedAliases.contains(aliasName)) {
            return message;
        } else {
            usedAliases.add(aliasName);
        }
        LoadedAlias loadedAlias = Alias.get(db, aliasName);
        if ((loadedAlias != null) && loadedAlias.isEnabled()) {
            if (splitMsg.length == 1) {
                return checkAlias(db, loadedAlias.getCommand(), usedAliases);
            }
            return checkAlias(db, (loadedAlias.getCommand() + " " + splitMsg[1]), usedAliases);
        }
        // Return message if not an alias
        return message;
    }

    // Returns an empty string if script command
    private static ProcessedCommand checkCommand(DatabaseWrapper db, String message, String channel, String user, UserLevel userLevel, boolean debug) {
        String[] splitMsg = message.split(" ", 2);
        String cmdName = splitMsg[0];

        String[] args;
        if ((splitMsg.length == 1) || splitMsg[1].equals("")) {
            args = new String[0];
        } else {
            args = splitMsg[1].split(" ");
            List<String> tempArrayList = Arrays.asList(args);
            for (Iterator<String> i = tempArrayList.iterator(); i.hasNext();) {
                if (i.next().isEmpty()) {
                    i.remove();
                }
            }

            if (tempArrayList.size() == 0) {
                args = new String[0];
            } else {
                args = tempArrayList.toArray(new String[tempArrayList.size()]);
            }
        }

        LoadedCommand command = Commands.get(db, cmdName);
        if ((command != null) && command.isEnabled() && userLevel.getValue() >= command.getExecUserLevel().getValue() && args.length >= command.getMinArgs()) {
            App.logger.debug("Processing command: " + cmdName);
            String scriptPath = command.getScript();
            // Return script path
            if ((scriptPath != null) && !scriptPath.equals("null")) {
                return new ProcessedCommand(scriptPath, cmdName, true, args);
            }
            // Else non-script command
            // Check if command is debug
            else if (!command.isDebug() || debug) {
                String response = CommandResponseParser.parse(user, channel, (command.getCount() + 1), args, command.getResponse());
                return new ProcessedCommand(response, cmdName, false, args);
            }
        }
        return new ProcessedCommand("", "", false, args);
    }
}
