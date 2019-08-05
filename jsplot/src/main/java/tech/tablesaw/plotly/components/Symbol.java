package tech.tablesaw.plotly.components;

/**
 * symbol ( "circle-open" | "circle-dot" | "circle-open-dot" "square-open" | "square-dot" |
 * "square-open-dot" "diamond-open" | "diamond-dot" | "diamond-open-dot" "cross-open" | "cross-dot"
 * | "cross-open-dot" "x-open" | "x-dot" | "x-open-dot" "triangle-up-open" | "triangle-up-dot" |
 * "triangle-up-open-dot" "triangle-down-open" | "triangle-down-dot" | "triangle-down-open-dot"
 * "triangle-left-open" | "triangle-left-dot" | "triangle-left-open-dot" "triangle-right-open" |
 * "triangle-right-dot" | "triangle-right-open-dot" "triangle-ne-open" | "triangle-ne-dot" |
 * "triangle-ne-open-dot" "triangle-se-open" | "triangle-se-dot" | "triangle-se-open-dot"
 * "triangle-sw-open" | "triangle-sw-dot" | "triangle-sw-open-dot" "triangle-nw-open" |
 * "triangle-nw-dot" | "triangle-nw-open-dot"
 *
 * <p>"pentagon-open" | "pentagon-dot | "pentagon-open-dot" "hexagon-open" | "hexagon-dot" |
 * "hexagon-open-dot" "hexagon2-open" | "hexagon2-dot" | "hexagon2-open-dot" "octagon-open" |
 * "octagon-dot" | "octagon-open-dot" "star-open" | "star-dot" | "star-open-dot" "hexagram-open" |
 * "hexagram-dot" | "hexagram-open-dot" "star-triangle-up-open" | "star-triangle-up-dot" |
 * "star-triangle-up-open-dot" "star-triangle-down-open" | "star-triangle-down-dot" |
 * "star-triangle-down-open-dot" "star-square-open" | "star-square-dot" | "star-square-open-dot"
 * "star-diamond-open" | "star-diamond-dot" | "star-diamond-open-dot"
 *
 * <p>"diamond-tall-open" | "diamond-tall-dot" | "diamond-tall-open-dot" "diamond-wide-open" |
 * "diamond-wide-dot" | "diamond-wide-open-dot" "hourglass-open" "bowtie-open" "circle-cross-open"
 * "circle-x-open" "square-cross-open" "square-x-open" "diamond-cross-open" "diamond-x-open"
 *
 * <p>"cross-thin-open" "x-thin-open" "asterisk-open" "hash-open" "hash-dot" "hash-open-dot"
 *
 * <p>"y-up-open" "y-down-open" "y-left-open" "y-right-open"
 *
 * <p>"line-ew-open" "line-ns-open" "line-ne-open" "line-nw-open"
 *
 * <p>default: "circle"
 *
 * <p>Sets the marker symbol type. Adding 100 is equivalent to appending "-open" to a symbol name.
 * Adding 200 is equivalent to appending "-dot" to a symbol name. Adding 300 is equivalent to
 * appending "-open-dot" or "dot-open" to a symbol name.
 */
public enum Symbol {
  CIRCLE("circle"),
  SQUARE("square"),
  DIAMOND("diamond"),
  CROSS("cross"),
  X("x"),
  TRIANGLE_UP("triangle-up"),
  TRIANGLE_DOWN("triangle-down"),
  TRIANGLE_LEFT("triangle-left"),
  TRIANGLE_RIGHT("triangle-right"),
  TRIANGLE_NE("triangle-ne"),
  TRIANGLE_SE("triangle-se"),
  TRIANGEL_SW("triangle-sw"),
  TRIANGLE_NW("triangle-nw"),

  PENTAGON("pentagon"),
  HEXAGON("hexagon"),
  HEXAGON2("hexagon2"),
  OCTAGON("octagon"),
  STAR("star"),
  HEXAGRAM("hexagram"),

  STAR_TRIANGLE_UP("star-triangle-up"),
  STAR_TRIANGLE_DOWN("star-triangle-down"),
  STAR_SQUARE("star-square"),
  STAR_DIAMOND("star-diamond"),

  DIAMOND_TALL("diamond-tall"),
  DIAMOND_WIDE("diamond-wide"),
  HOURGLASS("hourglass"),
  BOWTIE("bowtie"),

  CIRCLE_CROSS("circle-cross"),
  CIRCLE_X("circle-x"),
  SQUARE_CROSS("square-cross"),
  SQUARE_X("square-x"),
  DIAMOND_CROSS("diamond-cross"),
  DIAMOND_X("diamond-x"),

  CROSS_THIN("cross-thin"),
  X_THIN("x-thin"),
  ASTERISK("asterisk"),
  HASH("hash"),

  Y_UP("y-up"),
  Y_DOWN("y-down"),
  Y_LEFT("y-left"),
  Y_RIGHT("y-right"),

  LINE_EW("line-ew"),
  LINE_NS("line-ns"),
  LINE_NE("line-ne"),
  LINE_NW("line-sw");

  private final String value;

  Symbol(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
