package com.deathrayresearch.outlier.app.ui.table;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.Column;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TableView extends VBox {

  private Table table;
  private static final String TAB_DRAG_KEY = "titledpane";
  private ObjectProperty<ColumnView> draggingTab;

  private Map<Table, Integer> tableTabMap = new HashMap<>();

  public TableView(Table table) {

    draggingTab = new SimpleObjectProperty<>();

    this.table = table;
    this.getChildren().add(new TableMenu());
    this.getChildren().add(new Label(table.shape()));

    HBox row = new HBox();
    row.setSpacing(10);

    for(Column column : table.getColumns()) {
      ColumnView columnView = new ColumnView(column);
      columnView.setOnDragDetected(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Dragboard dragboard = columnView.startDragAndDrop(TransferMode.MOVE);
          ClipboardContent clipboardContent = new ClipboardContent();
          clipboardContent.putString(TAB_DRAG_KEY);
          dragboard.setContent(clipboardContent);
          draggingTab.set(columnView);
          event.consume();
        }
      });

      columnView.setOnDragOver(new EventHandler<DragEvent>() {
        @Override
        public void handle(DragEvent event) {
          final Dragboard dragboard = event.getDragboard();
          if (dragboard.hasString()
              && TAB_DRAG_KEY.equals(dragboard.getString())
              && draggingTab.get() != null) {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
          }
        }
      });

      row.setOnDragDropped(new EventHandler<DragEvent>() {
        public void handle(final DragEvent event) {
          System.out.println("ondragdropped");
          Dragboard db = event.getDragboard();
          boolean success = false;
          if (db.hasString()) {
            Pane parent = (Pane) columnView.getParent();
            Object source = event.getGestureSource();
            int sourceIndex = parent.getChildren().indexOf(source);
            int targetIndex = parent.getChildren().indexOf(columnView);
            List<Node> nodes = new ArrayList<>(parent.getChildren());
            if (sourceIndex < targetIndex) {
              Collections.rotate(
                  nodes.subList(sourceIndex, targetIndex + 1), -1);
            } else {
              Collections.rotate(
                  nodes.subList(targetIndex, sourceIndex + 1), 1);
            }
            parent.getChildren().clear();
            parent.getChildren().addAll(nodes);
            success = true;
          } else {
            System.out.println("dragboard has no string");
          }

          event.setDropCompleted(success);
          event.consume();
        }
      });

      row.getChildren().add(columnView);
    }

    row.setOnDragOver(new EventHandler <DragEvent>() {
      public void handle(DragEvent event) {

        System.out.println("onDragOver");
        if (event.getDragboard().hasString()
            && TAB_DRAG_KEY.equals(event.getDragboard().getString())
            && draggingTab.get() != null) {
          event.acceptTransferModes(TransferMode.MOVE);
          event.consume();
      }
    }});

    row.setOnDragEntered(new EventHandler <DragEvent>() {
      public void handle(DragEvent event) {
        if (event.getGestureSource() != row &&
            event.getDragboard().hasString()) {
            System.out.println("OnDragEntered");
        }
        event.consume();
      }
    });

    row.setOnDragExited(new EventHandler <DragEvent>() {
      public void handle(DragEvent event) {
        System.out.println("OnDragExited");
        event.consume();
      }
    });

    this.getChildren().add(row);
  }
}