package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class FloatParser extends AbstractParser<Float> {

    public FloatParser(ColumnType columnType) {
        super(columnType);
    }

    public FloatParser(FloatColumnType columnType, CsvReadOptions readOptions) {
        super(columnType);
        if (readOptions.missingValueIndicator() != null) {
            missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
        }
    }

    @Override
    public boolean canParse(String s) {
        if (isMissing(s)) {
            return true;
        }
        try {
            Float.parseFloat(AbstractParser.remove(s, ','));
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Float parse(String s) {
        return parseFloat(s);
    }

    @Override
    public float parseFloat(String s) {
        if (isMissing(s)) {
            return FloatColumnType.missingValueIndicator();
        }
        return Float.parseFloat(AbstractParser.remove(s, ','));
    }
}
