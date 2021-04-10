package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;

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
     * @throws IllegalArgumentException
     * @param initialCards
     * @return
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Retourne les billets du joueur
     *
     * @return
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les billets donnés
     *
     * @param newTickets
     * @return
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes);
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     *
     * @return
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus la carte donnée
     *
     * @param card
     * @return
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> newCard = SortedBag.of(card);
        return withAddedCards(newCard);
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les cartes données
     *
     * @param additionalCards
     * @return
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes);
    }

    /**
     * Retourne vrai si le joueur peut s'emparer de la route donnée
     *
     * @param route
     * @return
     */
    public boolean canClaimRoute(Route route) {
        return route.length() <= carCount() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour prendre possession de la route donnée
     *
     * @throws IllegalArgumentException
     * @param route
     * @return
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(route.length() <= carCount());
        List<SortedBag<Card>> listOfCards = new ArrayList<>();
        for (SortedBag<Card> bagOfCards : route.possibleClaimCards()) {
            if (cards.contains(bagOfCards)) {
                listOfCards.add(bagOfCards);
            }
        }
        return listOfCards;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives
     *
     * @throws IllegalArgumentException
     * @param additionalCardsCount
     * @param initialCards
     * @param drawnCards
     * @return
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {
        //TODO constantes
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
        if (buildedCards.size() >= additionalCardsCount) {
            finalList = new ArrayList<>(buildedCards.subsetsOfSize(additionalCardsCount));
        }
        finalList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return finalList;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
     *
     * @param route
     * @param claimCards
     * @return
     */
    //TODO Vérifier que les cartes sont en possession du joueur, ne pas le faire pour l'instant
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes);
        newRoutes.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), newRoutes);
    }

    /**
     * Retourne le nombre de points obtenus par le joueur grâce à ses billets
     *
     * @return
     */
    //TODO optimiser?
    public int ticketPoints() {
        int indexMax = 0;
        for (Route r : routes) {
            if (indexMax < Math.max(r.station1().id(), r.station2().id())) {
                indexMax = Math.max(r.station1().id(), r.station2().id());
            }
        }
        StationPartition.Builder stationPartitonProfonde = new StationPartition.Builder(indexMax + 1);
        for (Route r : routes) {
            stationPartitonProfonde.connect(r.station1(), r.station2());
        }
        StationPartition stationPartitionAplatie = stationPartitonProfonde.build();
        int ticketPt = 0;
        for (Ticket t : tickets) {
            ticketPt = ticketPt + t.points(stationPartitionAplatie);
        }
        return ticketPt;
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie
     *
     * @return
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
