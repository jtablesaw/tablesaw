package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HumanReadableFuzzTest {

  @Test
  public void simpleSortTest() {
    Table articles = articles();
    Table sorted = articles.sortOn("tags");
    assertArrayEquals(new int[] {0, 5, 1, 2, 3, 4}, sorted.intColumn("id").asIntArray());
  }

  @Test
  public void nullListRowsCanSortInEitherOrder() {
    Table articles = articlesWithEqualNullLists();
    Table sorted = articles.sortOn("tags");
    assertEquals(2, sorted.rowCount());
    assertNull(((StringListColumn) sorted.column("tags")).get(0));
    assertNull(((StringListColumn) sorted.column("tags")).get(1));
    assertEquals(
        Set.of(4, 5), Set.of(sorted.intColumn("id").get(0), sorted.intColumn("id").get(1)));
  }

  private Table articles() {
    IntColumn ids = IntColumn.create("id");
    StringListColumn tags = StringListColumn.create("tags");
    addArticle(ids, tags, 0, List.of());
    addArticle(ids, tags, 1, List.of("hi"));
    addArticle(ids, tags, 2, Arrays.asList("hi", null, "hi"));
    addArticle(ids, tags, 3, Arrays.asList(null, null, null));
    addArticle(ids, tags, 4, null);
    addArticle(ids, tags, 5, List.of("alpha", "beta"));
    return Table.create("articles").addColumns(ids, tags);
  }

  private void addArticle(IntColumn ids, StringListColumn tags, int id, List<String> values) {
    ids.append(id);
    tags.append(values);
  }

  private Table articlesWithEqualNullLists() {
    IntColumn ids = IntColumn.create("id");
    StringListColumn tags = StringListColumn.create("tags");
    addArticle(ids, tags, 4, null);
    addArticle(ids, tags, 5, null);
    return Table.create("articles").addColumns(ids, tags);
  }
}
