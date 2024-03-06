package org.by1337.bairx.inventory.pipeline.click;

import org.bukkit.event.inventory.InventoryEvent;
import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.bairx.inventory.pipeline.PipelineManager;

public class ClickHandler implements PipelineHandler<InventoryEvent> {
    @Override
    public void process(InventoryEvent val, PipelineManager<InventoryEvent> manager) {
        // nothing
    }
}
