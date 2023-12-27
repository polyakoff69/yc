package ru.yc.app.dlgopts;

import ru.yc.app.Config;

public class TabEditors extends TabViewEdit {
    public TabEditors(){
        bViewers = false;
    }

    public void setPrefW(double w){
        super.setPrefW(w);
        list.setPrefHeight(183);
    }

    public Object onAction(Object o){
        onSaveForm2();
        Config cfg = (Config)o;
        cfg.getEditors().clear();
        cfg.getEditors().addAll(list.getItems());
        return cfg;
    }

}
