package ru.pcom.app;

public class Сonfig {

    private static Сonfig c;

    private Сonfig(){
    }

    public static Сonfig get(){
        if(c==null){
            c = new Сonfig();
        }
        return c;
    }

    private int frmW = 600, frmH = 300;

    public int getFrmW() {
        return frmW;
    }

    public int getFrmH() {
        return frmH;
    }

}
