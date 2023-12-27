package ru.yc.app.dlgopts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Pair;
import javafx.util.StringConverter;
import ru.yc.app.Config;
import ru.yc.app.FileHandler;
import ru.yc.app.ICallback;
import ru.yc.app.util.Str;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TabViewEdit extends TabBase implements ICallback {

  private Pane thisPane;
  protected ListView<FileHandler> list;
  private ComboBox<Option> cbxMode;
  private Label labMode;
  private TextField edCmd, edEnv, edDir, edPar;
  private Button tbAdd, tbEdit, tbDel, tbUp, tbDn;
  protected boolean bViewers = true;
  protected Map<String, Option> mOpts = new HashMap<>();
  protected FileHandler editFH = null;

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

    tbAdd = new Button("");
    tbAdd.setFocusTraversable(false);
    tbAdd.setTooltip(new Tooltip(cfg.getTextResource().getString("Create")));
    Image image = new Image(getClass().getResourceAsStream("/ico_new.png"));
    tbAdd.setGraphic(new ImageView(image));
    tbAdd.setOnAction(evt -> onAdd());
    tb.getItems().add(tbAdd);

    tbEdit = new Button("");
    tbEdit.setFocusTraversable(false);
    tbEdit.setTooltip(new Tooltip(cfg.getTextResource().getString("Edit3")));
    image = new Image(getClass().getResourceAsStream("/ico_edit.png"));
    tbEdit.setGraphic(new ImageView(image));
    tbEdit.setOnAction(evt -> onEdit());
    tb.getItems().add(tbEdit);

    tbDel = new Button("");
    tbDel.setFocusTraversable(false);
    tbDel.setTooltip(new Tooltip(cfg.getTextResource().getString("delete")));
    image = new Image(getClass().getResourceAsStream("/ico_del.png"));
    tbDel.setGraphic(new ImageView(image));
    tbDel.setOnAction(evt -> onDel());
    tb.getItems().add(tbDel);

    tbUp = new Button("");
    tbUp.setFocusTraversable(false);
    tbUp.setTooltip(new Tooltip(cfg.getTextResource().getString("Move up")));
    image = new Image(getClass().getResourceAsStream("/ico_arru.png"));
    tbUp.setGraphic(new ImageView(image));
    tbUp.setOnAction(evt -> onUp());
    tb.getItems().add(tbUp);

    tbDn = new Button("");
    tbDn.setFocusTraversable(false);
    tbDn.setTooltip(new Tooltip(cfg.getTextResource().getString("Move down")));
    image = new Image(getClass().getResourceAsStream("/ico_arrd.png"));
    tbDn.setGraphic(new ImageView(image));
    tbDn.setOnAction(evt -> onDn());
    tb.getItems().add(tbDn);

    tb.setFocusTraversable(false);

    pane.add(tb,0,1, 2, 1);
    GridPane.setMargin(tb, new Insets(0, 6, 0, 4));

    list = new ListView<>();
    list.setEditable(true);
    pane.add(list,0,2, 2, 1);
    GridPane.setMargin(list, new Insets(0, 6, 0, 4));

    list.setCellFactory(param -> new FHListCell());
    list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      FileHandler selectedItem = (FileHandler)newValue;
      FileHandler prevItem = (FileHandler)oldValue;
      onSaveForm(prevItem);
      onEditForm(selectedItem);
    });

    ObservableList<FileHandler> items;
    if(bViewers){
      items = FXCollections.observableArrayList(cfg.getViewers());
    }else{
      items = FXCollections.observableArrayList(cfg.getEditors());
    }
    list.setItems(items);

    buildForm(cfg, pane);
    // TODO: setAccelerators(pane.getScene());

    thisPane = pane;

    updateControls();

    return pane;
  }

  protected void buildForm(Config CFG, GridPane pane){
    labMode = new Label(CFG.getTextResource().getString("Viewer")+": ");
    if(bViewers) {
      pane.add(labMode, 0, 3, 1, 1);
      GridPane.setMargin(labMode, new Insets(10, 0, 10, 4));
    }

    Option[] oo = new Option[2];
    oo[0] = new Option(FileHandler.INTERNAL, CFG.getTextResource().getString("Internal viewer"));
    oo[1] = new Option(FileHandler.EXTERNAL, CFG.getTextResource().getString("External viewer"));
    ObservableList<Option> v = FXCollections.observableArrayList(oo);
    // v.stream().map(op -> mOpts.put(op.getKey(), op));
    v.stream().forEach(op -> mOpts.put(op.getKey(), op));
    cbxMode = new ComboBox<>(v);
    if(bViewers) {
      pane.add(cbxMode, 1, 3, 1, 1);
      GridPane.setMargin(cbxMode, new Insets(10, 8, 10, 0));
    }

    GridPane pane1 = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane1.setPadding(new Insets(10));
    pane1.setHgap(5);
    pane1.setVgap(5);
    ru.yc.app.gui.BorderedTitledPane grpPane = new ru.yc.app.gui.BorderedTitledPane(CFG.getTextResource().getString("External viewer"), pane1);
    pane.add(grpPane,0,4, 2, 1);
    if(!bViewers) {
      GridPane.setMargin(grpPane, new Insets(10, 0, 0, 0));
    }

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

  private void setAccelerators(Scene scene) {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      final KeyCombination keyCombAltUp = new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN);
      final KeyCombination keyCombAltDn = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.ALT_DOWN);
      final KeyCombination keyCombIns = new KeyCodeCombination(KeyCode.INSERT);
      final KeyCombination keyCombDel = new KeyCodeCombination(KeyCode.DELETE);
      final KeyCombination keyCombF4 = new KeyCodeCombination(KeyCode.F4);
      public void handle(KeyEvent ke) {
        if (keyCombAltUp.match(ke)) {
          onUp();
          ke.consume(); // <-- stops passing the event to next node
          return;
        }
        if (keyCombAltDn.match(ke)) {
          onDn();
          ke.consume(); // <-- stops passing the event to next node
          return;
        }
        if (keyCombIns.match(ke)) {
          onAdd();
          ke.consume(); // <-- stops passing the event to next node
          return;
        }
        if (keyCombDel.match(ke)) {
          onDel();
          ke.consume(); // <-- stops passing the event to next node
          return;
        }
        if (keyCombF4.match(ke)) {
          onEdit();
          ke.consume(); // <-- stops passing the event to next node
          return;
        }
      }
    });
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
      ix = -1;
    }
    FileHandler fh = new FileHandler();
    fh.setMode(FileHandler.INTERNAL);
    list.getItems().add(ix+1, fh);
    list.requestFocus();
    updateControls();
  }

  public void onDel(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<0){
      ix = 0;
    }
    list.getItems().remove(ix);
    list.requestFocus();
    updateControls();
  }

  public void onEdit(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<0){
      ix = 0;
    }
    list.edit(ix);
    list.requestFocus();
    updateControls();
  }

  public void onUp(){
    int ix = list.getSelectionModel().getSelectedIndex();
    if(ix<1 || list.getItems().size()<2){
      return;
    }
    ObservableList v = list.getItems();
    Object o1 = v.get(ix);
    v.remove(ix);
    v.add(ix-1, o1);
    list.setItems(v);
    list.getSelectionModel().select(ix-1);
    updateControls();
  }

  public void onDn(){
    int ix = list.getSelectionModel().getSelectedIndex();
    int sz = list.getItems().size();
    if(ix>=(sz-1) || sz<2){
      return;
    }
    ObservableList v = list.getItems();
    Object o1 = v.get(ix);
    v.remove(ix);
    v.add(ix+1, o1);
    list.setItems(v);
    list.getSelectionModel().select(ix+1);
    updateControls();
  }

  public void onEditForm(FileHandler ti){
    edCmd.setText(ti.getCmd());
    edDir.setText(ti.getDir());
    edEnv.setText(ti.getEnv());
    edPar.setText(ti.getParam());
    if(bViewers){
      Option op = mOpts.get(ti.getMode());
      cbxMode.getSelectionModel().select(op);
    }
    updateControls();
  }

  public void onSaveForm(FileHandler ti){
    editFH = ti;
    if(ti==null){
      updateControls();
      return;
    }
    ti.setCmd(Str.trim(edCmd.getText()));
    ti.setDir(Str.trim(edDir.getText()));
    ti.setEnv(Str.trim(edEnv.getText()));
    ti.setParam(Str.trim(edPar.getText()));
    if(bViewers){
      try {
        ti.setMode(cbxMode.getSelectionModel().getSelectedItem().getKey());
      }catch (Exception e){
        System.out.println(e.getMessage()); // TODO:
      }
    }
    updateControls();
  }

  protected void onSaveForm2(){
    onSaveForm(editFH);
  }

  protected void updateControls(){
    int ix = list.getSelectionModel().getSelectedIndex();
    int sz = list.getItems().size();
    if(ix>=0){
      tbDel.setDisable(false);
      tbEdit.setDisable(false);
      if(ix>0) {
        tbUp.setDisable(false);
      }else {
        tbUp.setDisable(true);
      }
      if((ix+1)>=sz) {
        tbDn.setDisable(true);
      }else {
        tbDn.setDisable(false);
      }
    }else{
      tbDel.setDisable(true);
      tbEdit.setDisable(true);
      tbUp.setDisable(true);
      tbDn.setDisable(true);
    }
  }

  public Object onAction(Object o){
    onSaveForm2();
    Config cfg = (Config)o;
    cfg.getViewers().clear();
    cfg.getViewers().addAll(list.getItems());
    return cfg;
  }

  class Option extends Pair<String, String> {
    public Option(String x, String s) { super(x, s); }

    @Override
    public String toString() {
      return getValue();
    }
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
