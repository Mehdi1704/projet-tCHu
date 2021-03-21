package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {

    @Test
    void StationPartitionBuilderException(){
        StationPartition.Builder builder = new StationPartition.Builder(0);

        assertThrows(IllegalArgumentException.class, () -> {
            StationPartition.Builder builderExcept = new StationPartition.Builder(-1);
        });
    }

    @Test
    void StationPartitionCheck(){
        List<Station> listStation = new ArrayList<>();
        List<String> listNom = List.of(
                "Bern", "Delémont", "Fribourg", "Interlaken", "La Chaux-de-Fonds", "Lausanne",
                "Lucerne", "Neuchâtel", "Olten", "Schwyz", "Soleure",
                "Wassen", "Yverdon", "Zoug", "Zürich"
        );

        //---------------------------------------Test de creation des stations---------------------------------------
        for(int i=0; i<=14; i++){
            listStation.add(new Station(i, listNom.get(i)));
            assertEquals(listStation.get(i).toString(), listNom.get(i));
        }
        assertEquals(listStation.size(), listNom.size());

        //--------------------------------------Creation de groupe-------------------------------------------
        List<Station> groupe1 = List.of(listStation.get(5), listStation.get(2), listStation.get(0),listStation.get(3));
        List<Station> groupe2 = List.of(listStation.get(7), listStation.get(10), listStation.get(8));
        List<Station> groupe3 = List.of(listStation.get(6), listStation.get(13), listStation.get(9),listStation.get(11));
        List<List<Station>> listGroupes= List.of(groupe1, groupe2, groupe3);

        //---------------------------------------Build de la Partition---------------------------------------
        StationPartition.Builder builder = new StationPartition.Builder(15);

        builder.connect(groupe1.get(1), groupe1.get(2)); //Fribourg -> Berne
        builder.connect(groupe1.get(0), groupe1.get(1)); //Lausanne -> Fribourg
        builder.connect(groupe1.get(2), groupe1.get(3)); //Berne -> Interlaken

        builder.connect(groupe2.get(0), groupe2.get(1)); //Neuchâtel -> Soleure
        builder.connect(groupe2.get(1), groupe2.get(2)); //Soleure -> Olten

        builder.connect(groupe3.get(0), groupe3.get(1)); //Lucerne -> Zoug
        builder.connect(groupe3.get(2), groupe3.get(3)); //Schwyz -> Wassen
        builder.connect(groupe3.get(1), groupe3.get(2)); //Zoug -> Schwyz

        //-----------------------------------------Test du Build-------------------------------------------
        StationPartition partition= builder.build();

        //Connectiviter des groupes
        for( List<Station> tempGroupe : listGroupes){
            for ( Station i : tempGroupe){
                for ( Station j : tempGroupe){
                    assertTrue(partition.connected(i,j));
                }
            }
        }

        //Non Connectiviter
        for ( Station i : groupe1)
            for ( Station j : groupe2)
                assertFalse(partition.connected(i,j));

        for ( Station i : groupe1)
            for ( Station j : groupe3)
                assertFalse(partition.connected(i,j));

        for ( Station i : groupe2)
            for ( Station j : groupe3)
                assertFalse(partition.connected(i,j));

    }
}
