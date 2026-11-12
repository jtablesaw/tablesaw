package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FloatListColumnHumanReadableTest {

  @Test
  public void simpleSortTest() {
    Table articles = articles();
    Table sorted = articles.sortOn("embedding");
    assertArrayEquals(new int[] {3, 1, 2, 0, 4, 5}, sorted.intColumn("id").asIntArray());
  }

  @Test
  public void nullEmbeddingRowsCanSortInEitherOrder() {
    Table articles = articlesWithEqualNullEmbeddings();
    Table sorted = articles.sortOn("embedding");
    FloatListColumn embeddings = (FloatListColumn) sorted.column("embedding");
    assertNull(embeddings.get(0));
    assertNull(embeddings.get(1));
    assertEquals(
        Set.of(4, 5), Set.of(sorted.intColumn("id").get(0), sorted.intColumn("id").get(1)));
  }

  private Table articles() {
    IntColumn ids = IntColumn.create("id");
    FloatListColumn embeddings = FloatListColumn.create("embedding");
    addArticle(ids, embeddings, 0, embedding(0.40f));
    addArticle(ids, embeddings, 1, embedding(0.20f));
    addArticle(ids, embeddings, 2, embedding(0.30f));
    addArticle(ids, embeddings, 3, embedding(0.10f));
    addArticle(ids, embeddings, 4, null);
    addArticle(ids, embeddings, 5, null);
    return Table.create("articles").addColumns(ids, embeddings);
  }

  private Table articlesWithEqualNullEmbeddings() {
    IntColumn ids = IntColumn.create("id");
    FloatListColumn embeddings = FloatListColumn.create("embedding");
    addArticle(ids, embeddings, 4, null);
    addArticle(ids, embeddings, 5, null);
    return Table.create("articles").addColumns(ids, embeddings);
  }

  private void addArticle(IntColumn ids, FloatListColumn embeddings, int id, List<Float> value) {
    ids.append(id);
    embeddings.append(value);
  }

  private List<Float> embedding(float value) {
    return Collections.nCopies(384, value);
  }
}
