package ru.yc.app.dlgopts;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import ru.yc.app.Config;
import ru.yc.app.ICallback;

import java.util.ResourceBundle;

public class TabEmpty extends TabBase implements ICallback {
  public Region buildTab(ResourceBundle rs, DlgOptions parent){
    return null;
  }

  public Region buildTab(ResourceBundle rs, DlgOptions3 parent, String title){
    Config cfg = Config.get();

    GridPane pane = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane.setPadding(new Insets(10));
    pane.setHgap(5);
    pane.setVgap(5);
    // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

    Label labTitle = getTitleLabel(title);
    pane.add(labTitle,0,0, 2, 1);
    GridPane.setMargin(labTitle, new Insets(2, 0, 2, 4));

    return pane;
  }

  public Object onAction(Object o){
    return o;
  }
}
