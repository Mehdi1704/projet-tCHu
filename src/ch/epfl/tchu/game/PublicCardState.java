package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Etat public de cartes
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur public permettant d'initialiser les cartes à l'etat connu
     *
     * @param faceUpCards  les 5 cartes face visible
     * @param deckSize     nombre de cartes de la pioche
     * @param discardsSize nombre de cartes de la défausse
     * @throws IllegalArgumentException si il n'y a pas le nombre voulu de cartes visibles,
     *                                  si la pioche ou la défausse ont une taille négative
     */

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {

        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT &&
                deckSize >= 0 &&
                discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * methode retournant le nombre total de carte face visible , des cartes se trouvant dans la pioche
     * ainsi que la defausse .
     *
     * @return nombre total de cartes en jeu
     */
    public int totalSize() {
        return (Constants.FACE_UP_CARDS_COUNT + deckSize + discardsSize);
    }

    /**
     * getter de la liste des cartes face visible .
     *
     * @return liste des cartes face visible
     */
    public List<Card> faceUpCards() {
        return (faceUpCards);
    }

    /**
     * methode retourne la carte face visible se trouvant à l'index slot.
     *
     * @param slot index
     * @return la carte correspondant a la carte
     * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0 et le nombre de cartes visibles
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * getter retournant le nombre de cartes de la pioche .
     *
     * @return taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * getter retournant le nombre de cartes de la defausse .
     *
     * @return taille de la defausse
     */
    public int discardsSize() {
        return discardsSize;
    }

    /**
     * methode retournant vrai si le deck est vide .
     *
     * @return booleen
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

}