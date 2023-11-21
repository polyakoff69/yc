package ru.pcom.app.dlgoper;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.pcom.app.Config;
import ru.pcom.app.Main;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.CfgUtil;
import ru.pcom.app.util.UiUtil;

import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class DlgOperation extends Stage {
    ResourceBundle rs = null;
    Label prompt1 = new Label(""), f1 = new Label(""), f2 = new Label(""), opinfo = new Label("0"),
          prompt2 = new Label(""), done = new Label("");
    ProgressBar progress = new ProgressBar();
    Label procent = new Label("0%");
    private String promptId;
    protected boolean cancelled = false, file2 = true;

    public DlgOperation(Stage owner, String title, String promptId) {
        super();
        rs = Config.get().getTextResource();
        this.promptId = promptId;

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

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, new EventHandler<javafx.scene.input.KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ESCAPE);
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (keyComb.match(ke)) { onCancel(); }
            }
        });

        setScene(scene);

        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        Button btnX = new Button(rs.getString("cancel"));
        btnX.setOnAction(evt-> onCancel());
        btnX.setMinWidth(UiUtil.getWidgetWidth(75));

        HBox paneButtons = new HBox(10, btnX);
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

        String s = rs.getString(promptId);
        int pos = s.indexOf("/");
        if(pos>0){
            s = s.substring(0,pos);
        }
        prompt1.setText(s+":  ");
        setTitle(s);
        // prompt.setPrefWidth(400.0);

        prompt2.setText(rs.getString("to")+":  ");
        if("fdelete".equalsIgnoreCase(promptId)){
            prompt2.setText("");
        }

        done.setText(rs.getString("Done")+":  ");

        gridpane1.add(prompt1, 0, 0); gridpane1.add(f1, 1, 0);
        if(file2) {
            gridpane1.add(prompt2, 0, 1); gridpane1.add(f2, 1, 1);
        }
        gridpane1.add(done, 0, 2); gridpane1.add(progress, 1, 2);
        gridpane1.add(opinfo, 1, 3);
        gridpane1.add(paneButtons, 0, 4, 2, 1);

        f1.setMinWidth(500.0);
        f1.setMaxWidth(500.0);
        f2.setMinWidth(500.0);
        f2.setMaxWidth(500.0);
        GridPane.setMargin(prompt2, new Insets(4, 0, 0, 0));
        GridPane.setMargin(f2, new Insets(4, 14, 0, 0));
        GridPane.setMargin(f1, new Insets(0, 14, 0, 0));
        GridPane.setMargin(done, new Insets(10, 0, 0, 0));
        GridPane.setMargin(progress, new Insets(10, 10, 0, 0));

        progress.setMaxWidth(3000);

        GridPane.setHalignment(btnX, HPos.CENTER.RIGHT);
        GridPane.setHalignment(prompt2, HPos.CENTER.RIGHT);
        GridPane.setHalignment(done, HPos.CENTER.RIGHT);
        GridPane.setFillWidth(progress, true);

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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void closeWindowEvent(WindowEvent event) {
        event.consume();
        onCancel();
    }

    private void onCancel(){
        Config CFG = Config.get();

        Alert alert = new Alert(Alert.AlertType.WARNING);
        MsgBox.setYN(alert, CFG);
        alert.setTitle(CFG.getTextResource().getString("Question"));
        alert.setHeaderText(null);
        alert.setContentText(String.format(Config.get().getTextResource().getString("Cancel_oper")));
        alert.initOwner(getOwner());

        DialogPane dialogPane = alert.getDialogPane();
        String css = CfgUtil.loadCSS(getClass(),"/app.css");
        if(css!=null)
            dialogPane.getStylesheets().add(css);
        // dialogPane.getStyleClass().add("myDialog");

        Optional<ButtonType> res = alert.showAndWait();
        if(res.isPresent()){
            ButtonType btt = res.get();
            if(MsgBox.equalCode(btt, ButtonType.YES)==false){
                return;
            }
        }

        setCancelled(true);
        close();
    }

    public void setStatus(File fs, File ft, int prc, int total, int cnt){
        f1.setText(fs.getAbsolutePath());
        if(ft!=null) {
            f2.setText(ft.getAbsolutePath());
        }else {
            f2.setText("");
        }
        progress.setProgress((double) prc / 100.0);
        NumberFormat nf = NumberFormat.getNumberInstance();
        opinfo.setText(""+prc+"%, "+nf.format(cnt)+"/"+nf.format(total));
    }

}
