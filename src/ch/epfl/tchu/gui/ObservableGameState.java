package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;

public class ObservableGameState {
    //TODO constructeur et setter
    public ObservableGameState(PlayerId PlayerId){

        //PublicGameState PGS = new PublicGameState(0,null,null,null,null);
        //PlayerState PS = new PlayerState(null,null,null);
    }

    public ObservableGameState setState(PublicGameState publicGameState, PlayerState playerState){
        return this;
    }
}
