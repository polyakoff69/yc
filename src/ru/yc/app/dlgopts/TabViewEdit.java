package ru.yc.app.dlgopts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import ru.yc.app.Config;
import ru.yc.app.FileHandler;
import ru.yc.app.ICallback;
import ru.yc.app.sys.Os;

import java.io.File;
import java.util.ResourceBundle;

public class TabViewEdit extends TabBase implements ICallback {

  private Pane thisPane;
  private ListView<FileHandler> list;
  private ComboBox<String> cbxMode;
  private Label labMode;
  private TextField edCmd, edEnv, edDir, edPar;

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
    pane.add(labTitle,0,0, 2, 1);
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
    tbAdd.setOnAction(evt -> onAdd());
    tb.getItems().add(tbAdd);

    Button tbEdit = new Button("");
    tbEdit.setFocusTraversable(false);
    tbEdit.setTooltip(new Tooltip(cfg.getTextResource().getString("Edit3")));
    image = new Image(getClass().getResourceAsStream("/ico_edit.png"));
    tbEdit.setGraphic(new ImageView(image));
    tbEdit.setOnAction(evt -> onEdit());
    tb.getItems().add(tbEdit);

    Button tbDel = new Button("");
    tbDel.setFocusTraversable(false);
    tbDel.setTooltip(new Tooltip(cfg.getTextResource().getString("delete")));
    image = new Image(getClass().getResourceAsStream("/ico_del.png"));
    tbDel.setGraphic(new ImageView(image));
    tbDel.setOnAction(evt -> onDel());
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

    pane.add(tb,0,1, 2, 1);
    GridPane.setMargin(tb, new Insets(0, 6, 0, 4));

    list = new ListView<>();
    list.setEditable(true);
    pane.add(list,0,2, 2, 1);
    GridPane.setMargin(list, new Insets(0, 6, 0, 4));

    list.setCellFactory(param -> new FHListCell());

    buildForm(cfg, pane);

    thisPane = pane;

    return pane;
  }

  private void buildForm(Config CFG, GridPane pane){
    labMode = new Label(CFG.getTextResource().getString("Viewer")+": ");
    pane.add(labMode,0,3, 1, 1);
    GridPane.setMargin(labMode, new Insets(10, 0, 10, 4));

    cbxMode = new ComboBox<>();
    pane.add(cbxMode,1,3, 1, 1);
    GridPane.setMargin(cbxMode, new Insets(10, 8, 10, 0));

    GridPane pane1 = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane1.setPadding(new Insets(10));
    pane1.setHgap(5);
    pane1.setVgap(5);
    ru.yc.app.gui.BorderedTitledPane grpPane = new ru.yc.app.gui.BorderedTitledPane(CFG.getTextResource().getString("External viewer"), pane1);
    pane.add(grpPane,0,4, 2, 1);

    Label lab = new Label(CFG.getTextResource().getString("Command")+":");
    pane1.add(lab, 0, 0);

    edCmd = new TextField();
    pane1.add(edCmd, 1, 0);
    GridPane.setHgrow(edCmd, Priority.ALWAYS);

    lab = new Label(CFG.getTextResource().getString("Parameters")+":");
    pane1.add(lab, 0, 1);

    edPar = new TextField();
    pane1.add(edPar, 1, 1);
    GridPane.setHgrow(edPar, Priority.ALWAYS);

    lab = new Label(CFG.getTextResource().getString("Environment variables")+": ");
    pane1.add(lab, 0, 2);

    edEnv = new TextField();
    pane1.add(edEnv, 1, 2);
    GridPane.setHgrow(edEnv, Priority.ALWAYS);

    lab = new Label(CFG.getTextResource().getString("Working folder")+":");
    pane1.add(lab, 0, 3);

    edDir = new TextField();
    pane1.add(edDir, 1, 3);
    GridPane.setHgrow(edDir, Priority.ALWAYS);
  }

  public void setPrefW(double w){
    super.setPrefW(w);
    thisPane.setPrefWidth(w);
    list.setPrefWidth(w);
    list.setPrefHeight(150);
  }

  public void onAdd(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<0){
      ix = 0;
    }
    list.getItems().add(ix, new FileHandler());
    list.requestFocus();
  }

  public void onDel(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<0){
      ix = 0;
    }
    list.getItems().remove(ix);
    list.requestFocus();
  }

  public void onEdit(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<0){
      ix = 0;
    }
    list.edit(ix);
    list.requestFocus();
  }

  public Object onAction(Object o){
    return o;
  }

}

class FHListCell extends TextFieldListCell<FileHandler> {
  class Converter extends StringConverter<FileHandler> {
    @Override
    public String toString(FileHandler fh) {
      return fh.toString();
    }
    @Override
    public FileHandler fromString(String string) {
      if (isEmpty()) {
        return new FileHandler(string);
      }
      FileHandler fh = getItem();
      fh.setMask(string);
      return fh;
    }
  };

  public FHListCell() {
    super();
    refreshConverter();
  }

  private void refreshConverter() {
    setConverter(new Converter());
  }

  @Override
  public void updateItem(FileHandler item, boolean empty) {
    super.updateItem(item, empty);
    refreshConverter();
  }
}
