package ru.yc.app.gui;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import ru.yc.app.Config;
import ru.yc.app.Constants;
import ru.yc.app.file.FileData;
import ru.yc.app.util.FileUtil;
import ru.yc.app.util.Str;
import ru.yc.app.util.Util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

public class StatusLine extends HBox {

    private Hyperlink hlName = new Hyperlink();
    private Hyperlink hlSize = new Hyperlink();
    private Hyperlink hlDate = new Hyperlink();
    private Hyperlink hlTime = new Hyperlink();
    private Hyperlink hlAttr = new Hyperlink();

    private Label c1 = new Label(",");
    private Label c2 = new Label(",");
    private Label c3 = new Label(",");
    private Label c4 = new Label(",");

    private Label xinfo = new Label("");

    Tooltip tt = new Tooltip(Config.get().getTextResource().getString("clip_copy"));

    private String folder = "Folder";

    public StatusLine(){
    }

    public void init(String folder){
        hlName.setOnAction(evt -> clipCopy(hlName.getText()) );
        hlName.setFocusTraversable(false);

        hlSize.setOnAction(evt -> clipCopy(hlSize.getText()) );
        hlSize.setFocusTraversable(false);

        hlDate.setOnAction(evt -> clipCopy(hlDate.getText()) );
        hlDate.setFocusTraversable(false);

        hlTime.setOnAction(evt -> clipCopy(hlTime.getText()) );
        hlTime.setFocusTraversable(false);

        hlAttr.setOnAction(evt -> clipCopy(hlAttr.getText()) );
        hlAttr.setFocusTraversable(false);

        c1.setStyle("-fx-border-width: 2 0 0 0");
        c2.setStyle("-fx-border-width: 2 0 0 0");
        c3.setStyle("-fx-border-width: 2 0 0 0");
        c4.setStyle("-fx-border-width: 2 0 0 0");

        getChildren().addAll(xinfo);
        getChildren().addAll(hlName, c1);
        getChildren().addAll(hlSize, c2);
        getChildren().addAll(hlDate, c3);
        getChildren().addAll(hlTime, c4);
        getChildren().addAll(hlAttr);

        setCommasVisible(false);
        xinfo.setVisible(false);

        hlName.setTooltip(tt);
        hlSize.setTooltip(tt);
        hlDate.setTooltip(tt);
        hlTime.setTooltip(tt);
        hlAttr.setTooltip(tt);

        this.folder = folder.toUpperCase();
    }

    public void setFileData(FileData fd){
        if(fd==null){
            setCommasVisible(false);
            hlName.setText("");
            hlSize.setText("");
            hlDate.setText("");
            hlTime.setText("");
            hlAttr.setText("");
            hlName.setTooltip(null);
            hlSize.setTooltip(null);
            hlDate.setTooltip(null);
            hlTime.setTooltip(null);
            hlAttr.setTooltip(null);
            return;
        }

        hlName.setTooltip(tt);
        hlSize.setTooltip(tt);
        hlDate.setTooltip(tt);
        hlTime.setTooltip(tt);
        hlAttr.setTooltip(tt);

        setMode1();
        setCommasVisible(true);
        xinfo.setText("");

        String s = "";
        hlName.setText(fd.getName());

        Long v = fd.getSize();
        FileData.Info fdi = fd.getInfo();
        if(v!=null && Constants.FOLDER ==v.longValue() || (fdi!=null && (fdi.isFolder() || fdi.isParent()))) {
            hlSize.setText(folder);
        } else {
            try{
                NumberFormat nf = NumberFormat.getInstance();
                int unit = Config.get().getUnit();
                long lv = v.longValue();
                if(unit==1) {
                    hlSize.setText("" + nf.format(v.longValue()));
                }else{
                    hlSize.setText("" + Util.getAutoSize(lv, unit));
                }
            }catch (Exception e) {
                hlSize.setText("" + v);
            }
        }

        java.nio.file.attribute.FileTime fdate = fd.getDate();
        if(fdate==null && fdi.isParent()){
            try {
                fdate = FileUtil.getFileTime(fd.getFile());
            }catch (Exception e){
                // TODO:
            }
        }

        DateTimeFormatter fmt = Util.getDateFmt();
        if(fdate!=null)
          hlDate.setText(fmt.format(fdate.toInstant()));
        else
          hlDate.setText("");

        fmt = Util.getTimeFmt();
        if(fdate!=null)
            hlTime.setText(fmt.format(fdate.toInstant()));
        else
            hlTime.setText("");

        s = fd.getAttr();
        if(s==null)
            s = "";
        if(Str.isEmpty(s))
            s = "-";
        hlAttr.setText(s);
    }

    public void setSelInfo(String i, long size){
        setModeX();
        setCommasVisible(false);
        xinfo.setText(" "+i);
        if(size>0L){
            NumberFormat nf = NumberFormat.getInstance();
            String tt = "" + nf.format(size) + " " + Config.get().getTextResource().getString("val_byte");
            xinfo.setTooltip(new Tooltip(tt));
        }else{
            xinfo.setTooltip(null);
        }
    }

    public static void clipCopy(String s){
        ClipboardContent cc = new ClipboardContent();
        cc.putString(s);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    public void setCommasVisible(boolean b){
        c1.setVisible(b);
        c2.setVisible(b);
        c3.setVisible(b);
        c4.setVisible(b);
    }

    public void setMode1(){
        hlName.setVisible(true);
        hlSize.setVisible(true);
        hlDate.setVisible(true);
        hlTime.setVisible(true);
        hlAttr.setVisible(true);
        xinfo.setVisible(!true);
    }

    public void setModeX(){
        hlName.setVisible(!true);
        hlSize.setVisible(!true);
        hlDate.setVisible(!true);
        hlTime.setVisible(!true);
        hlAttr.setVisible(!true);
        xinfo.setVisible(true);
    }

}
