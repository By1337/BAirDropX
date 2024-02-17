package org.by1337.bairx.util;

import org.by1337.blib.chat.Placeholderable;

import java.util.*;
import java.util.function.Supplier;

public abstract class Placeholder implements Placeholderable {
    protected final Map<String, Supplier<Object>> placeholders = new HashMap<>();

    public void registerPlaceholder(String placeholder, Supplier<Object> supplier) {
        placeholders.put(placeholder, supplier);
        String s;
    }

    public void registerPlaceholders(Collection<Map.Entry<String, Supplier<Object>>> list) {
        for (var entry : list) {
            registerPlaceholder(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String replace(String string) {
        StringBuilder sb = new StringBuilder(string);
        for (Map.Entry<String, Supplier<Object>> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            int len = placeholder.length();
            int pos = sb.indexOf(placeholder);
            while (pos != -1) {
                sb.replace(pos, pos + len, String.valueOf(entry.getValue().get()));
                pos = sb.indexOf(placeholder, pos + len - 2);
            }
        }
        return sb.toString();
    }

    public Set<Map.Entry<String, Supplier<Object>>> entrySet() {
        return placeholders.entrySet();
    }

}
