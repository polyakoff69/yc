package ru.pcom.app.mainwnd;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ru.pcom.app.sys.Os;

import java.util.HashMap;
import java.util.Map;


public abstract class MainFrmMenuToolBars extends MainFrmBase {

    protected Stage primStage;
    protected Controller ctr = null;
    protected Button tbBack, tbFwd, tbRen, tbOpt, tbCopy, tbMove, tbDel; // TODO: 2 map
    protected Button tbF1, tbF2, tbF3, tbF4, tbF5, tbF6, tbF7, tbF8, tbF9, tbF10, tbF11, tbF12; // TODO: 2 map

    protected final String SP8 = "        ", PT3 = "...";

    protected Map<String, MenuItem> mii = new HashMap<>();

    public Stage getPrimStage() {
        return primStage;
    }

    public void setPrimStage(Stage primStage) {
        this.primStage = primStage;
    }


    protected MenuBar buildMenu(){ // Create MenuBar

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu(" "+rsTxt.getString("file")); // Create menus
        Menu leftMenu = new Menu(" "+rsTxt.getString("left")); // Create menus
        Menu optMenu = new Menu(" "+rsTxt.getString("options"));
        Menu cmdMenu = new Menu(" "+rsTxt.getString("commands"));
        Menu rightMenu = new Menu(" "+rsTxt.getString("right"));

        //fileMenu.setStyle("-fx-color: white");

        Menu editMenu = new Menu(" "+rsTxt.getString("edit"));
        Menu helpMenu = new Menu(" "+rsTxt.getString("help"));

        buildMenuLR(leftMenu, true);
        buildMenuLR(rightMenu, false);
        buildMenuCmd(cmdMenu);
        buildMenuOpt(optMenu);
        buildMenuFile(fileMenu);

        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        editMenu.getItems().addAll(copyItem, pasteItem);

        MenuItem mi = new MenuItem(rsTxt.getString("about")+SP8);
        mi.setAccelerator(new KeyCodeCombination(KeyCode.F1, KeyCombination.SHIFT_DOWN));
        mi.setOnAction(evt->ctr.onCmd("about", null, evt));
        helpMenu.getItems().addAll(mi);

        menuBar.getMenus().addAll(leftMenu, fileMenu, editMenu, cmdMenu, optMenu, rightMenu, helpMenu); // Add Menus to the MenuBar

        menuBar.setFocusTraversable(false);

        return menuBar;
    }

    protected void buildMenuLR(Menu menu, boolean left){
        if(Os.isWindows()){
            String chdrive = "chdrive";
            MenuItem mi = new MenuItem(rsTxt.getString(chdrive)+SP8);
            mi.setAccelerator(new KeyCodeCombination(left ? KeyCode.F1 : KeyCode.F2, KeyCombination.ALT_DOWN));
            Image image = new Image(getClass().getResourceAsStream("/ico_disk.png"));
            mi.setGraphic(new ImageView(image));
            mi.setOnAction(evt-> {
                setActiveTab(left);
                ctr.onCmd(chdrive+(left ? "L" : "R"), null, evt);
            });
            menu.getItems().add(mi);
            mii.put((left ? "L" : "R")+chdrive, mi);
        }

        Menu goMenu = new Menu(" "+rsTxt.getString("goto"));

        MenuItem mi = new MenuItem(rsTxt.getString("back"));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN));
        Image image = new Image(getClass().getResourceAsStream("/ico_b_arrl2.png"));
        mi.setGraphic(new ImageView(image));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("history<", null, evt);
        });
        goMenu.getItems().add(mi);
        mii.put((left ? "L" : "R")+":back", mi);

        mi = new MenuItem(rsTxt.getString("forward"));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN));
        image = new Image(getClass().getResourceAsStream("/ico_b_arrr2.png"));
        mi.setGraphic(new ImageView(image));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("history>", null, evt);
        });
        goMenu.getItems().add(mi);
        mii.put((left ? "L" : "R")+":fwd", mi);
        mii.put((left ? "L" : "R")+":forward", mi);

        mi = new MenuItem(rsTxt.getString("root_folder")+SP8);
        if(Os.isWindows()){
            image = new Image(getClass().getResourceAsStream("/ico_rootw.png"));
        }else {
            image = new Image(getClass().getResourceAsStream("/ico_rootl.png"));
        }
        mi.setGraphic(new ImageView(image));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.SHIFT_DOWN));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("root_folder", (left ? "left" : "right"), evt);
        });
        goMenu.getItems().add(mi);
        mii.put((left ? "L" : "R")+":root_folder", mi);

        mi = new MenuItem(rsTxt.getString("home_folder")+SP8);
        image = new Image(getClass().getResourceAsStream("/ico_home.png"));
        mi.setGraphic(new ImageView(image));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.HOME, KeyCombination.CONTROL_DOWN));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("home_folder", (left ? "left" : "right"), evt);
        });
        goMenu.getItems().add(mi);
        mii.put((left ? "L" : "R")+":home_folder", mi);

        SeparatorMenuItem sep = new SeparatorMenuItem();

        mi = new MenuItem(rsTxt.getString("pan5050")+SP8);
        image = new Image(getClass().getResourceAsStream("/ico_5050.png"));
        mi.setGraphic(new ImageView(image));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.F11, KeyCombination.CONTROL_DOWN));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("5050", (left ? "left" : "right"), evt);
        });
        MenuItem mi5050 = mi;
        mii.put((left ? "L" : "R")+":5050", mi);

        mi = new MenuItem(rsTxt.getString("pan100")+SP8);
        image = new Image(getClass().getResourceAsStream((left ? "/ico_max_l.png" : "/ico_max_r.png")));
        mi.setGraphic(new ImageView(image));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("100", (left ? "left" : "right"), evt);
        });
        MenuItem mi100 = mi;
        mii.put((left ? "L" : "R")+":100", mi);

        SeparatorMenuItem sep1 = new SeparatorMenuItem();

        mi = new MenuItem(rsTxt.getString("refresh"));
        image = new Image(getClass().getResourceAsStream("/ico_refr.png"));
        mi.setGraphic(new ImageView(image));
        mi.setAccelerator(new KeyCodeCombination(KeyCode.F9, KeyCombination.CONTROL_DOWN));
        mi.setOnAction(evt-> {
            setActiveTab(left);
            ctr.onCmd("refresh", (left ? "left" : "right"), evt);
        });
        MenuItem miRefresh = mi;
        mii.put((left ? "L" : "R")+":refresh", mi);

        menu.getItems().addAll(goMenu, sep, mi100, mi5050, sep1, miRefresh);
    }

    protected void buildMenuOpt(Menu menu){
        MenuItem mi = new MenuItem(rsTxt.getString("options")+PT3);
        // mi.setAccelerator(new KeyCodeCombination(KeyCode., KeyCombination.ALT_DOWN));
        Image image = new Image(getClass().getResourceAsStream("/ico_opts.png"));
        mi.setGraphic(new ImageView(image));
        mi.setOnAction(evt-> ctr.onCmd("options", null, evt));
        menu.getItems().add(mi);
        mii.put("options", mi);
    }

    protected void buildMenuFile(Menu menu){
        MenuItem exitItem = new MenuItem(rsTxt.getString("exit"));
        exitItem.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
        exitItem.setOnAction(evt -> {
            Window window = getPrimStage().getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        MenuItem renameItem = new MenuItem(rsTxt.getString("rename2")+PT3+SP8);
        renameItem.setAccelerator(new KeyCodeCombination(KeyCode.F2));
        Image image = new Image(getClass().getResourceAsStream("/ico_renb.png"));
        renameItem.setGraphic(new ImageView(image));
        renameItem.setOnAction(evt-> ctr.onCmd("rename", null, evt));
        mii.put("rename", renameItem);

        MenuItem copyItem = new MenuItem(rsTxt.getString("copy2")+PT3+SP8);
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        image = new Image(getClass().getResourceAsStream("/ico_copy.png"));
        copyItem.setGraphic(new ImageView(image));
        copyItem.setOnAction(evt-> ctr.onCmd("fcopy", null, evt));
        mii.put("fcopy", copyItem);

        MenuItem moveItem = new MenuItem(rsTxt.getString("move2")+PT3+SP8);
        moveItem.setAccelerator(new KeyCodeCombination(KeyCode.F6));
        image = new Image(getClass().getResourceAsStream("/ico_move.png"));
        moveItem.setGraphic(new ImageView(image));
        moveItem.setOnAction(evt-> ctr.onCmd("fmove", null, evt));
        mii.put("fmove", moveItem);

        MenuItem delItem = new MenuItem(rsTxt.getString("delete2")+PT3+SP8);
        delItem.setAccelerator(new KeyCodeCombination(KeyCode.F8));
        image = new Image(getClass().getResourceAsStream("/ico_del.png"));
        delItem.setGraphic(new ImageView(image));
        delItem.setOnAction(evt-> ctr.onCmd("fdelete", null, evt));
        mii.put("fdelete", delItem);

        SeparatorMenuItem sep = new SeparatorMenuItem();
        menu.getItems().addAll(renameItem, copyItem, moveItem, delItem, sep, exitItem); // Add menuItems to the Menus
    }

    protected void buildMenuCmd(Menu menu){
        MenuItem mi = new MenuItem(rsTxt.getString("mkdir")+PT3+SP8);
        mi.setAccelerator(new KeyCodeCombination(KeyCode.F7));
        Image image = new Image(getClass().getResourceAsStream("/ico_nfol.png"));
        mi.setGraphic(new ImageView(image));
        mi.setOnAction(evt-> ctr.onCmd("mkdir", null, evt));
        menu.getItems().add(mi);
        mii.put("mk_dir", mi);
    }

    protected ToolBar buildToolBar(){
        ToolBar toolBar = new ToolBar();

        Button button = new Button(rsTxt.getString("back").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("back").replace("_","")));
        Image image = new Image(getClass().getResourceAsStream("/ico_b_arrl.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("history<", null, event);});
        tbBack = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("forward").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("forward").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_b_arrr.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("history>", null, event);});
        tbFwd = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("rename2").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("rename2").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_renw.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("rename", null, event);});
        tbRen = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("copy").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("copy").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_copw.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("fcopy", null, event);});
        tbCopy = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("move").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("move2").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_movw.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("fmove", null, event);});
        tbMove = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("delete").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("delete").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_delw.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("fdelete", null, event);});
        tbDel = button;
        toolBar.getItems().add(button);

        button = new Button(rsTxt.getString("options").replace("_",""));
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("options").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_optw.png"));
        button.setGraphic(new ImageView(image));
        button.setOnAction(event -> {ctr.onCmd("options", null, event);});
        tbOpt = button;
        toolBar.getItems().add(button);

        toolBar.setFocusTraversable(false);

        toolBar.getStyleClass().add("main-tool-bar");

        return toolBar;
    }

    protected ToolBar buildFootToolBar(){
        ToolBar toolBar = new ToolBar();
        toolBar.getStyleClass().add("fbar");

        String s = rsTxt.getString("help2").replace("_","");
        Button button = new Button("F1 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        button.setOnMouseClicked(event -> {
            if (event.isAltDown())
                ctr.onCmd("chdriveL", null, event);
        });
        tbF1 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("rename2").replace("_","");
        button = new Button("F2 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        button.setOnAction(event -> ctr.onCmd("rename", null, event));
        button.setOnMouseClicked(event -> {
            if (event.isAltDown())
                ctr.onCmd("chdriveR", null, event);
        });
        tbF2 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("view").replace("_","");
        button = new Button("F3 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        tbF3 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("edit").replace("_","");
        button = new Button("F4 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        tbF4 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("copy").replace("_","");
        button = new Button("F5 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        button.setOnAction(event -> ctr.onCmd("fcopy", null, event));
        tbF5 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("move").replace("_","");
        button = new Button("F6 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("move2").replace("_","")));
        button.setOnAction(event -> ctr.onCmd("fmove", null, event));
        tbF6 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("mkdir").replace("_","");
        button = new Button("F7 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        button.setOnAction(event -> ctr.onCmd("mkdir", null, event));
        tbF7 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("delete").replace("_","");
        button = new Button("F8 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        button.setOnAction(event -> ctr.onCmd("fdelete", null, event));
        tbF8 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("usermenu").replace("_","");
        button = new Button("F9 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(s));
        tbF9 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("menu").replace("_","");
        button = new Button("F10 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("menu_tt").replace("_","")));
        button.setOnAction(event -> ctr.onCmd("menu", null, event));
        tbF10 = button;
        toolBar.getItems().add(button);

        s = rsTxt.getString("pan100s").replace("_","");
        button = new Button("F11 "+s);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(rsTxt.getString("pan100").replace("_","")));
        button.setOnAction(event -> ctr.onCmd("100", null, event));
        tbF11 = button;
        toolBar.getItems().add(button);

        toolBar.setFocusTraversable(false);

        return toolBar;
    }

}
