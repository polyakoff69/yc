package ru.yc.app.mainwnd;

import ru.yc.app.Config;

import java.util.ResourceBundle;

public abstract class MainFrmBase {

    protected ResourceBundle rsTxt = null;

    public MainFrmBase(){
        rsTxt = Config.get().getTextResource();
    }

    protected abstract void setActiveTab(boolean left);
}
