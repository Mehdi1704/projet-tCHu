package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Joueur d'une partie
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public interface Player {

    /**
     * représente les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour .
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * qui est appelée au début de la partie pour communiquer au joueur sa propre identité ownId,
     * ainsi que les noms des différents joueurs, le sien inclus, qui se trouvent dans playerNames,
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    void initConstants(Map<PlayerId, String> playerColors, List<Integer> constants);

    /**
     * qui est appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie;
     * cette information est donnée sous la forme d'une chaîne de caractères, généralement produite par la classe Info
     *
     * @param info information que l'on doit passé au joueur .
     */
    void receiveInfo(String info);

    /**
     * qui est appelée chaque fois que l'état du jeu a changé,
     * pour informer le joueur de la composante publique de ce nouvel état,newState,
     * ainsi que de son propre état, ownState,
     *
     * @param newState nouvel état du jeu .
     * @param ownState état du joueur .
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * *qui est appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués,
     *
     * @param tickets les billets qui vont être distribués en debut de partie.
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * qui est appelée au début de la partie pour demander au joueur lesquels des billets
     * qu'on lui a distribué initialement (via la méthode précédente) il garde
     *
     * @return les billets que le joueur veut garder
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * qui est appelée au début du tour d'un joueur,
     * pour savoir quel type d'action il désire effectuer durant ce tour,
     *
     * @return type d'action que le joueur désire effectuer durant ce tour
     */
    TurnKind nextTurn();


    /**
     * qui est appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde,
     *
     * @param options les billets qu'on propose
     * @return les billets que l'on garde.
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * qui est appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir
     * d'où il désire les tirer: d'un des emplacements contenant une carte face visible—auquel cas
     * la valeur retourne est comprise entre 0 et 4 inclus —,
     * ou de la pioche—auquel cas la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1),
     *
     * @return index de la carte voulant être tirée
     */
    int drawSlot();

    /**
     * qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir de quelle route il s'agit,
     *
     * @return la route que le joueur désire s'emparer .
     */
    Route claimedRoute();

    /**
     * qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     *
     * @return les cartes que le joueur désire initialement utiliser pour tenter de s'emparer d'une route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * qui est appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires,
     * afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument;
     * si le multiensemble retourné est vide,
     * cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités.
     *
     * @return cartes additionelles que le joueur veut jouer .
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);


}
