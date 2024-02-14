package org.by1337.bairx.observer.requirement;

import org.by1337.bairx.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Requirements implements Predicate<Event> {
    private final List<Predicate<Event>> requirements;

    public Requirements(Collection<Requirement> requirements) {
        this.requirements = new ArrayList<>(requirements.stream().map(r -> (Predicate<Event>) r).toList());
    }

    public void add(Predicate<Event> predicate){
        requirements.add(predicate);
    }
    public void add(Requirement requirement){
        requirements.add(requirement);
    }

    @Override
    public boolean test(Event event) {
        for (Predicate<Event> predicate : requirements) {
            if (!predicate.test(event)) {
                return false;
            }
        }
        return true;
    }

}
