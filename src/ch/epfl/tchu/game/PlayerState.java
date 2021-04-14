package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;

/**
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;


    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = List.copyOf(routes);

    }

    /**
     * Retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées
     *
     * @param initialCards les cartes initiales .
     * @return nouvel état de PlayerState auquel les cartes initiales données ont été distribuées au joueur .
     * @throws IllegalArgumentException si on a pas le bon nombre de cartes initiales
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Retourne les billets du joueur
     *
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les billets donnés
     *
     * @param newTickets nouveaux billets à ajouter
     * @return nouvel état de PlayerState oû le joueur possède en plus newTickets .
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes);
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     *
     * @return les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus la carte donnée
     *
     * @param card carte à ajouter au joueur
     * @return nouvel état de playerState dans lequel on ajoute card .
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> newCard = SortedBag.of(card);
        return withAddedCards(newCard);
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les cartes données
     *
     * @param additionalCards cartes à ajouter .
     * @return nouvel état de playerState dans lequel on ajoute additionalCards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes);
    }

    /**
     * Retourne vrai si le joueur peut s'emparer de la route donnée
     *
     * @param route route qu'on souhaite capturer
     * @return vrai si on peut la capturer .
     */
    public boolean canClaimRoute(Route route) {
        return route.length() <= carCount() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour prendre possession de la route donnée
     *
     * @param route route qu'on souhaite capturer
     * @return la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour prendre possession de la route
     * @throws IllegalArgumentException si la longueur de la route depasse le nombre de wagons du joueur
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(route.length() <= carCount());
        List<SortedBag<Card>> listOfCards = new ArrayList<>();
        for (SortedBag<Card> bagOfCards : route.possibleClaimCards()) {
            if (cards.contains(bagOfCards)) listOfCards.add(bagOfCards);
        }
        return listOfCards;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives
     *
     * @param additionalCardsCount nombre de carte Additionnelles
     * @param initialCards         SortedBag des cartes initiales
     * @param drawnCards           SortedBag des cartes piochées
     * @return liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel.
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus),
     *                                  si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents,
     *                                  ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes,
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(1 <= additionalCardsCount && additionalCardsCount <= ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);

        SortedBag.Builder<Card> possibleAdditionalCards = new SortedBag.Builder<>();
        SortedBag<Card> playersActualCards = cards.difference(initialCards);

        for (Card c1 : playersActualCards) {
            if (initialCards.contains(c1) || c1 == Card.LOCOMOTIVE) {
                possibleAdditionalCards.add(c1);
            }
        }
        SortedBag<Card> buildedCards = possibleAdditionalCards.build();
        List<SortedBag<Card>> finalList = new ArrayList<>();
        if (buildedCards.size() >= additionalCardsCount)
            finalList = new ArrayList<>(buildedCards.subsetsOfSize(additionalCardsCount));
        finalList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return finalList;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
     *
     * @param route      qui a été emparer
     * @param claimCards cartes utilisées pour la carpure de la route.
     * @return un nouvel état de PlayerState , ou le joueur a pris possession de route au moyen de claimCards .
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes);
        newRoutes.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), newRoutes);
    }

    /**
     * Retourne le nombre de points obtenus par le joueur grâce à ses billets
     *
     * @return nombre de points obtenus par le joueur grâce à ses billets
     */
    public int ticketPoints() {
        int indexMax = 0;
        for (Route r : routes) {
            if (indexMax < Math.max(r.station1().id(), r.station2().id())) {
                indexMax = Math.max(r.station1().id(), r.station2().id());
            }
        }
        StationPartition.Builder stationPartitonProfonde = new StationPartition.Builder(indexMax + 1);
        routes.forEach(r -> stationPartitonProfonde.connect(r.station1(), r.station2()));
        StationPartition stationPartitionAplatie = stationPartitonProfonde.build();
        int ticketPt = 0;
        for (Ticket t : tickets) ticketPt = ticketPt + t.points(stationPartitionAplatie);
        return ticketPt;
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie
     *
     * @return la totalité des points du joueur à la fin de la partie(sans le bonus)
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
