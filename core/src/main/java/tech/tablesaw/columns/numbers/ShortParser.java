package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import static tech.tablesaw.api.ColumnType.SHORT;

public class ShortParser extends AbstractParser<Short> {

    public ShortParser(ShortColumnType columnType) {
        super(columnType);
    }

    public ShortParser(ShortColumnType columnType, CsvReadOptions readOptions) {
        super(columnType);
        if (readOptions.missingValueIndicator() != null) {
            missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
        }
    }

    @Override
    public boolean canParse(String str) {
        if (isMissing(str)) {
            return true;
        }
        String s = str;
        try {
            if (s.endsWith(".0")) {
                s = s.substring(0, s.length() - 2);
            }
            Short.parseShort(AbstractParser.remove(s, ','));
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Short parse(String s) {
        return parseShort(s);
    }

    @Override
    public double parseDouble(String s) {
        return parseInt(s);
    }

    @Override
    public short parseShort(String str) {
        if (isMissing(str)) {
            return ShortColumnType.missingValueIndicator();
        }
        String s = str;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        String preparedString = AbstractParser.remove(s, ',');
        try {
            return Short.parseShort(preparedString);
        } catch (NumberFormatException e) {
            long intResult;

            intResult = Long.parseLong(s);

            if (intResult > Short.MAX_VALUE) {
                // if it hasn't blown up parsing as a long, we know it was too big for short, but is a real whole number
                // now we can upgrade our data holder to one that holds integers and try again
                throw new NumberOutOfRangeException(str, intResult, SHORT);
            }
            else throw e;
        }
     }
}
