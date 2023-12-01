package ru.yc.app.dlgopts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Pair;
import ru.yc.app.Config;
import ru.yc.app.ICallback;

import java.util.Arrays;
import java.util.ResourceBundle;

public class TabGeneral implements ICallback {

    CheckBox cbEagerLoad, cbReanOnAct, cbHideFiles;
    Label labUnit;
    ChoiceBox<Option> cbxUnit;

    public Region buildTab(ResourceBundle rs, DlgOptions parent){
        Config cfg = Config.get();

        GridPane pane = new GridPane();
        pane.getStyleClass().add("dlg-tab-container");
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);
        // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

        cbHideFiles = new CheckBox(rs.getString("hide_files"));
        pane.add(cbHideFiles,0,0, 2, 1);
        GridPane.setMargin(cbHideFiles, new Insets(0+4, 0, 0, 10+4));

        cbEagerLoad = new CheckBox(rs.getString("eager_load"));
        pane.add(cbEagerLoad,0,1, 2, 1);
        GridPane.setMargin(cbEagerLoad, new Insets(0, 0, 0, 10+4));

        CheckBox cb = new CheckBox(rs.getString("read_on_activ"));
        pane.add(cb,0,2, 2 ,1);
        GridPane.setMargin(cb, new Insets(0, 0, 0, 10+4));
        cbReanOnAct = cb;

        labUnit = new Label(rs.getString("sz_unit")+":");
        pane.add(labUnit,0,3, 1, 1);
        GridPane.setMargin(labUnit, new Insets(0, 0, 0, 10+4));

        Option[] oo = new Option[3];
        oo[0] = new Option(0, rs.getString("auto"));
        oo[1] = new Option(1, rs.getString("val_byte"));
        oo[2] = new Option(2, rs.getString("val_kb"));
        ObservableList<Option> v = FXCollections.observableArrayList(oo);
        cbxUnit = new ChoiceBox<Option>(v);
        pane.add(cbxUnit,1,3);
        labUnit.setLabelFor(cbxUnit);

        cbEagerLoad.setSelected(cfg.isEagerLoad());
        cbReanOnAct.setSelected(cfg.isReadOnActiv());
        cbHideFiles.setSelected(cfg.isHideFiles());

        int l1 = cfg.getUnit();
        Option opt = Arrays.stream(oo).filter(op -> l1 == (op.getKey())).findAny().orElse(null);
        if(opt!=null) {
            cbxUnit.setValue(opt);
        }else{
            cbxUnit.setValue(oo[0]);
        }

        return pane;
    }

    public Object onAction(Object o){
        Config cfg = (Config)o;
        cfg.setEagerLoad(cbEagerLoad.isSelected());
        cfg.setReadOnActiv(cbReanOnAct.isSelected());
        cfg.setHideFiles(cbHideFiles.isSelected());

        Option opt = cbxUnit.getValue();
        if(opt!=null){
            cfg.setUnit(opt.getKey());
        }else{
            cfg.setUnit(0);
        }

        return o;
    }

    class Option extends Pair<Integer, String> {
        public Option(Integer x, String s) { super(x, s); }

        @Override
        public String toString() {
            return getValue();
        }
    }
}
