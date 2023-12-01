package ru.yc.app.mainwnd;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class MainFrameScene extends Scene {

    private MainFrm frm;

    public MainFrameScene(Parent p, MainFrm f, double w, double h){
        super(p,w,h);
        frm = f;
    }

    public MainFrm getFrm() {
        return frm;
    }

    public void setFrm(MainFrm frm) {
        this.frm = frm;
    }

}
