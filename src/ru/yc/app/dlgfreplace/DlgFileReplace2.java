package ru.yc.app.dlgfreplace;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import ru.yc.app.util.UiUtil;

public class DlgFileReplace2 extends DlgFileReplace {

  public DlgFileReplace2(Stage owner, String title){
    super(owner, title);
  }

  protected Button[] initButtons(){
    Button btnY = new Button(rs.getString("yes"));
    btnY.setOnAction(evt-> onSave());
    btnY.setMinWidth(UiUtil.getWidgetWidth(75));
    btnY.setDefaultButton(true);

    Button btnA = new Button(rs.getString("All"));
    btnA.setOnAction(evt-> onButton(4));
    btnA.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnS = new Button(rs.getString("Skip"));
    btnS.setOnAction(evt-> onButton(2));
    btnS.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnSA = new Button(rs.getString("Skip all"));
    btnSA.setOnAction(evt-> onButton(3));
    btnSA.setMinWidth(UiUtil.getWidgetWidth(75));

    Button btnX = new Button(rs.getString("cancel"));
    btnX.setOnAction(evt-> close());
    btnX.setMinWidth(UiUtil.getWidgetWidth(75));

    return new Button[] {btnY, btnA, btnS, btnSA, btnX};
  }
}
