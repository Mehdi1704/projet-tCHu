package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
/**
 *
 *
 * @author Mehdi Bouchoucha (314843)
 * @author Ali Ridha Mrad (314529)
 */
public final class StationPartition implements StationConnectivity {


    private final int[] links;


    /**
     * @param tabOfLinks tableau d'entier represantant les lien entre les stations
     */
    private StationPartition(int[] tabOfLinks) {

        this.links = tabOfLinks.clone();
    }

    /**
     * Redefinition
     *
     * @param s1 station 1
     * @param s2 station 2
     * @return retourne vrai si la station 1 et la station 2 sont reliées
     */
    @Override
    public boolean connected(Station s1, Station s2) {

        if (s1.id() >= links.length || s2.id() >= links.length) {
            return (s1.id() == s2.id());
        } else {
            return (links[s1.id()] == links[s2.id()]);
        }
    }

    /**
     * builder permettant de construire  le tableau des liens entres les différentes stations /gares .
     */
    public static final class Builder {


        private final int[] tabBuilderOfPartition;

        /**
         * initialisation du tableau , chaque sation est son propre représentant .
         *
         * @param stationCount nombres de stations
         * @throws IllegalArgumentException léve IllegalArgumentException si le nombre de station et négatif .
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            int[] tampBuilderOfPartition = new int[stationCount];

            for (int i = 0; i < stationCount; i++) {
                tampBuilderOfPartition[i] = i;
            }
            this.tabBuilderOfPartition = tampBuilderOfPartition;
        }

        /**
         * le représentant de la station 1 va être relier au représentant de la station 2 .
         *
         * @param s1 station 1
         * @param s2 station 2
         * @return retourne un tableau oû on connecte la station 1 et la station 2 .
         */
        public Builder connect(Station s1, Station s2) {
            tabBuilderOfPartition[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * construit un tableau "aplati" des représantant le lien entre chaque station ,via leur représentant .
         *
         * @return retourne un tableau "applatie" des représantant de chaque gare/station .
         */
        public StationPartition build() {
            for (int i = 0; i < tabBuilderOfPartition.length; i++) {
                tabBuilderOfPartition[i] = representative(i);
            }
            return new StationPartition(tabBuilderOfPartition);
        }

        /**
         * methode auxiliaire permettant d'associer a chaque station , la satation qui la représente
         *
         * @param stationId identificateur de la station
         * @return retourne l'identificateur de la station passée en paramètre
         */
        private int representative(int stationId) {
            int n = stationId;
            while (n != tabBuilderOfPartition[n]) {
                n = tabBuilderOfPartition[n];
            }
            return n;
        }
    }
}
