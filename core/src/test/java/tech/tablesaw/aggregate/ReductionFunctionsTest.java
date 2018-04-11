package tech.tablesaw.aggregate;

import org.junit.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import static tech.tablesaw.aggregate.ReductionFunctions.standardDeviation;
import static tech.tablesaw.aggregate.ReductionFunctions.truths;

public class ReductionFunctionsTest {

    @Test
    public void testT() {

        boolean[] args = {true, false, true, false};
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);

        double[] numbers = {1, 2, 3, 4};
        NumberColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn);

        Table result = table.summarize(booleanColumn, numberColumn, truths, standardDeviation).by(stringColumn);
        System.out.println(result);




    }
}