package org.by1337.bairx.inventory;

import org.by1337.bairx.inventory.pipeline.PipelineHandler;
import org.by1337.blib.configuration.YamlContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HandlerCreator<T> {
    PipelineHandler<T> create(YamlContext context);

    HandlerRegistry.Type<T> getType();
    @Nullable
    String addBefore();
    @NotNull YamlContext saveDefault();
    @NotNull String name();
}
