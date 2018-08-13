package tech.tablesaw.io;


import com.google.common.base.Splitter;
import tech.tablesaw.util.StringUtils;

public class ParsingUtils {

    private static final Splitter underscoreSplitter = Splitter.on('_');

    public static String splitCamelCase(String s) {
        return StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(s),
                ' '
        );
    }

    public static String splitOnUnderscore(String s) {
        return StringUtils.join(
                underscoreSplitter.splitToList(s).toArray(),
                ' '
        );
    }
}
