package tech.tablesaw;

import tech.tablesaw.api.Table;

public class FlightDelayExample extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table t = Table.read().csv("/Users/larrywhite/IdeaProjects/testdata/bigdata/2015.csv");
        out(t);
    }

}
