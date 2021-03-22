package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;

public class PlayerState extends PublicPlayerState{

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
     *
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }
    /**
     *
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }
    /**
     *
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(this.tickets.union(newTickets),this.cards,this.routes);
    }
    /**
     *
     */
    public SortedBag<Card> cards(){
        return cards;
    }
    /**
     *
     */
    public PlayerState withAddedCard(Card card){
        SortedBag<Card> newCard = SortedBag.of(card);
        return withAddedCards(newCard);
    }
    /**
     *
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes);
    }
    /**
     *
     */
    public boolean canClaimRoute(Route route){
        return route.length() <= carCount() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(route.length() <= carCount());
        List<SortedBag<Card>> listOfCards = new ArrayList<>();
        for(SortedBag<Card> bagOfCards : route.possibleClaimCards()) {
            if (cards.contains(bagOfCards)) {
                listOfCards.add(bagOfCards);
            }
        }
        return listOfCards;
    }
    /**
     *
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards){
        Preconditions.checkArgument(1 <= additionalCardsCount && additionalCardsCount <= ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);

        SortedBag.Builder<Card> possibleAdditionalCards = new SortedBag.Builder<>();
        SortedBag<Card> playersActualCards = cards.difference(initialCards);

        for (Card c1 : playersActualCards) {
            if (initialCards.contains(c1) || c1 == Card.LOCOMOTIVE){
                    possibleAdditionalCards.add(c1);
            }
        }
        SortedBag<Card> buildedCards = possibleAdditionalCards.build();
        List<SortedBag<Card>> finalList= new ArrayList<>();
        if (buildedCards.size() >= additionalCardsCount){
            finalList = new ArrayList<>(buildedCards.subsetsOfSize(additionalCardsCount));
        }
        finalList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return finalList;
    }
    /**
     *
     */
    //TODO Vérifier que les cartes sont en possession du joueur, ne pas le faire pour l'instant
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> newRoutes = new ArrayList<>(routes);
        newRoutes.add(route);
        return new PlayerState(this.tickets,this.cards.difference(claimCards),newRoutes);
    }
    /**
     *
     */
    public int ticketPoints(){
        int indexMax = 0;
        for(Route r : routes){
            if(indexMax < Math.max(r.station1().id(),r.station2().id())){
                indexMax = Math.max(r.station1().id(),r.station2().id());
            }
        }
        StationPartition.Builder stationPartitonProfonde = new StationPartition.Builder(indexMax + 1);
        for(Route r : routes){ stationPartitonProfonde.connect(r.station1(), r.station2());}
        StationPartition stationPartitionAplatie = stationPartitonProfonde.build();
        int ticketPt = 0;
        for (Ticket t : tickets){ticketPt = ticketPt + t.points(stationPartitionAplatie);}
        return ticketPt;
    }
    /**
     *
     */
    public int finalPoints(){
        return claimPoints()+ticketPoints();
    }
}
