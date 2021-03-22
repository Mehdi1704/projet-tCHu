package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;

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
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);
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

    /**
     * Retourne vrai si la pioche n'est pas vide
     * @return
     */
    public boolean canDrawTickets(){
        return ticketsCount!=0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive
     * @return
     */
    public PublicCardState cardState(){
        return new PublicCardState(cardState.faceUpCards(),cardState.deckSize(), cardState.discardsSize());
    }

    /**
     * retourne vrai ssi il est possible de tirer des cartes,
     * c-à-d si la pioche et la défausse contiennent entre elles au moins 5 cartes
     * @return
     */
    public boolean canDrawCards(){
        int totalCards = cardState.deckSize() + cardState.discardsSize();
        return (totalCards >= INITIAL_TICKETS_COUNT);
    }
    /**
     * Retourne l'identité du joueur actuel
     * @return
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée
     * @param playerId
     * @return
     */
    //TODO verifier
    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant
     * @return
     */
    //TODO verifier
    public PublicPlayerState currentPlayerState(){
        return playerState.get(currentPlayerId());
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     * @return
     */
    //TODO faire
    public List<Route> claimedRoutes(){

        return null;
    }

    /**
     * Retourne l'identité du dernier joueur,
     * ou null si elle n'est pas encore connue
     * car le dernier tour n'a pas commencé
     * @return
     */
    public PlayerId lastPlayer(){
        //TODO null si le dernier tour n'a pas commencé
        return lastPlayer;
    }
}
