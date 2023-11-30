package ru.pcom.app.dlgopwarn;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pcom.app.Config;
import ru.pcom.app.dlgerror.DlgOpError;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.util.*;

import java.io.File;
import java.util.ResourceBundle;

public class DlgOpWarn extends DlgOpError {
  public DlgOpWarn(Stage owner, String title, String item) {
    super(owner, title, item);
  }

}

