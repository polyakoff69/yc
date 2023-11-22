package ru.pcom.app.dlgerror;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pcom.app.Config;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.*;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DlgOpError extends Stage {
  ResourceBundle rs = null;
  Label labFile = new Label(""), labFileName = new Label(""), labErrMsg = new Label(""), labErr = new Label("");
  private int result = -1;

  public DlgOpError(Stage owner, String title) {
    super();
    rs = Config.get().getTextResource();

    initOwner(owner);
    setTitle(title);
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

    Button btnY = new Button(rs.getString("Retry"));
    btnY.setOnAction(evt-> onSave());
    btnY.setMinWidth(UiUtil.getWidgetWidth(75));
    btnY.setDefaultButton(true);

    Button btnX = new Button(rs.getString("cancel"));
    btnX.setOnAction(evt-> close());
    btnX.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnN = new Button(rs.getString("no"));
    btnN.setOnAction(evt-> onNo());
    btnN.setMinWidth(UiUtil.getWidgetWidth(75));

    HBox paneButtons = new HBox(10, btnY, btnN, btnX);
    paneButtons.setPadding(new Insets(10, 0, 0, 0));
    paneButtons.setAlignment(Pos.BASELINE_CENTER);
    // gridpaneButtons.setHgap(5);
    // gridpaneButtons.setVgap(0);
    // gridpaneButtons.getColumnConstraints().add(new ColumnConstraints(250));

    GridPane gridpane1 = new GridPane();
    gridpane1.setPadding(new Insets(10+4));
    gridpane1.setHgap(0);
    gridpane1.setVgap(4);
    // gridpane1.getColumnConstraints().add(new ColumnConstraints(250));

    labFile.setText(Str.Capit(rs.getString("file")+":  "));
    // prompt.setPrefWidth(400.0);
    labErr.setText(rs.getString("error")+":  ");

    gridpane1.add(labFile, 0, 0); gridpane1.add(labFileName, 1, 0);
    gridpane1.add(labErr, 0, 1); gridpane1.add(labErrMsg, 1, 1);
    gridpane1.add(paneButtons, 0, 2, 2, 1);

    labFileName.setMinWidth(300.0);
    GridPane.setMargin(labErr, new Insets(10, 0, 0, 0));
    GridPane.setMargin(labErrMsg, new Insets(10, 0, 0, 0));

    GridPane.setHalignment(btnY, HPos.CENTER.RIGHT);
    GridPane.setHalignment(btnX, HPos.CENTER.RIGHT);
    GridPane.setHalignment(labErr, HPos.CENTER.RIGHT);

    root.getChildren().add(gridpane1);

    String css = CfgUtil.loadCSS(this.getClass(),"/app.css");
    if(css!=null)
      getScene().getStylesheets().add(css);
    css = CfgUtil.loadCSS(this.getClass(),"/dlg.css");
    if(css!=null)
      getScene().getStylesheets().add(css);

    paneButtons.getStyleClass().add("dlg-wnd");
    gridpane1.getStyleClass().add("dlg-wnd");
    root.setStyle("-fx-background-color: -fx-base;");
  }

  private void onSave(){
    result = 1;
    close();
  }

  private void onNo(){
    result = 0;
    close();
  }

  public int getResult() {
    return result;
  }

  public void setFile(File fs, String err){
    labFileName.setText(fs.getAbsolutePath());
    labErrMsg.setText(err);
  }

}

