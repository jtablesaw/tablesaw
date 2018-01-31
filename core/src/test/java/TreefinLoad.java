import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericSummaryTable;
import tech.tablesaw.aggregate.SummaryFunction;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TreefinLoad {
    
    public static void main(String[] args) throws IOException {
        Table table = Table.readTable("/Users/stefanostermayr/tests/bigData/tablesaw/log_entries.csv.saw");
        System.out.println(table.structure().print());
        
        Table table2 = table.selectWhere(table. dateColumn("C0").isAfter(LocalDate.parse("2018-01-01")));
        
        
    }
    
    
    
    public static void mainZZ(String[] args) throws IOException {
        
        Stopwatch clock = Stopwatch.createStarted();
    
        Table table = Table.readTable("/Users/stefanostermayr/tests/bigData/tablesaw/log_entries.csv.saw");
//        table.columnNames();
        
        table.addIndexColumn("index", 0);
        
    
        System.out.println(table.structure().print());
        System.out.println(table.shape());
        System.out.println(table.print());
        
        
        Table countByTable = table.countBy(table.categoryColumn("C2"));
        
        System.out.println(countByTable.print(50));
        
        
        System.out.println("elapsed " + clock.elapsed(TimeUnit.SECONDS));
    }
    
    
    private static void etlToSAWFile() throws IOException {
        
        Stopwatch clock = Stopwatch.createStarted();
        
        // date, id, remote_user,remote_addr,request_path
        // 2017-04-25|d4505a80-2939-11e7-6593-d6eab5b7b9|getInsurances|93.201.21.141|foobar@gmail.com
        ColumnType[] types = {ColumnType.LOCAL_DATE, ColumnType.SKIP, ColumnType.CATEGORY, ColumnType.SKIP, ColumnType.SKIP};
        Table table = Table.read().csv(CsvReadOptions
                .builder("/Users/stefanostermayr/tests/bigData/log_entries.csv")
//                .builder("/tmp/log_entries_fixed.csv")
                .separator('|')
                .header(false)
                .columnTypes(types)
                );
        
        System.out.println(table.structure().print());
        System.out.println(table.rowCount());
        System.out.println("mem(total) " + Runtime.getRuntime().totalMemory());
        
        System.out.println("elapsed " + clock.elapsed(TimeUnit.MILLISECONDS) + " ms");
        
        String folder = table.save("/Users/stefanostermayr/tests/bigData/tablesaw");
        System.out.println("folder=" + folder);
    }

}
