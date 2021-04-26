package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import ch.epfl.test.TestRandomizer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.EnumMap;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxyTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    US_ASCII));
            Player playerProxy = new RemotePlayerProxy(socket,r,w);

            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);

            Map<PlayerId, Info> playerInformation = new EnumMap<>(PlayerId.class);
            playerNames.forEach((player, info) -> playerInformation.put(player, new Info(info)));
            playerProxy.receiveInfo(playerInformation.get(PLAYER_1).willPlayFirst());
        }
        System.out.println("Server done!");
    }

}
