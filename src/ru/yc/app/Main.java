package ru.yc.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import ru.yc.app.gui.MsgBox;
import ru.yc.app.mainwnd.MainFrameScene;
import ru.yc.app.mainwnd.MainFrm;
import ru.yc.app.mainwnd.ShadowWnd;
import ru.yc.app.sys.Environ;
import ru.yc.app.sys.Os;
import ru.yc.app.util.CfgUtil;
import ru.yc.app.util.Util;

import java.io.File;
import java.util.Optional;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Config CFG = Config.get();
        CFG.setApplication(this);

        Util.initB();

        MainFrm frm = new MainFrm();
        Scene scene = frm.build();

        String css = CfgUtil.loadCSS(this.getClass(),"/app.css");
        if(css!=null)
            scene.getStylesheets().add(css);

        frm.setPrimStage(primaryStage);

        if(false) { // TODO: flat-wnd
            if (Os.isWindows()) {
                VBox vbox = new VBox(scene.getRoot());
                scene = (new ShadowWnd()).getShadowScene(vbox);
                primaryStage.initStyle(StageStyle.TRANSPARENT);
            } else {
                primaryStage.initStyle(StageStyle.UNDECORATED);
            }
        }

        primaryStage.setTitle(CFG.getTextResource().getString("app"));
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/app.png")));

        EvtHandler h = new EvtHandler(primaryStage, this);
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, h::closeWindowEvent);
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, frm::onPostCreate);

        if(CFG.getFrmX()>=0 && CFG.getFrmY()>=0) {
            primaryStage.setX(CFG.getFrmX());
            primaryStage.setY(CFG.getFrmY());
        }

        primaryStage.show();

        String e = CFG.getLoadErr();
        if(e!=null && e.trim().isEmpty()==false)
            MsgBox.showErrorOk(getClass(), CFG.getTextResource().getString("err_cfg_read")+": " + e, null, null);

    }

    public static void main(String[] args) {

        try {
            File f = new File(Environ.getHomeFolder());
            f = new File(f,".yc");
            f = new File(f,"yc.xml");
            Config CFG = Config.get();
            CFG.load(f.getAbsolutePath());
            CFG.setTextResourceL(CFG.getLang());
        }catch (Exception e){
            Config.get().setLoadErr(e.getMessage());
        }

        launch(args);
    }

    public void saveCfg(){
        CfgUtil.saveCfg(getClass());
    }

    class EvtHandler {

        private Main main;
        private Stage stage;
        private long tm = 0;

        public EvtHandler(Stage primStage, Main main){
          this.stage = primStage;
          this.main = main;
        }

        public void closeWindowEvent(WindowEvent event) {
            long tm = System.currentTimeMillis();
            if(tm-this.tm<2000L) {
                event.consume();
                return;
            }
            this.tm = tm;

            if(true) {
                Config CFG = Config.get();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                MsgBox.setYN(alert, CFG);
                alert.setTitle(CFG.getTextResource().getString("app"));
                alert.setHeaderText(null);
                alert.setContentText(String.format(Config.get().getTextResource().getString("ask_quit")));
                alert.initOwner(stage.getOwner());

                DialogPane dialogPane = alert.getDialogPane();
                String css = CfgUtil.loadCSS(getClass(),"/app.css");
                if(css!=null)
                    dialogPane.getStylesheets().add(css);
                // dialogPane.getStyleClass().add("myDialog");
                Stage stage = (Stage) dialogPane.getScene().getWindow();
                stage.getIcons().clear();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/app.png")));

                Optional<ButtonType> res = alert.showAndWait();

                if(res.isPresent()) {
                    ButtonType btt = res.get();
                    if(btt.equals(ButtonType.NO) ||
                       (btt.getButtonData()!=null && "N".equals(btt.getButtonData().getTypeCode()))) {
                        event.consume();
                    } else {

                        try { // save window pos
                            Stage s= this.stage;
                            CFG.setFrmW((int)s.getScene().getWidth());
                            CFG.setFrmH((int)s.getScene().getHeight());
                            CFG.setFrmX((int)s.getX());
                            CFG.setFrmY((int)s.getY());

                            if(s.getScene() instanceof MainFrameScene) {
                                MainFrameScene mfs = (MainFrameScene)s.getScene();
                                mfs.getFrm().getConfig(CFG);
                            }
                        }catch (Exception e){ }

                        main.saveCfg();
                    }
                }
            }
        }
    }

}
