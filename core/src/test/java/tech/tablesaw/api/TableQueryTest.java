package tech.tablesaw.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableQueryTest {

    @Test
    public void testWithAnd() throws IOException {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.where(
                    t.nCol("approval").isGreaterThan(70).and(
                    t.dateColumn("date").isBefore(LocalDate.of(2001, 9, 11))
        ));
        Table t2 = t.where(
                t.nCol("approval").isGreaterThan(70)
                    .and(t.dateColumn("date").isAfter(LocalDate.of(2001, 9, 11))
        ));
        assertEquals(0, t1.rowCount());
        assertEquals(93, t2.rowCount());
    }
}
