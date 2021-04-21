package ch.epfl.tchu;

/**
 * Verification des paramètres de certaines methodes
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * @param shouldBeTrue paramètre devant être vrai
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
    /**
     * @param string ne devant pas etre vide
     */
    public static void checkIfEmptyString(String string) {
        if (string.equals("")) {
            throw new IllegalArgumentException();
        }
    }
}
