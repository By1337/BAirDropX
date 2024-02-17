package org.by1337.bairx.menu.click;


import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.Menu;
import org.by1337.bairx.menu.requirement.Requirements;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Click implements IClick{
    private final List<String> commands;
    private final Requirements requirements;
    private final ClickType clickType;

    public Click(List<String> commands, @Nullable Requirements requirements, ClickType clickType) {
        this.clickType = clickType;
        this.commands = commands;
        this.requirements = requirements;
    }

    @Override
    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public List<String> run(Placeholderable placeholderable, Menu menu) {
        if (requirements == null || requirements.check(placeholderable, menu)){
            return commands;
        }else {
            return requirements.getDenyCommands();
        }
    }
}
