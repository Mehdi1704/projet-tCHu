package ch.epfl.tchu.net;

import java.util.List;

public enum MessageId {

    INIT_PLAYERS,
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
    // peut etre faut il renommer la methode ALL
    /**
     * liste de tous les type de messageID .
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    public static final int COUNT = ALL.size();

}



