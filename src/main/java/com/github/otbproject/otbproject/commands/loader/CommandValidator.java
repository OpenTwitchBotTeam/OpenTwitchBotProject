package com.github.otbproject.otbproject.commands.loader;

import com.github.otbproject.otbproject.util.DefaultCommandGenerator;

public class CommandValidator {
    public static LoadedCommand validateCommand(LoadedCommand command) throws InvalidCommandException {
        if ((command == null) || (command.getName() == null) || (command.getResponse() == null)) {
            throw new InvalidCommandException();
        }

        LoadedCommand validatedCommand = command.getCopy();
        LoadedCommand defaultCommand = DefaultCommandGenerator.createDefaultCommand();

        if (validatedCommand.getExecUserLevel() == null) {
            validatedCommand.setExecUserLevel(defaultCommand.getExecUserLevel());
        }

        if (validatedCommand.modifyingUserLevels.getNameModifyingUL() == null) {
            validatedCommand.modifyingUserLevels.setNameModifyingUL(defaultCommand.modifyingUserLevels.getNameModifyingUL());
        }

        if (validatedCommand.modifyingUserLevels.getResponseModifyingUL() == null) {
            validatedCommand.modifyingUserLevels.setResponseModifyingUL(defaultCommand.modifyingUserLevels.getResponseModifyingUL());
        }

        if (validatedCommand.modifyingUserLevels.getUserLevelModifyingUL() == null) {
            validatedCommand.modifyingUserLevels.setUserLevelModifyingUL(defaultCommand.modifyingUserLevels.getUserLevelModifyingUL());
        }

        if (validatedCommand.isEnabled() == null) {
            validatedCommand.setEnabled(defaultCommand.isEnabled());
        }

        return validatedCommand;
    }

    public static LoadedAlias validateAlias(LoadedAlias alias) throws InvalidAliasException {
        if ((alias == null) || (alias.getName() == null) || (alias.getCommand() == null)) {
            throw new InvalidAliasException();
        }

        LoadedAlias validatedAlias = alias.getCopy();
        LoadedAlias defaultAlias = DefaultCommandGenerator.createDefaultAlias();

        if (validatedAlias.getModifyingUserLevel() == null) {
            validatedAlias.setModifyingUserLevel(defaultAlias.getModifyingUserLevel());
        }

        if (validatedAlias.isEnabled() == null) {
            validatedAlias.setEnabled(defaultAlias.isEnabled());
        }

        return validatedAlias;
    }
}