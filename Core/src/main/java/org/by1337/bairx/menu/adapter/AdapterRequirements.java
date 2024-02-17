package org.by1337.bairx.menu.adapter;

import org.bukkit.configuration.ConfigurationSection;
import org.by1337.blib.configuration.YamlContext;
import org.by1337.blib.configuration.adapter.ClassAdapter;
import org.by1337.bairx.menu.requirement.IRequirement;
import org.by1337.bairx.menu.requirement.Requirements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AdapterRequirements implements ClassAdapter<Requirements> {
    @Override
    public ConfigurationSection serialize(Requirements obj, YamlContext context) {
        throw new IllegalStateException();
    }

    @Override
    public Requirements deserialize(YamlContext context) {
        List<IRequirement> requirementList = context.getMap("requirements", IRequirement.class, new HashMap<>()).values().stream().toList();
        List<String> denyCommands = Collections.unmodifiableList(context.getList("deny_commands", String.class, new ArrayList<>()));
        return new Requirements(requirementList, denyCommands);
    }
}