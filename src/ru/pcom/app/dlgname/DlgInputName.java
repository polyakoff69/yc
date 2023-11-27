package ru.pcom.app.dlgname;

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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pcom.app.Config;
import ru.pcom.app.ICallback;
import ru.pcom.app.dlgopts.TabAppearance;
import ru.pcom.app.dlgopts.TabGeneral;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.CfgUtil;
import ru.pcom.app.util.UiUtil;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class DlgInputName extends Stage {
    ResourceBundle rs = null;
    Label prompt = new Label("");
    TextField edName = new TextField();
    private boolean result = false;

    public DlgInputName(Stage owner, String title) {
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

        Button btnOk = new Button("OK");
        btnOk.setOnAction(evt-> onSave());
        btnOk.setMinWidth(UiUtil.getWidgetWidth(75));
        btnOk.setDefaultButton(true);

        Button btnX = new Button(rs.getString("cancel"));
        btnX.setOnAction(evt-> close());
        btnX.setMinWidth(UiUtil.getWidgetWidth(75));

        Button btnHlp = new Button(rs.getString("help2"));
        btnHlp.setDisable(true);
        // btnHlp.setOnAction();
        btnHlp.setMinWidth(UiUtil.getWidgetWidth(75));

        HBox paneButtons = new HBox(10, btnOk, btnX, btnHlp);
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

        prompt.setPrefWidth(400.0);
        prompt.setLabelFor(edName);

        gridpane1.add(prompt, 0, 0);
        gridpane1.add(edName, 0, 1);
        gridpane1.add(paneButtons, 0, 2);
        // GridPane.setMargin(gridpane, new Insets(0, 0, 0, 10));

        GridPane.setHalignment(btnOk, HPos.CENTER.RIGHT);
        GridPane.setHalignment(btnX, HPos.CENTER.RIGHT);

        root.getChildren().add(gridpane1);

        String css = CfgUtil.loadCSS(this.getClass(),"/app.css");
        if(css!=null)
            getScene().getStylesheets().add(css);
        css = CfgUtil.loadCSS(this.getClass(),"/dlg.css");
        if(css!=null)
            getScene().getStylesheets().add(css);

        Stage stage = (Stage) getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/app.png")));

        paneButtons.getStyleClass().add("dlg-wnd");
        gridpane1.getStyleClass().add("dlg-wnd");
        root.setStyle("-fx-background-color: -fx-base;");
    }

    public void setContentText(String s){
        prompt.setText(s);
    }
    private void onSave(){
        result = true;
        close();
    }

    public boolean isResult() {
        return result;
    }

    public String getValue(){
        if(isResult())
            return edName.getText();
        return "";
    }

    public void setValue(String s){
        edName.setText(s);
    }

}

