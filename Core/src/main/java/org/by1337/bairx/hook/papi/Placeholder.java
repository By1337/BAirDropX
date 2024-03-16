package org.by1337.bairx.hook.papi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Placeholder {
    private final String name;
    private final Map<String, Placeholder> subPlaceholders = new HashMap<>();
    @Nullable
    private PlaceholderExecutor executor;

    public Placeholder(String name) {
        this.name = name;
    }

    public Placeholder addSubPlaceholder(Placeholder subPlaceholder) {
        subPlaceholders.put(subPlaceholder.name, subPlaceholder);
        return this;
    }

    public Placeholder executor(PlaceholderExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Nullable
    public String process(Player player, String[] args) {
        if (args.length >= 1) {
            String subcommandName = args[0];

            if (subPlaceholders.containsKey(subcommandName)) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                Placeholder subcommand = subPlaceholders.get(subcommandName);
                if (subcommand == null) {
                    for (Placeholder cmd : subPlaceholders.values()) {
                        if (cmd.name.equals(subcommandName)) {
                            subcommand = cmd;
                            break;
                        }
                    }
                }
                if (subcommand != null) {
                    return subcommand.process(player, subArgs);
                }
            }
        }
        if (executor == null) return null;
        return executor.run(player, args);
    }

    private Placeholder find(String[] args) {
        if (args.length >= 1) {
            String placeholderName = args[0];
            Placeholder placeholder = subPlaceholders.get(placeholderName);
            if (placeholder != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if (subArgs.length == 0) {
                    return placeholder;
                }
                return placeholder.find(subArgs);
            }
        }
        return null;
    }

    public void build() {
        var map = new HashMap<>(subPlaceholders);
        subPlaceholders.clear();
        map.forEach((k, v) -> {
            if (k.contains("_")) {
                String[] arr = k.split("_");
                Placeholder last = null;
                for (int i = 0; i < arr.length; i++) {
                    Placeholder placeholder = find(Arrays.copyOfRange(arr, 0, i + 1));
                    if (placeholder == null) {
                        placeholder = new Placeholder(arr[i]);
                        if (last != null) {
                            last.addSubPlaceholder(placeholder);
                            last = placeholder;
                        } else {
                            subPlaceholders.put(placeholder.name, placeholder);
                            last = placeholder;
                        }
                    } else {
                        last = placeholder;
                    }
                    if (i == arr.length - 1) {
                        placeholder.executor = v.executor;
                    }
                }
            } else {
                subPlaceholders.put(k, v);
            }
        });
    }

    public List<String> getAllPlaceHolders() {
        List<String> list = new ArrayList<>();
        if (executor != null && name != null)
            list.add(name);

        for (Placeholder placeholder : subPlaceholders.values()) {
            for (String s : placeholder.getAllPlaceHolders()) {
                if (name != null)
                    list.add(name + "_" + s);
                else
                    list.add(s);
            }
        }
        return list;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Placeholder{" +
                "name='" + name + '\'' +
                ", subPlaceholders=" + subPlaceholders +
                ", executor=" + executor +
                '}';
    }
}
