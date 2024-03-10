package org.by1337.bairx.random;

import org.by1337.bairx.util.Placeholder;

import java.util.Random;
import java.util.UUID;

public class RandomPlaceholders extends Placeholder {
    public static final RandomPlaceholders INSTANCE = new RandomPlaceholders();
    private final Random random;
    private RandomPlaceholders() {
        random = new Random();
        registerPlaceholder("{rand-1}", () -> random.nextInt(1));
        registerPlaceholder("{rand-10}", () -> random.nextInt(10));
        registerPlaceholder("{rand-50}", () -> random.nextInt(50));
        registerPlaceholder("{rand-100}", () -> random.nextInt(100));
        registerPlaceholder("{rand-boolean}", random::nextBoolean);
        registerPlaceholder("{rand-uuid}", UUID::randomUUID);

    }
}
