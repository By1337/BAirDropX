package org.by1337.bairx.command.bair;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.bairx.command.CommandRegistry;
import org.by1337.blib.command.Command;
import org.by1337.blib.command.CommandException;
import org.by1337.blib.command.CommandSyntaxError;
import org.by1337.blib.command.argument.Argument;
import org.by1337.blib.command.argument.ArgumentStrings;
import org.by1337.blib.command.requires.RequiresPermission;
import org.by1337.blib.util.NameKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExecuteCommand extends Command<CommandSender> {

    public ExecuteCommand() {
        super("execute");
        requires(new RequiresPermission<>("bair.execute"));
        addSubCommand(new Command<CommandSender>("default")
                .argument(new MainArgument("arg", Type.DEFAULT))
                .executor(((sender, args) -> {}))
        );
        addSubCommand(new Command<CommandSender>("special")
                .argument(new MainArgument("arg", Type.SPECIAL))
                .executor(((sender, args) -> {}))
        );
    }

    private static class MainArgument extends ArgumentStrings<CommandSender> {
        private final Type type;

        public MainArgument(String name, Type type) {
            super(name);
            this.type = type;
        }

        ///bairx execute default <airdrop> <airdrop-command>
        @Override
        public List<String> tabCompleter(CommandSender sender, String str) throws CommandSyntaxError {
            final Player player = sender instanceof Player p ? p : null;
            if (str.isEmpty()) return BAirDropX.airdrops().stream().map(air -> air.getId().getName()).toList();
            final String[] args;
            if (str.endsWith(" ")){
                var arr = str.split(" ");
                args = new String[arr.length + 1];
                System.arraycopy(arr, 0, args, 0, arr.length);
                args[arr.length] = "";
            }else {
                args = str.split(" ");
            }
            if (args.length == 1)
                return BAirDropX.airdrops().stream().map(air -> air.getId().getName()).filter(s -> s.startsWith(str)).toList();
            final AirDrop airDrop = BAirDropX.getAirdropById(new NameKey(args[0]));
            if (airDrop == null) {
                return Collections.singletonList("Аирдроп '" + args[0] + "' не найден!");
            }
            if (type == Type.DEFAULT) {
                return CommandRegistry.getDefaultCompleter(player, airDrop, Arrays.copyOfRange(args, 1, args.length));
            } else {
                return CommandRegistry.getAirDropCompleter(player, airDrop, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            }
        }

        @Override
        public Object process(CommandSender sender, String str) throws CommandSyntaxError {
            final Player player = sender instanceof Player p ? p : null;
            if (str.isEmpty())
                throw new CommandSyntaxError("bairx execute <default/special> <airdrop> <airdrop-command>");
            final String[] args = str.split(" ");
            if (args.length == 1)
                throw new CommandSyntaxError("bairx execute <default/special> <airdrop> <airdrop-command>");
            final AirDrop airDrop = BAirDropX.getAirdropById(new NameKey(args[0]));
            if (airDrop == null) {
                throw new CommandSyntaxError("Аирдроп '" + args[0] + "' не найден!");
            }
            if (type == Type.DEFAULT) {
                try {
                    CommandRegistry.runDefaultCommand(player, airDrop, Arrays.copyOfRange(args, 1, args.length));
                } catch (CommandException e) {
                    throw new CommandSyntaxError(e.getMessage());
                }
            } else {
                CommandRegistry.runAirDropCommand(player, airDrop, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            }
            return null;
        }
    }

    private enum Type {
        SPECIAL,
        DEFAULT;
    }
}
