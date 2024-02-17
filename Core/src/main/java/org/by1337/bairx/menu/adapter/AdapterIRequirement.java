package org.by1337.bairx.menu.adapter;

import org.bukkit.configuration.ConfigurationSection;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.ClassAdapter;
import org.by1337.bairx.menu.requirement.*;

import java.util.Objects;

public class AdapterIRequirement implements ClassAdapter<IRequirement> {
    @Override
    public ConfigurationSection serialize(IRequirement obj, YamlContext context) {
        throw new IllegalStateException();
    }

    @Override
    public IRequirement deserialize(YamlContext context) {
        String type = context.getAsString("type");
        if (type.equalsIgnoreCase("string equals") || type.equalsIgnoreCase("sq") || type.equalsIgnoreCase("se")) {
            String input = Objects.requireNonNull(context.getAsString("input"), "string equals должен содержать input");
            String input2 = Objects.requireNonNull(context.getAsString("input2"), "string equals должен содержать input2");
            String output = context.getAsString("output", "true");
            return new RequirementStringEquals(input, input2, output);
        }
        if (type.equalsIgnoreCase("string contains") || type.equalsIgnoreCase("sc")) {
            String input = Objects.requireNonNull(context.getAsString("input"), "string contains должен содержать input");
            String input2 = Objects.requireNonNull(context.getAsString("input2"), "string contains должен содержать input2");
            String output = context.getAsString("output", "true");
            return new RequirementStringContains(input, input2, output);
        }
        if (type.equalsIgnoreCase("math") || type.equalsIgnoreCase("m")) {
            String input = Objects.requireNonNull(context.getAsString("input"), "math должен содержать input");
            String output = context.getAsString("output", "true");

            return new RequirementMath(input, output);
        }
        if (type.equalsIgnoreCase("has permission") || type.equalsIgnoreCase("hp")) {
            return new RequirementHasPermission(Objects.requireNonNull(context.getAsString("permission"), "has permission должен содержать permission"));
        }
        throw new IllegalArgumentException("unknown requirement type: " + type);
    }
}
