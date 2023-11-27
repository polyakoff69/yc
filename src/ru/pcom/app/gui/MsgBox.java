package ru.pcom.app.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.pcom.app.Config;
import ru.pcom.app.util.CfgUtil;

public class MsgBox {

    public static void showWarnOk(Class c, String text, String title, String header){
        showErrorOk(c, text, title, header, Alert.AlertType.WARNING);
    }

    public static void showErrorOk(Class c, String text, String title, String header){
        showErrorOk(c, text, title, header, Alert.AlertType.ERROR);
    }

    public static void showErrorOk(Class c, String text, String title, String header, Alert.AlertType atype){
        if(title==null)
            title = Config.get().getTextResource().getString("error");

        Alert alert = new Alert(atype);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        DialogPane dialogPane = alert.getDialogPane();
        String css = CfgUtil.loadCSS(c,"/app.css");
        if(css!=null)
            dialogPane.getStylesheets().add(css);
        dialogPane.requestFocus();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(new Image(c.getResourceAsStream("/app.png")));

        alert.showAndWait();
    }

    public static void setYN(Alert alert, Config cfg){
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().remove(ButtonType.CANCEL);
        if("EN".equalsIgnoreCase(cfg.getTextResource().getString("lang"))){
            alert.getButtonTypes().add(ButtonType.NO);
            alert.getButtonTypes().add(ButtonType.YES);
        }else{
            ButtonType yes = new ButtonType(cfg.getTextResource().getString("yes"), ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType(cfg.getTextResource().getString("no"), ButtonBar.ButtonData.NO);
            alert.getButtonTypes().add(no);
            alert.getButtonTypes().add(yes);
        }
    }

    public static boolean equalCode(ButtonType bt1, ButtonType bt2){
        if(bt1==null || bt2==null){
            return false;
        }
        if(bt1.getButtonData()==null || bt2.getButtonData()==null){
            return false;
        }
        if(bt1.getButtonData().getTypeCode()==null || bt2.getButtonData().getTypeCode()==null){
            return false;
        }
        return bt1.getButtonData().getTypeCode().equals(bt2.getButtonData().getTypeCode());
    }

}
