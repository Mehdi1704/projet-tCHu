package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface Serde<E> {

    /**
     * methode abtraite permettant de sérialiser et retournant la chaîne correspondante
     *
     * @param toSerialize ce qui doit etre sérialiser
     * @return retourne la chaine de caractère correspondante
     */
    String serialize(E toSerialize);

    /**
     * methode abtraite permettant de desérialiser et retournant l'objet correspondant
     *
     * @param toDeserialize ce qui doit etre désérialiser
     * @return retourne l'objet correspondant
     */
    E deserialize(String toDeserialize);

    // classe anonyme dans la methode of .

    /**
     * @param serialize   fonction de sérialisation
     * @param deserialize la fonction de désérialisation
     * @param <E>         paramètre de type de la méthode
     * @return le serde correspondant .
     */
    static <E> Serde<E> of(Function<E, String> serialize, Function<String, E> deserialize) {
        return new Serde<>() {
            @Override
            public String serialize(E toSerialize) {
                return serialize.apply(toSerialize);
            }
                
            @Override
            public E deserialize(String toDeserialize) {
                return deserialize.apply(toDeserialize);
            }
        };
    }

    static <E> Serde<E> oneOf(List<E> liste) {
        //lequel faut il utiliser Preconditions.checkArgument(!liste.isEmpty());
        Objects.requireNonNull(liste);
        return of(e -> String.valueOf(liste.indexOf(e)),
                v -> liste.get(Integer.parseInt(v)));
    }


    static <E> Serde<List<E>> listOf(Serde<E> ourSerde, String separation) {
        //il faut que la separation soit différente de "".
        Preconditions.checkIfEmptyString(separation);
        Objects.requireNonNull(ourSerde);
        return new Serde<>() {
            @Override
            public String serialize(List<E> toSerialize) {
                List<String> tabSerialiser = new ArrayList<>();
                Objects.requireNonNull(toSerialize).forEach(i -> tabSerialiser.add(ourSerde.serialize(i)));
                return String.join(separation, tabSerialiser);
            }

            @Override
            public List<E> deserialize(String toDeserialize) {
                if (toDeserialize.equals("")) {
                    return List.of();
                } else {
                    List<E> tabDeserializer = new ArrayList<>();
                    String[] tab = toDeserialize.split(Pattern.quote(separation), -1);
                    for (String s : tab) {
                        tabDeserializer.add(ourSerde.deserialize(s));
                    }
                    return tabDeserializer;
                }

            }
        };
    }

    static <E extends Comparable<E>> Serde<SortedBag<E>> bagOf(Serde<E> ourSerde, String separation) {
        Preconditions.checkIfEmptyString(separation);
        Objects.requireNonNull(ourSerde);
        Serde<List<E>> sorted = listOf(ourSerde, separation);
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<E> toSerialize) {
                return sorted.serialize(toSerialize.toList());
            }

            @Override
            public SortedBag<E> deserialize(String toDeserialize) {
                return SortedBag.of(sorted.deserialize(toDeserialize));
            }
        };
    }

}
