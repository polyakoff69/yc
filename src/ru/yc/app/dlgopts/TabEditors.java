package ru.yc.app.dlgopts;

public class TabEditors extends TabViewEdit {
    public TabEditors(){
        bViewers = false;
    }

    public void setPrefW(double w){
        super.setPrefW(w);
        list.setPrefHeight(183);
    }
}
