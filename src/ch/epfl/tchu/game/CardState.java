package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;
import static java.util.Collections.shuffle;

public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> deckDiscard;
    private final List<Card> faceUpCards;

    /**
     * constructeur principal privee appelant le constructeur de la classe PublicCardState
     * initialisant nos parametres
     *
     * @param faceUpCards
     * @param deck
     * @param deckDiscard
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> deckDiscard) {
        super(faceUpCards, deck.size(), deckDiscard.size());
        this.deck = deck;
        this.deckDiscard = deckDiscard;
        this.faceUpCards = faceUpCards;
    }

    /**
     * retourne un etat de carte où les 5 cartes disposées faces visibles sont les 5 premières du tas passé en argument,
     * la pioche est quant à elle constituée des cartes du tas restantes, et la défausse est vide
     *
     * @param deck
     * @return
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(FACE_UP_CARDS_COUNT).toList(), deck.withoutTopCards(FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * Methode retournant un ensemble de cartes identique à l'exception de que la carte face
     * visible de l'index slot va être remplacé par celle se trouvant au sommet de la pioche
     *
     * @param slot
     * @return
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());
        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot, topDeckCard());

        return (new CardState(newFaceUpCards, deck.withoutTopCard(), deckDiscard));

    }

    /**
     * methode qui retourne la carte se trouvant au sommet de la pioche
     *
     * @return
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * methode retournant un ensemble de cartes identique au recpeteur mais sans la carte au sommet de la pioche .
     *
     * @return
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return (new CardState(faceUpCards, deck.withoutTopCard(), deckDiscard));
    }

    /**
     * methode qui retourne un ensemble de cartes identique au recpeteur
     * si ce n'est que les cartes de la défausse ont été mélangées et placées à la place de la pioche
     * et une defausse vide.
     *
     * @param rng
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards, Deck.of(deckDiscard, rng), SortedBag.of());
    }

    /**
     * methode qui retourne un ensemble de carte indentique au recpeteur
     * mais en ajoutant via la methode union un ensemble de carte à la defausse .
     *
     * @param additionalDiscards
     * @return
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards, deck, deckDiscard.union(additionalDiscards));
    }
}