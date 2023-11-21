package ru.pcom.app.dlgabout;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import ru.pcom.app.Config;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.CfgUtil;
import ru.pcom.app.util.UiUtil;

public class DlgAbout extends Stage {
    ResourceBundle rs = null;
    public DlgAbout(Stage owner) {
        super();
        rs = Config.get().getTextResource();

        initOwner(owner);
        setTitle(rs.getString("about2"));
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root /*, 250, 150 */ /*, Color.WHITE */);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.SHIFT_ANY);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) { close(); }
            }
        });

        setScene(scene);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(buildHeader());

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(10+4));
        gridpane.setHgap(5);
        gridpane.setVgap(5);

        Label lab = new Label(rs.getString("app_ver")+", build "+rs.getString("app_build")); // TODO: вынести в конфиг
        lab.setStyle("-fx-font-weight: bold");
        gridpane.add(lab, 0, 1);

        lab = new Label(rs.getString("copyright"));
        gridpane.add(lab, 0, 2);

        Separator sep = new Separator(Orientation.HORIZONTAL); // lab = new Label(" ");
        gridpane.add(sep, 0, 3);
        GridPane.setMargin(sep, new Insets(5,0,5,0));

        lab = new Label(rs.getString("web")+": ");
        Hyperlink hl = new Hyperlink("http://www.pcom.com");
        // hl.setTooltip(new Tooltip(rs.getString("open"))); TODO:
        hl.setBorder(Border.EMPTY);
        hl.setPadding(new Insets(0, 0, 0, 0));
        hl.setOnAction(event -> { openUrl("http://www.pcom.com"); });
        HBox hbx1 = new HBox(lab, hl);
        gridpane.add(hbx1, 0, 4);

        lab = new Label(rs.getString("email")+": ");
        hl = new Hyperlink("support@pcom.com");
        // hl.setTooltip(new Tooltip(rs.getString("open"))); TODO:
        hl.setBorder(Border.EMPTY);
        hl.setPadding(new Insets(0, 0, 0, 0));
        hl.setOnAction(event -> { openUrl("mailto:support@pcom.com"); });
        hbx1 = new HBox(lab, hl);
        gridpane.add(hbx1, 0, 5);

        sep = new Separator(Orientation.HORIZONTAL); // lab = new Label(" ");
        gridpane.add(sep, 0, 6);
        GridPane.setMargin(sep, new Insets(5,0,5,0));

        lab = new Label(rs.getString("copyright_warn"));
        lab.setMaxWidth(370);
        lab.setWrapText(true);
        gridpane.add(lab, 0, 7);

        Button btnOk = new Button("OK");
        btnOk.setOnAction(evt-> close());
        btnOk.setMinWidth(UiUtil.getWidgetWidth(75));
        HBox hbx = new HBox(btnOk);
        hbx.getStyleClass().add("dlg-wnd");
        hbx.setAlignment(Pos.BOTTOM_RIGHT);
        hbx.setPadding(new Insets(10,10+4,10+4,10));

        borderPane.getStyleClass().add("dlg-wnd");
        lab = new Label(" ");
        lab.setStyle("-fx-background-color: -fx-base;");
        lab.setPrefWidth(65.0);
        borderPane.setLeft(lab);

        borderPane.setCenter(gridpane);
        borderPane.setBottom(hbx);
        root.getChildren().add(borderPane);

        String css = CfgUtil.loadCSS(this.getClass(),"/app.css");
        if(css!=null)
            getScene().getStylesheets().add(css);
        css = CfgUtil.loadCSS(this.getClass(),"/dlg.css");
        if(css!=null)
            getScene().getStylesheets().add(css);

        gridpane.getStyleClass().add("dlg-wnd");
        root.setStyle("-fx-background-color: -fx-base;");

        btnOk.requestFocus();
    }

    private Node buildHeader() {
        GridPane gridPane = new GridPane();
        Image image = new Image(getClass().getResourceAsStream("/app.jpg"));
        ImageView imageView = new ImageView(image);
        gridPane.add(imageView,0,0,1,2);
        gridPane.setHgap(20);
        gridPane.setVgap(0);
        gridPane.setPadding(new Insets(10));

        int fsz = Config.get().getFontSz();
        if(fsz<6)fsz = 6;

        Label lab = new Label(rs.getString("app"));
        lab.setStyle("-fx-font-size: "+(fsz+6)+"; -fx-font-weight: bold");
        lab.setAlignment(Pos.BOTTOM_LEFT);
        gridPane.add(lab,1,0);

        lab = new Label(rs.getString("app_descr")+"            ");
        lab.setStyle("-fx-font-size: "+(fsz-1));
        lab.setAlignment(Pos.TOP_LEFT);
        gridPane.add(lab,1,1);

        gridPane.getStyleClass().add("dlg-wnd");
        gridPane.setStyle("-fx-background-color: #FFFFFF;");

        return gridPane;
    }

    public void openUrl(String url){
        try {
            final HostServices hsvc = Config.get().getApplication().getHostServices();
            hsvc.showDocument(url);
        }catch (Exception e){
            MsgBox.showErrorOk(getClass(), e.getMessage(), null, null);
        }
    }

}

