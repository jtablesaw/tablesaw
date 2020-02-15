package com.test.testing;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

import java.io.IOException;
import java.util.List;


public class test1 {
    public static void main(String[] args) throws IOException {
        System.out.println("Working till here!");
        tech.tablesaw.io.xlsx.XlsxReadOptions options = XlsxReadOptions.builder("/Users/rajat/Documents/Book1.xlsx").build();
        XlsxReader reader = new XlsxReader();
        List<Table> tables = reader.readMultiple(options);

        for (Table table : tables) {
            System.out.println(table.name());

        }

        Table table = reader.read(options);
        System.out.println(table.name());


    }
}
