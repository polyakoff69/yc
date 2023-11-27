package ru.pcom.app.dlgfreplace;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pcom.app.Config;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.CfgUtil;
import ru.pcom.app.util.FileUtil;
import ru.pcom.app.util.UiUtil;
import ru.pcom.app.util.Util;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DlgFileReplace extends Stage {
    ResourceBundle rs = null;
    Label prompt1 = new Label(""), f1 = new Label(""), f2 = new Label(""), f1info = new Label(""), f2info = new Label(""), prompt2 = new Label("");
    private int result = -1;

    public DlgFileReplace(Stage owner, String title) {
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

        Button btnY = new Button(rs.getString("yes"));
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

        prompt1.setText(rs.getString("replace_file_1")+":  ");
        // prompt.setPrefWidth(400.0);

        prompt2.setText(rs.getString("replace_file_2")+":  ");

        gridpane1.add(prompt1, 0, 0); gridpane1.add(f1, 1, 0);
                                             gridpane1.add(f1info, 1, 1);
        gridpane1.add(prompt2, 0, 2); gridpane1.add(f2, 1, 2);
                                             gridpane1.add(f2info, 1, 3);
        gridpane1.add(paneButtons, 0, 4, 2, 1);

        f1.setMinWidth(300.0);
        GridPane.setMargin(prompt2, new Insets(10, 0, 0, 0));
        GridPane.setMargin(f2, new Insets(10, 0, 0, 0));

        GridPane.setHalignment(btnY, HPos.CENTER.RIGHT);
        GridPane.setHalignment(btnX, HPos.CENTER.RIGHT);
        GridPane.setHalignment(prompt2, HPos.CENTER.RIGHT);

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

    public void setFiles(File fs, File ft){
        f1.setText(fs.getAbsolutePath());
        f2.setText(ft.getAbsolutePath());
        f1info.setText(getInfo(fs));
        f2info.setText(getInfo(ft));
    }

    public String getInfo(File f){
        String i = "";
        i = ""+Util.getAutoSize(f.length(), Config.get().getUnit());

        FileTime fdate = null;
        try {
            fdate = FileUtil.getFileTime(f);
        }catch (Exception e){}

        DateTimeFormatter fmt = Util.getDateFmt();
        if(fdate!=null)
            i = i + ", " + fmt.format(fdate.toInstant());

        fmt = Util.getTimeFmt();
        if(fdate!=null)
            i = i + ", " + fmt.format(fdate.toInstant());

        String fa = "";
        boolean tryPosix = true;
        try {
            fa = FileUtil.getFileAttr(f);
            tryPosix = false;
        } catch (UnsupportedOperationException x) {
            if(f.isHidden())
                fa = "H";
        }catch (Exception e){}

        if (tryPosix) {
            try {
                fa = FileUtil.getFilePxAttr(f);
            } catch (UnsupportedOperationException x) {
                if (f.isHidden())
                    fa = "H";
            } catch (Exception e) {
            }
        }

        if(fa.trim().isEmpty())
            fa = "-";

        i = i + ", " +fa;

        return i;
    }

}

