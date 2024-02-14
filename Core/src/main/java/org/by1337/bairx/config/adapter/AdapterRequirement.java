package org.by1337.bairx.config.adapter;

import org.bukkit.configuration.ConfigurationSection;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.bairx.observer.requirement.impl.RequirementMath;
import org.by1337.bairx.observer.requirement.impl.RequirementString;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.ClassAdapter;

import java.util.Objects;

public class AdapterRequirement implements ClassAdapter<Requirement> {
    @Override
    public ConfigurationSection serialize(Requirement obj, YamlContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Requirement deserialize(YamlContext context) {
        String type = context.getAsString("type");
        String input = Objects.requireNonNull(context.getAsString("input"));
        if ("math".equals(type)){
            return new RequirementMath(input);
        }
        return new RequirementString(input);
    }
}
