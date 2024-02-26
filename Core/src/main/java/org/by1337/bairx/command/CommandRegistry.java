package org.by1337.bairx.command;

import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.event.Event;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.argument.ArgumentStrings;

public class CommandRegistry {

    private static final Command<Event> commands;

    public static void run(Event event, String command) {
        try {
            commands.process(event, command.split(" "));
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerCommand(Command<Event> command) {
        if (commands.getCommand().contains(command.getCommand())) {
            throw new IllegalArgumentException("command already exist!");
        }
        commands.addSubCommand(command);
    }

    public static void unregisterCommand(String cmd) {
        if (commands.getSubcommands().remove(cmd) == null) {
            throw new IllegalArgumentException("command not-exist!");
        }
    }

    static {
        commands = new Command<>("cmd");

        registerCommand(new Command<Event>("[MESSAGE_ALL]")
                .argument(new ArgumentStrings<>("message"))
                .executor((event, args) -> {
                    BAirDropX.getMessage().sendAllMsg((String) args.getOrThrow("message", "Отсутствует сообщение!"));
                })
        );
    }
}
