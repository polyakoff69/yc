package ru.pcom.app.dlgopts;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pcom.app.Config;
import ru.pcom.app.ICallback;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.CfgUtil;
import ru.pcom.app.util.UiUtil;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class DlgOptions extends Stage {

    ResourceBundle rs = null;
    java.util.List<ICallback> tabs = new ArrayList<>();
    private boolean result = false;

    public DlgOptions(Stage owner) {
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

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(10-4, 10+4, 10+4, 10+4));
        gridpane.setHgap(10);
        gridpane.setVgap(5);
        gridpane.getColumnConstraints().add(new ColumnConstraints(300));

        GridPane gridpane1 = new GridPane();
        gridpane1.setPadding(new Insets(0));
        gridpane1.setHgap(5);
        gridpane1.setVgap(10);

        TabPane tab = new TabPane();
        tab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // tab.setStyle("-fx-background-color: -fx-base; -fx-border-width: -fx-base; -fx-border-color: -fx-base; -fx-box-border: -fx-base;");
        gridpane1.add(tab, 0, 0);

        TabGeneral ts = new TabGeneral();
        Tab tab1 = new Tab(rs.getString("General"), ts.buildTab(rs, this));
        tab1.setTooltip(new Tooltip(rs.getString("General")));
        tabs.add(ts);
        tab.getTabs().add(tab1);

        TabAppearance ta = new TabAppearance();
        tab1 = new Tab(rs.getString("Appearance"), ta.buildTab(rs, this));
        tab1.setTooltip(new Tooltip(rs.getString("Appearance")));
        tabs.add(ta);
        tab.getTabs().add(tab1);

        TabLang tl = new TabLang();
        tab1 = new Tab(rs.getString("Language").replace("_",""), tl.buildTab(rs, this));
        tab1.setTooltip(new Tooltip(rs.getString("Language").replace("_","")));
        tabs.add(tl);
        tab.getTabs().add(tab1);

        gridpane1.add(gridpane, 0, 1);
        // GridPane.setMargin(gridpane, new Insets(0, 0, 0, 10));

        Button btnOk = new Button("OK");
        btnOk.setOnAction(evt-> onSave());
        btnOk.setMinWidth(UiUtil.getWidgetWidth(75));
        gridpane.add(btnOk, 1-1, 0);

        Button btnX = new Button(rs.getString("cancel"));
        btnX.setOnAction(evt-> close());
        btnX.setMinWidth(UiUtil.getWidgetWidth(75));
        gridpane.add(btnX, 2-1, 0);

        Button btnHlp = new Button(rs.getString("help2"));
        btnHlp.setDisable(true);
        // btnHlp.setOnAction();
        btnHlp.setMinWidth(UiUtil.getWidgetWidth(75));
        gridpane.add(btnHlp, 3-1, 0);

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

        gridpane.getStyleClass().add("dlg-wnd");
        gridpane1.getStyleClass().add("dlg-wnd");
        root.setStyle("-fx-background-color: -fx-base;");
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
