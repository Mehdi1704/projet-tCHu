package ch.epfl.test;

import java.util.Random;

public final class TestRandomizer {
    // Fix random seed to guarantee reproducibility.
    public final static long SEED = 2021;

    public final static int RANDOM_ITERATIONS = 1_000;
    public static final Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };

    public static Random newRandom() {
        return new Random(SEED);
    }
}
