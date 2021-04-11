package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;
import static java.util.Objects.requireNonNull;

public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructeur public d'un Public Game State
     *
     * @throws IllegalArgumentException si le nombre de tickets est négatif ou
     *  si on a un nombre de joueurs different de 2
     * @throws NullPointerException si cardState ou currentPlayerId est null
     * @param ticketsCount nombre de tickets
     * @param cardState etat de cartes
     * @param currentPlayerId identité joueur qui joue actuellement
     * @param playerState map representant les etats des joueurs
     * @param lastPlayer identité du dernier joueur
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);

        this.ticketsCount    = ticketsCount;
        this.cardState       = requireNonNull(cardState);
        this.currentPlayerId = requireNonNull(currentPlayerId);
        this.playerState     = playerState;
        this.lastPlayer      = lastPlayer;
    }

    /**
     * Retourne la taille de la pioche de billets
     *
     * @return taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Retourne vrai si la pioche n'est pas vide
     *
     * @return vrai si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive
     *
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes,
     * c-à-d si la pioche et la défausse contiennent entre elles au moins 5 cartes
     *
     * @return booleen
     */
    public boolean canDrawCards() {
        return ((cardState.deckSize() + cardState.discardsSize()) >= INITIAL_TICKETS_COUNT);
    }

    /**
     * Retourne l'identité du joueur actuel
     *
     * @return identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée
     *
     * @param playerId identité du joueur
     * @return etat public du joueur donné en argument
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant
     *
     * @return etat public du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     *
     * @return liste des routes des joueurs
     */
    public List<Route> claimedRoutes() {
        List<Route> newClaimedRoutes = new ArrayList<>(playerState.get(currentPlayerId).routes());
        newClaimedRoutes.addAll(playerState.get(currentPlayerId.next()).routes());
        return newClaimedRoutes;
    }

    /**
     * Retourne l'identité du dernier joueur,
     * ou null si elle n'est pas encore connue
     * car le dernier tour n'a pas commencé
     *
     * @return identité du joueur qui jouera en dernier si elle est connue
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
