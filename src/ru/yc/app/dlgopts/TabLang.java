package ru.yc.app.dlgopts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Pair;
import ru.yc.app.Config;
import ru.yc.app.ICallback;

import java.util.Arrays;
import java.util.ResourceBundle;

public class TabLang extends TabBase implements ICallback {
  DlgOptions3 parent;
  ChoiceBox<Option> cbxLang;
  Label labTitle;

  public Region buildTab(ResourceBundle rs, DlgOptions parent){
    return null;
  }

  public Region buildTab(ResourceBundle rs, DlgOptions3 parent, String title){
    this.parent = parent;
    Config cfg = Config.get();

    GridPane pane = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane.setPadding(new Insets(10));
    pane.setHgap(5);
    pane.setVgap(5);
    // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

    labTitle = getTitleLabel(title);
    pane.add(labTitle,0,0);
    GridPane.setMargin(labTitle, new Insets(2, 0, 2, 10+4));

    Label lab = new Label(rs.getString("Language")+":");
    pane.add(lab,0,1);
    GridPane.setMargin(lab, new Insets(0+4, 0, 0, 10+4));

    Option[] oo = getLanguages();
    ObservableList<Option> v = FXCollections.observableArrayList(oo);
    cbxLang = new ChoiceBox<Option>(v);
    pane.add(cbxLang,1,1);
    GridPane.setMargin(cbxLang, new Insets(0+4, 0, 0, 0));
    lab.setLabelFor(cbxLang);

    javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/ico_i.png"));
    Label labi = new Label(rs.getString("lang_reload_app"));
    labi.setGraphic(new ImageView(image));
    pane.add(labi,1,2);
    GridPane.setMargin(labi, new Insets(6, 10, 0, 0));

    String l = cfg.getLang();
    if(l==null || l.trim().isEmpty())
      l = "EN";
    final String l1 = l;
    Option opt = Arrays.stream(oo).filter(op -> l1.equals(op.getKey())).findAny().orElse(null);
    if(opt!=null) {
      cbxLang.setValue(opt);
    }else{
      cbxLang.setValue(oo[0]);
    }

    return pane;
  }
  private Option[] getLanguages(){
    Option[] oo = new Option[2];
    oo[0] = new Option("EN", "English");
    oo[1] = new Option("RU", "Pyccкий");
    return oo;
  }

  public Object onAction(Object o){
    Config cfg = (Config)o;

    Option opt = cbxLang.getValue();
    if(opt!=null){
      cfg.setLang(opt.getKey());
    }else{
      cfg.setLang("EN");
    }
    cfg.setTextResourceL(cfg.getLang());

    return o;
  }

  class Option extends Pair<String, String> {
    public Option(String s, String s2) { super(s, s2); }

    @Override
    public String toString() {
      return getValue();
    }
  }

}
