package ru.yc.app.dlgopts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import ru.yc.app.Config;
import ru.yc.app.ICallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TabAppearance implements ICallback {
  DlgOptions parent;
  ChoiceBox<String> cbxFont, cbxFontSz;
  private Label labSample;

  public Region buildTab(ResourceBundle rs, DlgOptions parent){
    this.parent = parent;
    Config cfg = Config.get();

    GridPane pane = new GridPane();
    pane.getStyleClass().add("dlg-tab-container");
    pane.setPadding(new Insets(10));
    pane.setHgap(5);
    pane.setVgap(5);
    // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

    Label lab = new Label(rs.getString("Font")+":");
    pane.add(lab,0,0);
    GridPane.setMargin(lab, new Insets(0+4, 0, 0, 10+4));

    ObservableList<String> fonts = FXCollections.observableArrayList(getFonts());
    cbxFont = new ChoiceBox<String>(fonts);
    // cbxFont.setValue("Java");
    pane.add(cbxFont,1,0);
    lab.setLabelFor(cbxFont);

    ObservableList<String> sz = FXCollections.observableArrayList(getSz());
    cbxFontSz = new ChoiceBox<String>(sz);
    cbxFontSz.setValue("12");
    pane.add(cbxFontSz,2,0);

    labSample = new Label(rs.getString("quick_fox"));
    labSample.setMaxWidth(400);
    labSample.setWrapText(true);
    pane.add(labSample,1,1,2,1);
    cbxFont.setOnAction(event -> setFontSample(cbxFont.getValue(),cbxFontSz.getValue()));
    cbxFontSz.setOnAction(event -> setFontSample(cbxFont.getValue(),cbxFontSz.getValue()));

    javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/ico_i.png"));
    Label labi = new Label(rs.getString("font_reload_app"));
    labi.setGraphic(new ImageView(image));
    pane.add(labi,1,2,2,1);
    GridPane.setMargin(labi, new Insets(6, 10, 0, 0));
    GridPane.setMargin(labSample, new Insets(6, 10, 0, 0));
    GridPane.setMargin(cbxFont, new Insets(0+4, 0, 0, 0));
    GridPane.setMargin(cbxFontSz, new Insets(0+4, 0, 0, 0));

    String f = "12";
    if(cfg.getFontSz()>4)
      f = ""+cfg.getFontSz();
    cbxFontSz.setValue(f);

    f = cfg.getFont();
    if(f!=null && f.trim().isEmpty()==false)
      cbxFont.setValue(f);

    return pane;
  }

  private void setFontSample(String font, String sz){
    labSample.setStyle("-fx-font-family: \""+font+"\"; -fx-font-size: "+sz);
  }

  private String[] getFonts(){
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String fontNames[] = ge.getAvailableFontFamilyNames();
    return fontNames;
  }

  private String[] getSz(){
    java.util.List<String> v = new ArrayList<>();
    for(int s=8;s<=32;s++){
      v.add(""+s);
    }
    return v.toArray(new String[v.size()]);
  }

  public Object onAction(Object o){
    Config cfg = (Config)o;
    cfg.setFont(cbxFont.getValue());
    cfg.setFontSz(Integer.parseInt(cbxFontSz.getValue()));
    parent.getScene().getWindow().sizeToScene();
    return o;
  }

}
