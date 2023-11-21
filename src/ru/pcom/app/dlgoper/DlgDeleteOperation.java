package ru.pcom.app.dlgoper;

import javafx.stage.Stage;

public class DlgDeleteOperation extends DlgOperation {

  public DlgDeleteOperation(Stage owner, String title, String promptId) {
    super(owner, title, promptId);
  }

  protected void initControls(){
    file2 = false;
    super.initControls();
  }
}
