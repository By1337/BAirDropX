package org.by1337.bairx.random;

import java.util.List;
import java.util.Random;

public class WeightedRandomItemSelector<T extends WeightedItem> {
    private final List<T> items;
    private final Random random;
    private final int totalWeight;

    public WeightedRandomItemSelector(List<T> items) {
        this.items = items;
        this.random = new Random();
        totalWeight = items.stream().mapToInt(WeightedItem::getWeight).sum();
    }

    public WeightedRandomItemSelector(List<T> items, Random random) {
        this.items = items;
        this.random = random;
        totalWeight = items.stream().mapToInt(WeightedItem::getWeight).sum();
    }

    public T getRandomItem() {
        if (totalWeight <= 0 || items.isEmpty()) return null;
        int randomWeight = random.nextInt(totalWeight);

        for (T item : items) {
            randomWeight -= item.getWeight();
            if (randomWeight < 0) {
                return item;
            }
        }

        return null;
    }
}

