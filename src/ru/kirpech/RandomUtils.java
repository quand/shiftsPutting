package ru.kirpech;

import java.util.Random;

public class RandomUtils {
    private static final Random RAND = new Random(10);

    private RandomUtils() {

    }

    public static int nextInt(int bound) {
        return RAND.nextInt(bound);
    }

    public static double nextDouble(int min, int max) {
        max -= min;
        return (RAND.nextDouble() * max)+min;
    }

    public static int nextInt(int min, int max) {
        return RAND.nextInt(max - min) + min;
    }

    public static double nextDouble() {
        return RAND.nextDouble();
    }
}
