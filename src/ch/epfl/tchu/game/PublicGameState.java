package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;
//ALOULOU YA MIBOUN
//YAATIK ASBA
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId,PublicPlayerState> playerState;
    private final PlayerId lastPlayer;
    //TODO IllegalArgumentException si la taille de la pioche est strictement négative ou
    // si playerState ne contient pas exactement deux paires clef/valeur,
    // et NullPointerException si l'un des autres arguments (lastPlayer excepté!) est nul.
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                            Map<PlayerId,PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0);
        if (cardState==null || currentPlayerId==null || playerState==null){
            throw new NullPointerException();
        }
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;
    }

    /**
     * Retourne la taille de la pioche de billets
     * @return
     */
    public int ticketsCount(){
        return ticketsCount;
    }
    public boolean canDrawTickets(){
        return false;
    }
    public PublicCardState cardState(){
        return new PublicCardState(null,0,0);
    }
    public boolean canDrawCards(){
        return false;
    }
    /**
     * Retourne l'identité du joueur actuel
     * @return
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }
    public PublicPlayerState playerState(PlayerId playerId){
        return null;
    }
    public PublicPlayerState currentPlayerState(){
        return null;
    }
    public List<Route> claimedRoutes(){
        return null;
    }
    public PlayerId lastPlayer(){
        return null;
    }
}
