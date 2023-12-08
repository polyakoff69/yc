package ru.yc.app.dlgopts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.yc.app.Config;
import ru.yc.app.ICallback;
import ru.yc.app.gui.MsgBox;
import ru.yc.app.util.CfgUtil;
import ru.yc.app.util.Pair;
import ru.yc.app.util.UiUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class DlgOptions3 extends Stage {

  ResourceBundle rs = null;
  Pane cardsPane = new StackPane();
  java.util.List<ICallback> tabs = new ArrayList<>();
  java.util.Map<String, Region> cards = new LinkedHashMap<>();
  private boolean result = false;

  public DlgOptions3(Stage owner) {
    super();
    rs = Config.get().getTextResource();

    initOwner(owner);
    setTitle(rs.getString("options").replace("_",""));
    initModality(Modality.APPLICATION_MODAL);
    setResizable(false);

    try{
      initControls();
    }catch (Exception e){
      MsgBox.showErrorOk(getClass(), e.getMessage(), null, null);
    }
  }

  protected void initControls(){
    Group root = new Group();
    Scene scene = new Scene(root /*, 250, 150 */ /*, Color.WHITE */);

    scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.SHIFT_ANY);
      public void handle(KeyEvent ke) {
        if (keyComb.match(ke)) { close(); }
      }
    });

    setScene(scene);

    GridPane gridpane1 = new GridPane();
    gridpane1.setPadding(new Insets(0));
    gridpane1.setHgap(5);
    gridpane1.setVgap(10);

    TreeItem<Pair> tiRoot = new TreeItem<Pair>(new Pair("options", rs.getString("options").replace("_","")));
    TreeItem<Pair> tiGeneral = new TreeItem<Pair>(new Pair("general", rs.getString("General")));
    TreeItem<Pair> tiAppearance = new TreeItem<Pair>(new Pair("appearance", rs.getString("Appearance")));
    TreeItem<Pair> tiLanguage = new TreeItem<Pair>(new Pair("language", rs.getString("Language").replace("_","")));
    tiRoot.getChildren().add(tiGeneral);
    tiRoot.getChildren().add(tiAppearance);
    tiRoot.getChildren().add(tiLanguage);

    TreeView<Pair> view3 = new TreeView<Pair>(tiRoot);
    view3.setPrefSize(180, 150);
    view3.setShowRoot(false);
    view3.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {
      @Override
      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        TreeItem<Pair> selectedItem = (TreeItem<Pair>) newValue;
        onCard(selectedItem.getValue());
      }
    });

    gridpane1.add(view3, 0, 0);
    GridPane.setMargin(view3, new Insets(10+6, 0, 0, 10+6));

    int w = Config.get().getFontSz();
    if(w<8 || w>50){
      w = 50;
    }
    cardsPane.setPrefWidth(w*50);

    TabGeneral ts = new TabGeneral();
    tabs.add(ts);
    cards.put("general", ts.buildTab(rs, this));

    TabAppearance ta = new TabAppearance();
    tabs.add(ta);
    cards.put("appearance", ta.buildTab(rs, this));

    TabLang tl = new TabLang();
    tabs.add(tl);
    cards.put("language", tl.buildTab(rs, this));

    onCard("general");

    gridpane1.add(cardsPane, 1, 0);

    Button btnOk = new Button("OK");
    btnOk.setOnAction(evt-> onSave());
    btnOk.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnX = new Button(rs.getString("cancel"));
    btnX.setOnAction(evt-> close());
    btnX.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnHlp = new Button(rs.getString("help2"));
    btnHlp.setDisable(true);
    // btnHlp.setOnAction();
    btnHlp.setMinWidth(UiUtil.getWidgetWidth(75));

    HBox hbx = new HBox(btnOk, btnX, btnHlp);
    hbx.setSpacing(10);
    hbx.setAlignment(Pos.BOTTOM_RIGHT);
    hbx.setPadding(new Insets(10,10+4,10+4,10));

    GridPane.setHalignment(btnOk, HPos.RIGHT);
    GridPane.setHalignment(btnX, HPos.RIGHT);
    GridPane.setHalignment(btnHlp, HPos.RIGHT);
    gridpane1.add(hbx, 0, 1, 2, 1);

    root.getChildren().add(gridpane1);

    String css = CfgUtil.loadCSS(this.getClass(),"/app.css");
    if(css!=null)
      getScene().getStylesheets().add(css);
    css = CfgUtil.loadCSS(this.getClass(),"/dlg.css");
    if(css!=null)
      getScene().getStylesheets().add(css);

    Stage stage = (Stage) getScene().getWindow();
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/app.png")));

    hbx.getStyleClass().add("dlg-wnd");
    gridpane1.getStyleClass().add("dlg-wnd");
    root.setStyle("-fx-background-color: -fx-base;");
  }

  private void onCard(Pair item){
    onCard(item.id);
  }

  private void onCard(String id){
    Region r = cards.get(id);
    cardsPane.getChildren().clear();
    cardsPane.getChildren().add(r);
  }

  private void onSave(){
    for(ICallback cb : tabs){
      cb.onAction(Config.get());
    }
    result = true;
    close();
  }

  public boolean isResult() {
    return result;
  }

}
