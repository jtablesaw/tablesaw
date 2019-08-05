package tech.tablesaw.examples;

/** A helper class for writing example code */
public abstract class AbstractExample {

  protected static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  protected static void out() {
    System.out.println();
  }
}
