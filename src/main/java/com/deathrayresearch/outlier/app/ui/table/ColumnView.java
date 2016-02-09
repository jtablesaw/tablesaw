package com.deathrayresearch.outlier.app.ui.table;

import com.deathrayresearch.outlier.columns.Column;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 */
public class ColumnView extends VBox {

  private Column column;
  private String columnId = "A";

  public ColumnView(Column column) {
    this.column = column;
    setSpacing(3);
    setAlignment(Pos.CENTER);
    setMinWidth(120);

    //setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    final String dataFormat = "-fx-background-color: #69BACF;\n";

    final String columnFormat =
          "-fx-border-color: grey;\n"
        + "-fx-border-insets: 5;\n"
        + "-fx-border-width: 3;\n"
        + "-fx-border-style: solid;\n"
        + "-fx-background-color: #AECED7;\n";
    setStyle(columnFormat);

    getChildren().add(new Label(columnId));
    VBox header = new VBox(10);
    Label columnName = new Label(column.name());
    Label columnType = new Label(column.type().name());
    header.getChildren().add(columnName);
    header.getChildren().add(columnType);
    header.setStyle(dataFormat);
    header.setAlignment(Pos.CENTER);
    getChildren().add(header);

    VBox body = new VBox(10);
    body.setAlignment(Pos.CENTER);
    body.setMinWidth(100);
    body.setMinHeight(340);
    body.setStyle(dataFormat);
    getChildren().add(body);


    VBox footer = new VBox(10);
    footer.setAlignment(Pos.CENTER);
    footer.setStyle(dataFormat);
    footer.setMinWidth(100);
    footer.setMinHeight(160);
    footer.getChildren().add(new Text(column.summary().print()));
    getChildren().add(footer);

  }
}
