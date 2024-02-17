package org.by1337.bairx.menu.requirement;

import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.Menu;
public interface IRequirement {
    boolean check(Placeholderable placeholderable, Menu menu);
    RequirementType getType();
}