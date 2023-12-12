package ru.yc.app.dlgopts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ru.yc.app.Config;
import ru.yc.app.ICallback;
import ru.yc.app.sys.Os;

import java.util.ResourceBundle;

public class TabViewEdit extends TabBase implements ICallback {

  private Pane thisPane;
  private ListView<String> list;

  public Region buildTab(ResourceBundle rs, DlgOptions parent){
    return null;
  }

  public Region buildTab(ResourceBundle rs, DlgOptions3 parent, String title){
    Config cfg = Config.get();

    GridPane pane = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane.setPadding(new Insets(10));
    pane.setHgap(5);
    pane.setVgap(0);
    // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

    Label labTitle = getTitleLabel(title);
    pane.add(labTitle,0,0, 1, 1);
    GridPane.setMargin(labTitle, new Insets(2, 5, 2+5, 4));

    ToolBar tb = new ToolBar();
    tb.getStyleClass().add("file-panel-toolbar");

    Label labInfo = new Label(cfg.getTextResource().getString("File_mask_prior")+" ");
    BorderPane p = new BorderPane();
    p.setLeft(labInfo);
    HBox hb = new HBox();
    hb.setHgrow(p, Priority.ALWAYS);
    labInfo.setAlignment(Pos.BOTTOM_LEFT);
    hb.setFillHeight(true);
    hb.setAlignment(Pos.BOTTOM_LEFT);
    tb.getItems().add(p);
    BorderPane.setMargin(labInfo, new Insets(6,4,0,4));

    Button tbAdd = new Button("");
    tbAdd.setFocusTraversable(false);
    tbAdd.setTooltip(new Tooltip(cfg.getTextResource().getString("Create")));
    Image image = new Image(getClass().getResourceAsStream("/ico_new.png"));
    tbAdd.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbAdd);

    Button tbEdit = new Button("");
    tbEdit.setFocusTraversable(false);
    tbEdit.setTooltip(new Tooltip(cfg.getTextResource().getString("Edit3")));
    image = new Image(getClass().getResourceAsStream("/ico_edit.png"));
    tbEdit.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbEdit);

    Button tbDel = new Button("");
    tbDel.setFocusTraversable(false);
    tbDel.setTooltip(new Tooltip(cfg.getTextResource().getString("delete")));
    image = new Image(getClass().getResourceAsStream("/ico_del.png"));
    tbDel.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbDel);

    Button tbUp = new Button("");
    tbUp.setFocusTraversable(false);
    tbUp.setTooltip(new Tooltip(cfg.getTextResource().getString("Move up")));
    image = new Image(getClass().getResourceAsStream("/ico_arru.png"));
    tbUp.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbUp);

    Button tbDn = new Button("");
    tbDn.setFocusTraversable(false);
    tbDn.setTooltip(new Tooltip(cfg.getTextResource().getString("Move down")));
    image = new Image(getClass().getResourceAsStream("/ico_arrd.png"));
    tbDn.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbDn);

    tb.setFocusTraversable(false);

    pane.add(tb,0,1, 1, 1);
    GridPane.setMargin(tb, new Insets(0, 6, 0, 4));

    list = new ListView<>();
    pane.add(list,0,2, 1, 1);
    GridPane.setMargin(list, new Insets(0, 6, 0, 4));

    thisPane = pane;

    return pane;
  }

  public void setPrefW(double w){
    super.setPrefW(w);
    thisPane.setPrefWidth(w);
    list.setPrefWidth(w);
    list.setPrefHeight(340);
  }

  public Object onAction(Object o){
    return o;
  }
}
