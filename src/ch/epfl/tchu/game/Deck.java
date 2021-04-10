package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Random;

import static java.util.Collections.shuffle;

public final class Deck<C extends Comparable<C>> {

    private final List<C> listOfCards;
    private final int ZERO_COUNT = 0;

    /**
     * Constructeur privé initialisant notre liste de carte.
     *
     * @param listOfCards Liste des cartes à transformer en Deck
     */
    private Deck(List<C> listOfCards) {
        this.listOfCards = List.copyOf(listOfCards);
    }

    /**
     * Méthode de construction retournant un deck de cartes qui ont été mélangés
     *
     * @param cards Cartes à melanger
     * @param rng variable aléatoire
     * @return Un deck melangé
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> tampOfListOfCards = cards.toList();
        shuffle(tampOfListOfCards, rng);
        return new Deck<>(tampOfListOfCards);
    }

    /**
     * methode retournant la taille du tas
     *
     * @return le nombre de cartes que le tas contient
     */
    public int size() {
        return (listOfCards.size());
    }

    /**
     * methode qui retourne vrai si et seulement si le tas est vide.
     *
     * @return un booleen indiquant si le tas est vide
     */
    public boolean isEmpty() {
        return (listOfCards.isEmpty());
    }

    /**
     * methode qui retourne la carte au sommet du tas
     *
     * @return la carte au sommet du tas
     */
    public C topCard() {
        Preconditions.checkArgument(!listOfCards.isEmpty());
        return (listOfCards.get(ZERO_COUNT));
    }

    /**
     * methode qui retourne un tas identique au récepteur mais sans la carte au sommet
     *
     * @return un deck sans la carte au sommet du tas
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!listOfCards.isEmpty());
        List<C> listWithoutTopCard = listOfCards.subList(1, listOfCards.size());
        return new Deck<>(listWithoutTopCard);
    }

    /**
     * methode qui retourne un multiensemble contenant les count cartes se trouvant au sommet du tas
     *
     * @throws IllegalArgumentException si le count n'est pas entre zero et la taille du tas
     * @param count nombre de cartes à prélever
     * @return Un SortedBag composé des count cartes du sommet du tas
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(ZERO_COUNT <= count && count <= listOfCards.size());
        SortedBag.Builder<C> topCards = new SortedBag.Builder<>();
        for (int i = 0; i < count; i++) topCards.add(listOfCards.get(i));
        return topCards.build();

    }

    /**
     * methode qui retourne un tas identique au récepteur mais sans les count cartes du sommet.
     *
     * @throws IllegalArgumentException si le count n'est pas entre zero et la taille du tas
     * @param count nombre de cartes à prélever
     * @return Deck sans les count cartes du sommet
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(ZERO_COUNT <= count && count <= listOfCards.size());
        return new Deck<>(listOfCards.subList(count, listOfCards.size()));

    }
}