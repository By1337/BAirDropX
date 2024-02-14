package org.by1337.bairx.observer.requirement;

import org.by1337.bairx.event.Event;

import java.util.function.Predicate;

public interface Requirement extends Predicate<Event> {
    boolean test(Event event);
}
