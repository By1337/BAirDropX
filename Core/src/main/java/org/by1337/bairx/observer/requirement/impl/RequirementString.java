package org.by1337.bairx.observer.requirement.impl;

import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.event.Event;
import org.by1337.bairx.observer.requirement.Requirement;
import org.by1337.blib.math.MathParser;

public class RequirementString implements Requirement {
    private final String condition;

    public RequirementString(String condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Event event) {
        String[] arr = condition.split(" ");
        if (arr.length != 3) {
            BAirDropX.getMessage().error(condition + " must be 'anyText !?[equals|contains] anyText'");
            return false;
        }
        if ("equals".equals(arr[1]) || "==".equals(arr[1])) {
            return arr[0].equals(arr[2]);
        } else if ("!equals".equals(arr[1]) || "!==".equals(arr[1])) {
            return !arr[0].equals(arr[2]);
        } else if ("contains".equals(arr[1])) {
            return arr[0].contains(arr[2]);
        } else if ("!contains".equals(arr[1])) {
            return !arr[0].contains(arr[2]);
        }
        BAirDropX.getMessage().error(condition + " must be 'anyText !?[equals|contains] anyText'");
        return false;
    }
}
