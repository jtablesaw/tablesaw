package tech.tablesaw.plotly;

import com.google.common.base.Stopwatch;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.concurrent.TimeUnit;

import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

public class DrugVisualizations extends AbstractExample {

    private static final ColumnType[] columnTypes = {
            INTEGER,    // 0     npi
            TEXT,       // 1     nppes_provider_last_org_name
            TEXT,       // 2     nppes_provider_first_name
            STRING,     // 3     nppes_provider_city
            STRING,     // 4     nppes_provider_state
            STRING,     // 5     specialty_description
            STRING,     // 6     description_flag
            STRING,     // 7     drug_name
            STRING,     // 8     generic_name
            SHORT,      // 9     bene_count
            SHORT,      // 10    total_claim_count
            FLOAT,      // 11    total_30_day_fill_count
            INTEGER,    // 12    total_day_supply
            FLOAT,      // 13    total_drug_cost
            SHORT,      // 14    bene_count_ge65
            STRING,     // 15    bene_count_ge65_suppress_flag
            SHORT,      // 16    total_claim_count_ge65
            STRING,     // 17    ge65_suppress_flag
            FLOAT,      // 18    total_30_day_fill_count_ge65
            INTEGER,    // 19    total_day_supply_ge65
            FLOAT,      // 20    total_drug_cost_ge65
    };

    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();

        Table scripts = Table.read()
                .csv(CsvReadOptions.builder("../data/PartD_Prescriber_16.txt")
                        .separator('\t')
                        .columnTypes(columnTypes)
                        .build());

        stopwatch.stop();
        System.out.println("loaded " + scripts.shape() + " in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        scripts.setName("prescriptions");

        out(scripts.structure().printAll());
        out(scripts.first(20).printAll());
        out(scripts.shape());

        Table oxy = scripts.where(scripts.stringColumn("drug_name").isEqualTo("OXYCONTIN"));
        out(oxy.first(10));
        out(oxy.shape());
    }
}
