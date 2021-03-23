package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;

public final class  Route {

    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id ;
    private final Station station1 ;
    private final Station station2 ;
    private final int length ;
    private final Level level ;
    private final Color color ;

    /**
     * constructeur de la classe initialisant nos valeurs .*
     */

    public Route(String id, Station station1, Station station2, int length, Level level, Color color){

        Preconditions.checkArgument((!(station1.equals(station2))) && (length>=Constants.MIN_ROUTE_LENGTH) &&
                (length<= Constants.MAX_ROUTE_LENGTH) );

        this.id       = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1) ;
        this.station2 = Objects.requireNonNull(station2) ;
        this.length   = length ;
        this.level    = Objects.requireNonNull(level) ;
        this.color    = color ;
    }

    /**
     * nos getter de la classe
     */
    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }

    /**
     * methode retournant une liste composée de station1 et station2
     */
    public List<Station> stations(){
        return (List.of(station1,station2));
    }

    /**
     * methode retournant la station opposée à celle qui est passé en argument.
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1)||station.equals(station2));
        if(station.equals(station1)){
            return station2;
        }else{
            return station1;
        }
    }

    /**
     *
     *  retourne la liste de tous les ensembles de cartes qui pourraient être joués pour pouvoir
     *  s'emparer de la route (tunnel)
     */
    public List<SortedBag<Card>> possibleClaimCards(){

        ArrayList<SortedBag<Card>> tempListOfPossibleClaimCards = new ArrayList<>();

        if(level.equals(Level.OVERGROUND)){
            if(color != null){
                tempListOfPossibleClaimCards.add(SortedBag.of(length,Card.of(color)));
            }else{
                for(Card c : Card.CARS) {
                    tempListOfPossibleClaimCards.add(SortedBag.of(length, c));
                }
            }

        }else{
            if(color != null){
                for(int i = 0; i <= length ; ++i){
                    tempListOfPossibleClaimCards.add(SortedBag.of(length-i, Card.of(color), i, Card.LOCOMOTIVE));
                }
            }else{

                for(int i = 0; i <= length ; ++i){
                    if(i<length) {
                        for (Card c : Card.CARS) {
                            tempListOfPossibleClaimCards.add(SortedBag.of(length - i, c, i, Card.LOCOMOTIVE));
                        }
                    }else{
                        tempListOfPossibleClaimCards.add(SortedBag.of(length , Card.LOCOMOTIVE));
                    }
                }
            }
        }

        return (List.copyOf(tempListOfPossibleClaimCards));

    }

    /**
     * retourne le nombre de carte additionnel à jouer pour pouvoir s'emparer de la route(tunnel)
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument((drawnCards.size() == ADDITIONAL_TUNNEL_CARDS) && level.equals(Level.UNDERGROUND));

        int count = 0 ;
        for(Card c : drawnCards){
            if(claimCards.contains(c) || c.equals(Card.LOCOMOTIVE)){
                count +=1;
            }

        }
        return count;
    }

    /**
     * retourne le nombre de points de construction associé a la longueur de la route .
     */
    public int claimPoints(){
        return (Constants.ROUTE_CLAIM_POINTS.get(this.length));
    }

}

