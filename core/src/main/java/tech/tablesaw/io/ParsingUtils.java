package tech.tablesaw.io;

import org.apache.commons.lang3.StringUtils;

public class ParsingUtils {

    public static String splitCamelCase(String s) {
        return StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(s),
                ' '
        );
    }

    public static String splitOnUnderscore(String s) {
        return StringUtils.join(
                StringUtils.split(s, '_'),
                ' '
        );
    }
}
