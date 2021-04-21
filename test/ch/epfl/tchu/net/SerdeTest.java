package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Color;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SerdeTest {

    @Test
    void ofTest(){
        Serde<Integer> intSerde = Serde.of(i -> Integer.toString(i), Integer::parseInt);
        for(int i=0; i< 400; i++){
            assertEquals(Integer.toString(i), intSerde.serialize(i));
            assertEquals(i, intSerde.deserialize(Integer.toString(i)));
        }
    }

    @Test
    void oneOf(){
        Serde<Color> color = Serde.oneOf(Color.ALL);
        for(Color card : Color.ALL){
            assertEquals(Integer.toString(card.ordinal()), color.serialize(card));
            assertEquals(card, color.deserialize(Integer.toString(card.ordinal())));
        }
    }

    @Test
    void oneOfException(){
        assertThrows(NullPointerException.class, () -> Serde.oneOf(null));
        assertThrows(NullPointerException.class, () -> Serde.oneOf(null));

        Serde.oneOf(Color.ALL);
        assertThrows(NullPointerException.class, () -> Serde.oneOf(Color.ALL).serialize(null));
        assertThrows(NumberFormatException.class, () -> Serde.oneOf(Color.ALL).deserialize(null));
        assertThrows(IllegalArgumentException.class, () -> Serde.oneOf(Color.ALL).deserialize(""));
    }

    @Test
    void listOf(){
        Serde<Color> color = Serde.oneOf(Color.ALL);
        Serde<List<Color>> listOfColor = Serde.listOf(color, "+");

        assertEquals(List.of(Color.BLACK, Color.VIOLET), listOfColor.deserialize("0+1"));
        assertEquals("2+3", listOfColor.serialize(List.of(Color.BLUE, Color.GREEN)));

        for(Color clr : Color.ALL){
            List<Color> list = Color.ALL.subList(0, clr.ordinal());
            List<String> listOrdinal = list.stream().map(c -> String.valueOf(c.ordinal())).collect(Collectors.toList());

            assertEquals(list, listOfColor.deserialize(String.join("+", listOrdinal)));
            assertEquals(String.join("+", listOrdinal), listOfColor.serialize(list));
        }

        assertEquals(List.of(), listOfColor.deserialize(""));
        assertEquals("", listOfColor.serialize(List.of()));
    }

    @Test
    void listOfException(){
        assertThrows(NullPointerException.class, () -> Serde.listOf(null, "+"));
        assertThrows(IllegalArgumentException.class, () -> Serde.listOf(Serde.oneOf(Color.ALL), ""));


        Serde.listOf(Serde.oneOf(Color.ALL), "+");
        assertThrows(NullPointerException.class, () -> Serde.listOf(Serde.oneOf(Color.ALL), "+").serialize(null));
        assertThrows(NullPointerException.class, () -> Serde.listOf(Serde.oneOf(Color.ALL), "+").deserialize(null));
    }

    @Test
    void bagOf(){
        Serde<Color> color = Serde.oneOf(Color.ALL);
        Serde<SortedBag<Color>> listOfColor = Serde.bagOf(color, "+");

        assertEquals(SortedBag.of(2, Color.BLACK, 1, Color.VIOLET), listOfColor.deserialize("0+0+1"));
        assertEquals("2+3", listOfColor.serialize(SortedBag.of(1, Color.BLUE, 1, Color.GREEN)));

        for(Color clr : Color.ALL){
            SortedBag<Color> list = SortedBag.of(Color.ALL.subList(0, clr.ordinal()));
            List<String> listOrdinal = list.stream().map(c -> String.valueOf(c.ordinal())).collect(Collectors.toList());

            assertEquals(list, listOfColor.deserialize(String.join("+", listOrdinal)));
            assertEquals(String.join("+", listOrdinal), listOfColor.serialize(list));
        }

        assertEquals(SortedBag.of(), listOfColor.deserialize(""));
        assertEquals("", listOfColor.serialize(SortedBag.of()));
    }

    @Test
    void bagOfException(){
        assertThrows(NullPointerException.class, () -> Serde.bagOf(null, "+"));
        assertThrows(IllegalArgumentException.class, () -> Serde.bagOf(Serde.oneOf(Color.ALL), ""));

        Serde.bagOf(Serde.oneOf(Color.ALL), "+");
        assertThrows(NullPointerException.class, () -> Serde.bagOf(Serde.oneOf(Color.ALL), "+").serialize(null));
        assertThrows(NullPointerException.class, () -> Serde.bagOf(Serde.oneOf(Color.ALL), "+").deserialize(null));
    }
}
