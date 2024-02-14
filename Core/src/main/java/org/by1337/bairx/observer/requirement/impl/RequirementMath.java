package org.by1337.bairx.observer.requirement.impl;

import org.by1337.bairx.event.Event;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.blib.math.MathParser;

public class RequirementMath implements Requirement {
    private final String condition;

    public RequirementMath(String condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Event event) {
        String out = MathParser.mathSave(String.format("math[%s]", event.replace(condition)));
        return "1".equals(out);
    }
}
