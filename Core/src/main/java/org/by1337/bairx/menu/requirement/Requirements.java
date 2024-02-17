package org.by1337.bairx.menu.requirement;

import org.by1337.bairx.BAirDropX;
import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.Menu;

import java.util.List;

public class Requirements {
    private final List<IRequirement> requirements;
    private final List<String> denyCommands;

    public Requirements(List<IRequirement> requirements, List<String> denyCommands) {
        this.requirements = requirements;
        this.denyCommands = denyCommands;
    }

    public boolean check(Placeholderable placeholderable, Menu menu) {
        for (IRequirement iRequirement : requirements) {
            try {
                if (!iRequirement.check(placeholderable, menu)) {
                    return false;
                }
            } catch (Exception e) {
                BAirDropX.getMessage().error(e);
            }
        }
        return true;
    }

    public List<IRequirement> getRequirements() {
        return requirements;
    }

    public List<String> getDenyCommands() {
        return denyCommands;
    }

}