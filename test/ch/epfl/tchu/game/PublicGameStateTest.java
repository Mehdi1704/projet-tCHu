package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicGameStateTest {

    @Test
    void publicGameStateConstructorThrows() {
        PublicCardState pcs = new PublicCardState(List.of(Card.BLUE,Card.BLUE,Card.BLUE,Card.BLUE,Card.BLUE),20,0) ;
        Map<PlayerId, PublicPlayerState> map = new TreeMap<>();
        map.put(PlayerId.PLAYER_1,new PublicPlayerState(2,5,List.of())) ;
        map.put(PlayerId.PLAYER_2,new PublicPlayerState(3,4,List.of())) ;

        Map<PlayerId, PublicPlayerState> mapFalse = new TreeMap<>();
        mapFalse.put(PlayerId.PLAYER_1,new PublicPlayerState(2,5,List.of())) ;

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(-1,pcs,PlayerId.PLAYER_1,map,null) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(4 ,pcs,PlayerId.PLAYER_1,mapFalse,null) ;
        });
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(4 ,null,PlayerId.PLAYER_1,map,null) ;
        });
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(4 ,pcs,null,map,null) ;
        });
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(4 ,pcs,PlayerId.PLAYER_1,null,null) ;
        });

    }
}
