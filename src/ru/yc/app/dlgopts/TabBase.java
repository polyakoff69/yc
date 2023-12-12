package ru.yc.app.dlgopts;

import javafx.scene.control.Label;
import ru.yc.app.Config;

public class TabBase {
    protected double pw = -1;
    protected Label getTitleLabel(String title){
        Label l = new Label(title);
        int fsz = Config.get().getFontSz();
        if(fsz<6)fsz = 6;
        l.setStyle("-fx-font-size: "+(fsz+2)+"; -fx-font-weight: bold");
        return l;
    }

    public void setPrefW(double w){
        pw = w;
    }
}
