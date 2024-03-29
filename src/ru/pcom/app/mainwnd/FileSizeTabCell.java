package ru.pcom.app.mainwnd;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import ru.pcom.app.Config;
import ru.pcom.app.Constants;
import ru.pcom.app.file.FileData;
import ru.pcom.app.util.Util;

import java.text.NumberFormat;

public class FileSizeTabCell extends TableCell<FileData, Long> {

    private String folder = "Folder";

    public FileSizeTabCell(String folder) {
        super();
        this.folder = folder.toUpperCase();
    }//constructor

    @Override
    public void updateItem( Long v, boolean empty ) {
        if(!empty && v!=null) {
            if(Constants.FOLDER ==v.longValue())
                setText(folder);
            else {
                try{
                    NumberFormat nf = NumberFormat.getInstance();
                    int unit = Config.get().getUnit();
                    long lv = v.longValue();
                    if(unit==1) {
                        setText("" + nf.format(v.longValue()));
                    }else{
                        setText("" + Util.getAutoSize(lv, unit));
                    }
                }catch (Exception e) {
                    setText("" + v);
                }
            }

            setAlignment(Pos.CENTER_RIGHT);
        }else {
            setText("");
        }
    }

}
