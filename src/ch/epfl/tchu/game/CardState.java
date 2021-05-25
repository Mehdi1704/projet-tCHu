package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * Etat des cartes lors du jeu
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> deckDiscard;
    private final List<Card> faceUpCards;

    /**
     * Constructeur principal privé appelant le constructeur de la classe PublicCardState
     * initialisant nos parametres
     *
     * @param faceUpCards Cartes face visible
     * @param deck        Cartes de la pioche
     * @param deckDiscard Cartes de la défausse
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> deckDiscard) {
        super(faceUpCards, deck.size(), deckDiscard.size());
        this.deck = deck;
        this.deckDiscard = deckDiscard;
        this.faceUpCards = faceUpCards;
    }

    /**
     * Retourne un etat de carte où les 5 cartes disposées faces visibles
     * sont les 5 premières du tas passé en argument, la pioche est quant à elle
     * constituée des cartes du tas restantes, et la défausse est vide
     *
     * @param deck Cartes de la pioche
     * @return Un nouveau CardState
     * @throws IllegalArgumentException S'il y a plus de cartes face visible que dans le Deck
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(FACE_UP_CARDS_COUNT).toList(), deck.withoutTopCards(FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * Methode retournant un ensemble de cartes identique à l'exception de que la carte face
     * visible de l'index slot va être remplacé par celle se trouvant au sommet de la pioche
     *
     * @param slot Index de la carte face visible
     * @return Un nouvel etat avec la carte remplacée
     * @throws IndexOutOfBoundsException Si l'index n'est pas compris dans les cartes visibles
     * @throws IllegalArgumentException  Si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());
        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot, topDeckCard());

        return new CardState(newFaceUpCards, deck.withoutTopCard(), deckDiscard);

    }

    /**
     * methode qui retourne la carte se trouvant au sommet de la pioche
     *
     * @return La carte du sommet de la pioche
     * @throws IllegalArgumentException Si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * methode retournant un ensemble de cartes identique au recpeteur
     * mais sans la carte au sommet de la pioche
     *
     * @return Un nouvel etat sans la carte du sommet de la pioche
     * @throws IllegalArgumentException Si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return (new CardState(faceUpCards, deck.withoutTopCard(), deckDiscard));
    }

    /**
     * methode qui retourne un ensemble de cartes identique au recpeteur
     * si ce n'est que les cartes de la défausse ont été mélangées et placées
     * à la place de la pioche et une defausse vide.
     *
     * @param rng variable aléatoire
     * @return Un nouvel etat remplacant la pioche par la défausse
     * @throws IllegalArgumentException Si la pioche n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards, Deck.of(deckDiscard, rng), SortedBag.of());
    }

    /**
     * methode qui retourne un ensemble de carte identique au récpeteur
     * mais en ajoutant un ensemble de cartes à la defausse
     *
     * @param additionalDiscards Cartes à ajouter à la défausse
     * @return Un nouvel état avec une défaussse modifiée
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards, deck, deckDiscard.union(additionalDiscards));
    }
}