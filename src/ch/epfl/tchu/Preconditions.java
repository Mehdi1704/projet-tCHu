package ch.epfl.tchu;

public final class Preconditions {

    private Preconditions(){ }

    /**
     *
     * @param shouldBeTrue paramètre devant être vrai
     */
        public static void checkArgument ( boolean shouldBeTrue ){
            if (!shouldBeTrue) {
                throw new IllegalArgumentException();

        }

    }
}
