package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /***
     * constructeur public permettant d'initialiser nos variables .
     * une Liste de carde qui represente les 5 cartes face visible
     * la taille de notre pioche ( c a d le nombre de carte)
     * la taille de notre defausse (c a d le nombre de carte )*
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