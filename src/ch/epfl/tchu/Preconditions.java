package ch.epfl.tchu;

public final class Preconditions {

    private Preconditions(){ }

    /**
     *
     * @param shouldBeTrue
     */
        public static void checkArgument ( boolean shouldBeTrue ){
            if (shouldBeTrue==false) {
                throw new IllegalArgumentException();

        }

    }
}
