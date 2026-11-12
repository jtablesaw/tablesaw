package tech.tablesaw.api;

class FloatListColumnFuzzTest extends AbstractListColumnTest<Float, FloatListColumn> {

  @Override
  protected FloatListColumn create(String name) {
    return FloatListColumn.create(name);
  }

  @Override
  protected Float value(int value) {
    return value / 10.0f;
  }

  @Override
  protected void appendObject(FloatListColumn column, Object value) {
    column.appendObj(value);
  }

  @Override
  protected void assertInvalidObject(FloatListColumn column, Object value) {
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> column.appendObj(value));
  }

  @Override
  protected Object invalidElement() {
    return "not a number";
  }
}
