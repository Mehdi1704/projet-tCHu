package ch.epfl.tchu;
/**
 *
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
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
