package ru.yc.app.dlgopts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import ru.yc.app.Config;
import ru.yc.app.ICallback;
import ru.yc.app.sys.Os;

import java.util.ResourceBundle;

public class TabViewEdit extends TabBase implements ICallback {

  private Pane thisPane;
  private ListView<String> list;

  public Region buildTab(ResourceBundle rs, DlgOptions parent){
    return null;
  }

  public Region buildTab(ResourceBundle rs, DlgOptions3 parent, String title){
    Config cfg = Config.get();

    GridPane pane = new GridPane();
    // pane.getStyleClass().add("dlg-tab-container");
    pane.setPadding(new Insets(10));
    pane.setHgap(5);
    pane.setVgap(0);
    // pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1 1 1 1");

    Label labTitle = getTitleLabel(title);
    pane.add(labTitle,0,0, 1, 1);
    GridPane.setMargin(labTitle, new Insets(2, 0, 2+5, 4));

    ToolBar tb = new ToolBar();
    tb.getStyleClass().add("file-panel-toolbar");

    Label labInfo = new Label(" "+cfg.getTextResource().getString("File_mask_prior")+" ");
    BorderPane p = new BorderPane();
    p.setCenter(labInfo);
    HBox hb = new HBox();
    hb.setHgrow(p, Priority.ALWAYS);
    labInfo.setAlignment(Pos.CENTER_LEFT);
    hb.setFillHeight(true);
    hb.setAlignment(Pos.CENTER_LEFT);
    tb.getItems().add(p);

    Button tbAdd = new Button("");
    tbAdd.setFocusTraversable(false);
    tbAdd.setTooltip(new Tooltip(cfg.getTextResource().getString("root_folder")));
    Image image = new Image(getClass().getResourceAsStream("/ico_rootw.png"));
    tbAdd.setGraphic(new ImageView(image));
    // button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));
    tb.getItems().add(tbAdd);

    tb.setFocusTraversable(false);

    pane.add(tb,0,1, 1, 1);

    list = new ListView<>();
    pane.add(list,0,2, 1, 1);

    thisPane = pane;

    return pane;
  }

  public void setPrefW(double w){
    super.setPrefW(w);
    thisPane.setPrefWidth(w);
    list.setPrefWidth(w);
  }

  public Object onAction(Object o){
    return o;
  }
}
