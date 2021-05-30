package ch.epfl.tchu.net;

import java.util.List;

/**
 * Identifiants de messages correspondant aux types d'action d'un joueur
 *
 * @author Mehdi Bouchoucha(314843)
 * @author Ali Ridha Mrad(314529)
 */
public enum MessageId {

    INIT_PLAYERS,
    INIT_CONSTANTS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

    public static final List<MessageId> ALL = List.of(MessageId.values());

    public static final int COUNT = ALL.size();

}



