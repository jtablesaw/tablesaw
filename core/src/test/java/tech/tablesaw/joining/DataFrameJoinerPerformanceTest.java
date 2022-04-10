package tech.tablesaw.joining;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static tech.tablesaw.joining.JoinType.FULL_OUTER;
import static tech.tablesaw.joining.JoinType.LEFT_OUTER;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

@Tag("Slow")
@Tag("Flaky")
public class DataFrameJoinerPerformanceTest {

  private static final long SEED = 200L;

  private static Table addFillerColumn(Table table, int numberColumnsToAdd, String prefix) {
    int[] filler = new int[table.rowCount()];
    Arrays.fill(filler, 1);
    IntColumn col = IntColumn.create("temp", filler);
    for (int i = 0; i < numberColumnsToAdd; i++) {
      table.addColumns(col.copy().setName(prefix + "_appendColumn" + i));
    }
    return table;
  }

  private static Table createCustomersTable(int numberCustomers) {
    Table customersTable = Table.create("customers");
    IntColumn customerIds =
        IntColumn.create("customerId", IntStream.range(0, numberCustomers).toArray());
    customersTable.addColumns(customerIds);
    return customersTable;
  }

  private static Table createOrdersTable(int numberOrders, int numberCustomers) {
    Table ordersTable = Table.create("orders");
    Random random = new Random(SEED);
    IntColumn orderCustomerIds =
        IntColumn.create(
            "customerId",
            random
                .doubles()
                .limit(numberOrders)
                .mapToInt(randomDouble -> (int) Math.floor(randomDouble * numberCustomers))
                .toArray());
    ordersTable.addColumns(orderCustomerIds);
    return ordersTable;
  }

  @Test
  public void innerJoinCustomersFirst() {
    int numberOrders = 50_000;
    int numberCustomers = 1_000;
    Table customers = createCustomersTable(numberCustomers);
    addFillerColumn(customers, 5, "customer");
    Table orders = createOrdersTable(numberOrders, numberCustomers);
    addFillerColumn(orders, 5, "order");
    assertTimeout(Duration.ofMillis(500), () -> customers.joinOn("customerId").with(orders).join());
  }

  @Test
  public void innerJoinOrdersFirst() {
    int numberOrders = 50_000;
    int numberCustomers = 1_000;
    Table customers = createCustomersTable(numberCustomers);
    addFillerColumn(customers, 5, "customer");
    Table orders = createOrdersTable(numberOrders, numberCustomers);
    addFillerColumn(orders, 5, "order");
    assertTimeout(Duration.ofSeconds(1), () -> orders.joinOn("customerId").with(customers).join());
  }

  @Test
  public void leftOuterOrdersFirst() {
    int numberOrders = 50_000;
    int numberCustomers = 1_000;
    Table customers = createCustomersTable(numberCustomers);
    addFillerColumn(customers, 5, "customer");
    // Number customers here is larger. Will create rows orders without matching customers.
    Table orders = createOrdersTable(numberOrders, numberCustomers);
    addFillerColumn(orders, 5, "order");
    assertTimeout(
        Duration.ofSeconds(1),
        () -> orders.joinOn("customerId").with(customers).type(LEFT_OUTER).join());
  }

  @Test
  public void leftOuterCustomersFirst() {
    int numberOrders = 50_000;
    int numberCustomers = 1_000;
    Table customers = createCustomersTable(numberCustomers);
    addFillerColumn(customers, 5, "customer");
    // Number customers here is larger. Will create rows orders without matching customers.
    Table orders = createOrdersTable(numberOrders, numberCustomers);
    addFillerColumn(orders, 5, "order");

    assertTimeout(
        Duration.ofSeconds(1),
        () -> customers.joinOn("customerId").with(orders).type(LEFT_OUTER).join());
  }

  @Test
  public void fullOuterJoin() {
    int numberOrders = 50_000;
    int numberCustomers = 1_000;
    Table customers = createCustomersTable(numberCustomers);
    addFillerColumn(customers, 5, "customer");
    // Number customers here is larger. Will create orders without matching customers.
    Table orders = createOrdersTable(numberOrders, 2_000);
    addFillerColumn(orders, 5, "order");

    assertTimeout(
        Duration.ofSeconds(1),
        () -> customers.joinOn("customerId").with(orders).type(FULL_OUTER).join());
  }
}
