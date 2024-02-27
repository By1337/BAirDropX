package org.by1337.bairx.inventory.pipeline;

public interface PipelineHandler<T> {
    void process(T val, PipelineManager<T> manager);
}
