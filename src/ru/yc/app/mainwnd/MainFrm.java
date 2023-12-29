package ru.yc.app.mainwnd;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import ru.yc.app.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainFrm extends MainFrmCtrl {

    public MainFrm() {
        ctr = new Controller(this);
    }

    public Scene build() {
        Config CFG = Config.get();
        this.CFG = CFG;

        BorderPane root = new BorderPane();
        root.setTop(buildMenu());
        root.setCenter(buildCont1(CFG));
        root.setBottom(buildFootToolBar());
        Scene scene = new MainFrameScene(root, this, CFG.getFrmW(), CFG.getFrmH());

        setAccelerators(scene);

        return scene;
    }

    private Pane buildCont1(Config CFG) {
        BorderPane p = new BorderPane();

        p.setTop(buildToolBar());

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.getStyleClass().add("main-split-pane");
        split.setDividerPositions((double) CFG.getSplit());
        this.split = split;
        splitCtxMenu();

        tab1 = buildPanel(true, CFG);
        tab2 = buildPanel(false, CFG);
        split.getItems().add(tab1);
        split.getItems().add(tab2);
        p.setCenter(split);

        if("right".equalsIgnoreCase(CFG.getDefSide())){
            tab2.requestFocus();
            onChFocus("right");
            getActivePanel(tab2).focus();
        }

        return p;
    }

    private void splitCtxMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem mi50 = new MenuItem("_50 / 50");
        Image image = new Image(getClass().getResourceAsStream("/ico_5050.png"));
        mi50.setGraphic(new ImageView(image));

        MenuItem mi100l = new MenuItem(CFG.getTextResource().getString("left100"));
        image = new Image(getClass().getResourceAsStream("/ico_max_l.png"));
        mi100l.setGraphic(new ImageView(image));

        MenuItem mi100r = new MenuItem(CFG.getTextResource().getString("right100"));
        image = new Image(getClass().getResourceAsStream("/ico_max_r.png"));
        mi100r.setGraphic(new ImageView(image));

        contextMenu.getItems().addAll(mi50, mi100l, mi100r);

        mi50.setOnAction((ActionEvent e) -> { split.setDividerPositions(0.5); });
        mi100l.setOnAction((ActionEvent e) -> { split.setDividerPositions(1.0); });
        mi100r.setOnAction((ActionEvent e) -> { split.setDividerPositions(0.0); });

        split.setOnContextMenuRequested(evt -> {
            String c = "";
            if(evt.getTarget()!=null){
                c = ""+evt.getTarget().getClass();
            }
            if(c.contains("SplitPane")) {
                contextMenu.show(split, evt.getScreenX(), evt.getScreenY());
            }
        });
    }

    private TabPane buildPanel(boolean bLeft, Config CFG) {
        TabPane pane = new TabPane();
        // pane.setStyle("-fx-border: red");
        pane.getStyleClass().add("files-tab-pane");

        String[] tabPaths = CFG.getTabs().get((bLeft ? "l" : "r"));
        if (tabPaths == null || tabPaths.length < 1)
            tabPaths = new String[]{"/"};

        String[] tabColW = CFG.getTabs().get((bLeft ? "wl" : "wr"));

        int i = 0;
        for (String path : tabPaths) {
            FilePanel tab = new FilePanel();
            tab.setPath(path);
            String colw = "";
            if(tabColW!=null && tabColW.length>i)
                colw = tabColW[i];
            tab.onCreate(bLeft, ctr, CFG, colw);
            pane.getTabs().add(tab);
            filePanels.add(tab);
            i++;
        }

        int defIx = (bLeft ? CFG.getDefTabL() : CFG.getDefTabR());
        if(defIx>=0 && defIx<i) {
            pane.getSelectionModel().select(defIx);
        }

        pane.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue){
                if (newValue){
                    getActivePanel(pane).focus();
                }
            }
        });

        pane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) { // при активации таба
                if(t1 instanceof FilePanel) {
                    FilePanel fp = (FilePanel) t1;
                    if(CFG.isReadOnActiv() || fp.isFilesRead()==false) { // либо всегда при активации либо 1й раз
                        fp.reloadFiles();
                    }
                }
            }
        });

        return pane;
    }

    private void setAccelerators(Scene scene){
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    if(tbBack!=null){
                        tbBack.fire();
                    }
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    if(tbFwd!=null){
                        tbFwd.fire();
                    }
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F9, KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onRefresh(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.HOME, KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    openHomeFolder(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination keyCombF4 = new KeyCodeCombination(KeyCode.F4);
            final KeyCombination keyCombF3 = new KeyCodeCombination(KeyCode.F3);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onRename(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
                if (keyCombF3.match(ke)) {
                    onView(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
                if (keyCombF4.match(ke)) {
                    onEdit(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F5);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onCopyMove("fcopy",null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F6);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onCopyMove("fmove",null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F11, KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onSplitPos("5050", null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F11);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onSplitPos("100", null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    Config CFG = Config.get();
                    CFG.setHideFiles(!CFG.isHideFiles());
                    onRefresh("left");
                    onRefresh("right");
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.DELETE);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    onDelete(null);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }

    public void onPostCreate(WindowEvent windowEvent) {
        for (FilePanel fp : filePanels) {
            if(fp.isSelected() || CFG.isEagerLoad())
                fp.readFiles(null);
        }
    }

    public String getSelectedTab(){
        return lastFocus;
    }

    public int getSelectedTab(String side){
        try {
            TabPane tab = tab1;
            if(!"left".equalsIgnoreCase(side))
                tab = tab2;
            return tab.getSelectionModel().getSelectedIndex();
        }catch (Exception e){
            return 0;
        }
    }

    public Map<String, String[]> getTabsCfg() {
        Map<String, String[]> m = new LinkedHashMap<>();
        m.put("l", getTabsCfg(true));
        m.put("r", getTabsCfg(false));
        m.put("wl", getTabsColW(true));
        m.put("wr", getTabsColW(false));
        return m;
    }

    public String[] getTabsCfg(boolean bLeft) { // tabs paths
        TabPane tab = (bLeft ? tab1 : tab2);
        ObservableList<Tab> tt = tab.getTabs();
        if (tt == null)
            return new String[0];

        List<String> v = new ArrayList<>();
        for (Tab t : tt) {
            if (!(t instanceof FilePanel))
                continue;

            FilePanel fp = (FilePanel) t;
            v.add(fp.getPath());
        }

        return v.toArray(new String[v.size()]);
    }

    public String[] getTabsColW(boolean bLeft) { // col widths
        TabPane tab = (bLeft ? tab1 : tab2);
        ObservableList<Tab> tt = tab.getTabs();
        if (tt == null)
            return new String[0];

        List<String> v = new ArrayList<>();
        for (Tab t : tt) {
            if (!(t instanceof FilePanel))
                continue;

            FilePanel fp = (FilePanel) t;
            v.add(fp.getColW());
        }

        return v.toArray(new String[v.size()]);
    }

    public Config getConfig(Config CFG) {
        float f = 0.5F;
        double[] dd = split.getDividerPositions();
        if(dd!=null && dd.length>0){
            f = (float) dd[0];
        }
        CFG.setSplit(f);

        CFG.setTabs(getTabsCfg());
        CFG.setDefSide(getSelectedTab());
        CFG.setDefTabL(getSelectedTab("left"));
        CFG.setDefTabR(getSelectedTab("right"));
        return CFG;
    }

    protected void setActiveTab(boolean left){
        if(left){
            tab1.requestFocus();
            onChFocus("left");
        }else{
            tab2.requestFocus();
            onChFocus("right");
        }
    }

    public void updateControls(String cmd){
        TabPane[] tabs = new TabPane[]{tab1, tab2};
        for(TabPane t : tabs) {
            ObservableList<Tab> tt = t.getTabs();
            tt.forEach(pan -> {((FilePanel)pan).updateControls();});
        }

        TabPane tab = null;
        if("left".equalsIgnoreCase(lastFocus))
            tab = tab1;
        if("right".equalsIgnoreCase(lastFocus))
            tab = tab2;

        FilePanel fp = getActivePanel(tab);
        if(fp!=null){
            tbBack.setDisable(!fp.hasHistBack());
            tbFwd.setDisable(!fp.hasHistFwd());
            if(!"focus".equalsIgnoreCase(cmd))
                fp.focus();

            boolean fof = fp.isSelectedFileOrFolder();
            if(fof){
                tbF2.setDisable(false);
                tbF5.setDisable(false);
                tbF6.setDisable(false);
                tbF8.setDisable(false);
            }else {
                tbF2.setDisable(true);
                tbF5.setDisable(true);
                tbF6.setDisable(true);
                tbF8.setDisable(true);
            }
        }else{
            tbBack.setDisable(true);
            tbFwd.setDisable(true);

            tbF2.setDisable(true);
            tbF6.setDisable(true);
            tbF5.setDisable(true);
            tbF8.setDisable(true);
        }

        tbRen.setDisable(tbF2.isDisabled());
        mii.get("rename").setDisable(tbF2.isDisabled());
        tbCopy.setDisable(tbF5.isDisabled());
        mii.get("fcopy").setDisable(tbF5.isDisabled());
        tbMove.setDisable(tbF6.isDisabled());
        mii.get("fmove").setDisable(tbF6.isDisabled());
        tbDel.setDisable(tbF8.isDisabled());
        mii.get("fdelete").setDisable(tbF8.isDisabled());

        String side = "L:";
        for(TabPane t : tabs) {
            fp = getActivePanel(t);
            if (fp != null) {
                mii.get(side+"back").setDisable(!fp.hasHistBack());
                mii.get(side+"fwd").setDisable(!fp.hasHistFwd());
            } else {
                mii.get(side+"back").setDisable(true);
                mii.get(side+"fwd").setDisable(true);
            }
            side = "R:";
        }

    }

}
