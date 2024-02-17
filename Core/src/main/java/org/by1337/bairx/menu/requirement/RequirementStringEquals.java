package org.by1337.bairx.menu.requirement;

import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.Menu;
public class RequirementStringEquals implements IRequirement {
    private final String input;
    private final String input1;
    private final String output;

    public RequirementStringEquals(String input, String input1, String output) {
        this.input = input;
        this.input1 = input1;
        this.output = output;
    }

    @Override
    public boolean check(Placeholderable placeholderable, Menu menu) {
        String replacesInput = placeholderable.replace(input);
        String replacesInput1 = placeholderable.replace(input1);

        return String.valueOf(replacesInput.equals(replacesInput1)).equals(placeholderable.replace(output));
    }

    @Override
    public RequirementType getType() {
        return RequirementType.STRING_EQUALS;
    }

    public static boolean parseBoolean(String s) {
        if (s.equals("1")) return true;
        return s.equals("true");
    }
}