package tech.tablesaw.perf;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static tech.tablesaw.joining.JoinType.FULL_OUTER;
import static tech.tablesaw.joining.JoinType.LEFT_OUTER;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

@Tag("Slow")
@Tag("Flaky")
public class DataFrameJoinerPerformanceTest {

  private static final long SEED = 200L;

  private static final int CUSTOMER_COUNT = 1_000;
  private static final int ORDER_COUNT = 10_000;

  private static final int CUSTOMER_FILL_COL_COUNT = 5;
  private static final int ORDER_FILL_COL_COUNT = 5;

  private static final int TIME_OUT_MILLIES = 1000;

  private static Table customers;
  private static Table orders;

  private static final Map<Integer, Integer> REGION_MAP = new HashMap<>();

  @BeforeAll
  static void setup() {
    customers = createCustomersTable(CUSTOMER_COUNT);
    orders = createOrdersTable(ORDER_COUNT, CUSTOMER_COUNT);
    addFillerColumns(customers, orders, CUSTOMER_FILL_COL_COUNT, ORDER_FILL_COL_COUNT);
  }

  private static void addFillerColumn(Table table, int numberColumnsToAdd, String prefix) {
    int[] filler = new int[table.rowCount()];
    Arrays.fill(filler, 1);
    IntColumn col = IntColumn.create("temp", filler);
    for (int i = 0; i < numberColumnsToAdd; i++) {
      table.addColumns(col.copy().setName(prefix + "_appendColumn" + i));
    }
  }

  private static Table createCustomersTable(int numberCustomers) {
    Random random = new Random(SEED);

    Table customersTable = Table.create("customers");
    IntColumn customerIds =
        IntColumn.create("customerId", IntStream.range(0, numberCustomers).toArray());
    IntColumn regions = IntColumn.create("region", numberCustomers);
    for (int i = 0; i < numberCustomers; i++) {
      int val = random.nextInt(49);
      REGION_MAP.put(customerIds.get(i), val);
      regions.set(i, val);
    }
    customersTable.addColumns(customerIds, regions);
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

    IntColumn regions = IntColumn.create("region", numberOrders);
    for (int i = 0; i < numberOrders; i++) {
      int customer = orderCustomerIds.getInt(i);
      int region = REGION_MAP.get(customer);
      regions.set(i, region);
    }
    ordersTable.addColumns(orderCustomerIds, regions);
    return ordersTable;
  }

  @Test
  public void innerJoinCustomersFirst() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () -> customers.joinOn("customerId").with(orders).allowDuplicateColumnNames(true).join());
  }

  @Test
  public void innerJoinCustomersFirst2() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () ->
            customers
                .joinOn("customerId", "region")
                .with(orders)
                .allowDuplicateColumnNames(true)
                .join());
  }

  @Test
  public void innerJoinOrdersFirst() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () -> orders.joinOn("customerId").with(customers).allowDuplicateColumnNames(true).join());
  }

  @Test
  public void leftOuterOrdersFirst() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () ->
            orders
                .joinOn("customerId")
                .with(customers)
                .type(LEFT_OUTER)
                .allowDuplicateColumnNames(true)
                .join());
  }

  @Test
  public void leftOuterCustomersFirst() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () ->
            customers
                .joinOn("customerId")
                .with(orders)
                .type(LEFT_OUTER)
                .allowDuplicateColumnNames(true)
                .join());
  }

  @Test
  public void fullOuterJoin() {
    assertTimeout(
        Duration.ofMillis(TIME_OUT_MILLIES),
        () ->
            customers
                .joinOn("customerId")
                .with(orders)
                .type(FULL_OUTER)
                .allowDuplicateColumnNames(true)
                .join());
  }

  private static void addFillerColumns(
      Table customers, Table orders, int customerFillCols, int orderFillCols) {
    addFillerColumn(customers, customerFillCols, "customer");
    addFillerColumn(orders, orderFillCols, "order");
  }
}
