package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity {


    private final int[] links;


    private StationPartition(int[] tabOfLinks){

        this.links =tabOfLinks.clone() ;
    }

    @Override
    public boolean connected(Station s1, Station s2) {

        if(s1.id() >= links.length || s2.id() >= links.length) {
            return (s1.id() == s2.id());
        }else{
            return (links[s1.id()] == links[s2.id()]);
        }
    }

    public static final class Builder {


        private final int[] tabBuilderOfPartition;

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            int[] tampBuilderOfPartition = new int[stationCount];

            for( int i=0 ; i < stationCount ; i++){
                tampBuilderOfPartition[i]=i;
            }
            this.tabBuilderOfPartition =  tampBuilderOfPartition;
        }

        public Builder connect(Station s1, Station s2){
            tabBuilderOfPartition[representative(s1.id())] = representative(s2.id());
            return this;
        }

        public StationPartition build(){
            for (int i = 0; i < tabBuilderOfPartition.length ; i++) {
                tabBuilderOfPartition[i] = representative(i);
            }
            return new StationPartition(tabBuilderOfPartition);
        }

        private int representative (int stationId){
            int n = stationId;
            while( n!= tabBuilderOfPartition[n]){
                n = tabBuilderOfPartition[n];
            }
            return n ;
        }
    }
}
