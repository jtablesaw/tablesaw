package tech.tablesaw.plotly;

import tech.tablesaw.AbstractExample;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import static tech.tablesaw.api.ColumnType.*;

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
            INTEGER,    // 9     bene_count
            INTEGER,    // 10    total_claim_count
            FLOAT,      // 11    total_30_day_fill_count
            INTEGER,    // 12    total_day_supply
            FLOAT,      // 13    total_drug_cost
            INTEGER,    // 14    bene_count_ge65
            STRING,     // 15    bene_count_ge65_suppress_flag
            INTEGER,    // 16    total_claim_count_ge65
            STRING,     // 17    ge65_suppress_flag
            FLOAT,      // 18    total_30_day_fill_count_ge65
            INTEGER,    // 19    total_day_supply_ge65
            FLOAT,      // 20    total_drug_cost_ge65
    };

    public static void main(String[] args) throws Exception {


        Table scripts = Table.read()
                .csv(CsvReadOptions.builder("../data/PartD_Prescriber_16.txt")
                        .separator('\t')
                        .columnTypes(columnTypes)
                        .build());

        scripts.setName("prescriptions");

        out(scripts.structure().printAll());
        out(scripts.first(20).printAll());
        out(scripts.shape());

        Table oxy = scripts.where(scripts.stringColumn("drug_name").isEqualTo("OXYCONTIN"));
        out(oxy.first(10));
        out(oxy.shape());
    }
}
