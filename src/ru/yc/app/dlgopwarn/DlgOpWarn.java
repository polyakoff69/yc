package ru.yc.app.dlgopwarn;

import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.yc.app.dlgerror.DlgOpError;
import ru.yc.app.util.*;

public class DlgOpWarn extends DlgOpError {
  public DlgOpWarn(Stage owner, String title, String item) {
    super(owner, title, item);
  }

  protected void initControls(){
    super.initControls();

    labErr.setText("");
  }

  protected Button[] initButtons(){
    Button btnY = new Button(rs.getString("yes"));
    btnY.setOnAction(evt-> onButton(1));
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

    return new Button[] {btnY, btnA, btnS, btnSA, btnX}; }
}

