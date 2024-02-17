package org.by1337.bairx.menu.click;

import org.by1337.blib.chat.Placeholderable;
import org.by1337.bairx.menu.Menu;

import java.util.List;

public interface IClick {
    ClickType getClickType();
    List<String> run(Placeholderable placeholderable, Menu menu);
}