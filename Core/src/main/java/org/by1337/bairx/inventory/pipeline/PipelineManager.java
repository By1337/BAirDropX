package org.by1337.bairx.inventory.pipeline;

import org.by1337.blib.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PipelineManager<T> {
    private final List<Pair<String, PipelineHandler<T>>> handlers = new ArrayList<>();

    public PipelineManager<T> add(String name, PipelineHandler<T> handler) {
        handlers.add(Pair.of(name, handler));
        return this;
    }

    public PipelineManager<T> addLast(String name, PipelineHandler<T> handler) {
        add(name, handler);
        return this;
    }


    public PipelineManager<T> addFirst(String name, PipelineHandler<T> handler) {
        handlers.add(0, Pair.of(name, handler));
        return this;
    }

    public PipelineManager<T> addBefore(String before, String name, PipelineHandler<T> handler) {
        for (int i = 0; i < handlers.size(); i++) {
            var pair = handlers.get(i);
            if (pair.getLeft().equals(before)) {
                handlers.add(i, Pair.of(name, handler));
                return this;
            }
        }
        throw new PipelineEndOfListException();
    }

    public void processNext(T val, PipelineHandler<T> current){
        for (Pair<String, PipelineHandler<T>> pair : handlers) {
            if (pair.getRight() == current) {
                pair.getRight().process(val, this);
                return;
            }
        }
        throw new PipelineEndOfListException();
    }

    public PipelineManager<T> add(String n, PipelineHandler<T> h,
                    String n1, PipelineHandler<T> h1,
                    String n2, PipelineHandler<T> h2,
                    String n3, PipelineHandler<T> h3,
                    String n4, PipelineHandler<T> h4,
                    String n5, PipelineHandler<T> h5
    ) {
        add(n, h, n1, h1, n2, h2, n3, h3, n4, h4);
        add(n5, h5);
        return this;
    }

    public PipelineManager<T> add(String n, PipelineHandler<T> h,
                    String n1, PipelineHandler<T> h1,
                    String n2, PipelineHandler<T> h2,
                    String n3, PipelineHandler<T> h3,
                    String n4, PipelineHandler<T> h4
    ) {
        add(n, h, n1, h1, n2, h2, n3, h3);
        add(n4, h4);
        return this;
    }

    public PipelineManager<T> add(String n, PipelineHandler<T> h,
                    String n1, PipelineHandler<T> h1,
                    String n2, PipelineHandler<T> h2,
                    String n3, PipelineHandler<T> h3
    ) {
        add(n, h, n1, h1, n2, h2);
        add(n3, h3);
        return this;
    }

    public PipelineManager<T> add(String n, PipelineHandler<T> h,
                    String n1, PipelineHandler<T> h1,
                    String n2, PipelineHandler<T> h2
    ) {
        add(n, h, n1, h1);
        add(n2, h2);
        return this;
    }

    public PipelineManager<T> add(String n, PipelineHandler<T> h, String n1, PipelineHandler<T> h1) {
        add(n, h);
        add(n1, h1);
        return this;
    }

    public List<Pair<String, PipelineHandler<T>>> getHandlers() {
        return handlers;
    }

    public static class PipelineEndOfListException extends RuntimeException {
        public PipelineEndOfListException() {
        }

        public PipelineEndOfListException(String message) {
            super(message);
        }

        public PipelineEndOfListException(String message, Throwable cause) {
            super(message, cause);
        }

        public PipelineEndOfListException(Throwable cause) {
            super(cause);
        }

        public PipelineEndOfListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
