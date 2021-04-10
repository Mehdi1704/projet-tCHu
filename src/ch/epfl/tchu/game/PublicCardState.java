package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur public permettant d'initialiser les cartes à l'etat connu
     *
     * @throws IllegalArgumentException si il n'y a pas le nombre voulu de cartes visibles,
     *                                  si la pioche ou la défausse ont une taille négative
     * @param faceUpCards les 5 cartes face visible
     * @param deckSize nombre de cartes de la pioche
     * @param discardsSize nombre de cartes de la défausse
     *
     */

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {

        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT &&
                deckSize >= 0 &&
                discardsSize >= 0);
        this.faceUpCards  = List.copyOf(faceUpCards);
        this.deckSize     = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * methode retournant le nombre total de carte face visible , des cartes se trouvant dans la pioche
     * ainsi que la defausse .
     *
     * @return
     */
    public int totalSize() {
        return (Constants.FACE_UP_CARDS_COUNT + deckSize + discardsSize);
    }

    /**
     * retourne une liste des cartes face visible .
     *
     * @return
     */
    public List<Card> faceUpCards() {
        return (faceUpCards);
    }

    /**
     * methode retourne la carte face visible se trouvant à l'index slot.*
     *
     * @throws IndexOutOfBoundsException
     * @param slot
     * @return
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * getter retournant le nombre de cartes de la pioche .
     *
     * @return
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * getter retournant le nombre de cartes de la defausse .
     *
     * @return
     */
    public int discardsSize() {
        return discardsSize;
    }

    /**
     * methode retournant vrai si le deck est vide .
     *
     * @return
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

}