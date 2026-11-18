package tech.tablesaw.api;

class StringListColumnFuzzTest extends AbstractListColumnTest<String, StringListColumn> {

  @Override
  protected StringListColumn create(String name) {
    return StringListColumn.create(name);
  }

  @Override
  protected String value(int value) {
    return "value-" + value;
  }

  @Override
  protected void appendObject(StringListColumn column, Object value) {
    column.appendObj(value);
  }

  @Override
  protected void assertInvalidObject(StringListColumn column, Object value) {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> column.appendObj(value));
  }

  @Override
  protected Object invalidElement() {
    return 42;
  }
}
