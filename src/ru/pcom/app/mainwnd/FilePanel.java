package ru.pcom.app.mainwnd;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.util.Callback;
import ru.pcom.app.Config;
import ru.pcom.app.file.FileData;
import ru.pcom.app.file.FileExplorer;
import ru.pcom.app.file.FileSysExplorer;
import ru.pcom.app.file.IFilePanel;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.gui.StatusLine;
import ru.pcom.app.sys.Os;
import ru.pcom.app.util.Str;
import ru.pcom.app.util.Util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class FilePanel extends Tab implements IFilePanel {

    private String path = "";
    private int mode = 0;

    private boolean filesRead = false;

    private TableView tableView ;
    private MenuButton btnDisk =null;
    private StatusLine status;

    private Stack<File> histBack = new Stack<>(), histFwd = new Stack<>();
    private File prevPath = null;
    private File selFile = null;

    private FileExplorer fileExplorer = null;
    private long fxId = 0L;

    private MainWndController wndController = null;
    protected Button tbRoot, tbAddPan, tbRefresh, tbHome;
    protected Label labInfo = new Label();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public File getSelFile() {
        return selFile;
    }

    public void setSelFile(File selFile) {
        this.selFile = selFile;
    }

    public void onCreate(boolean bLeft, MainWndController ctr, Config CFG, String colw){
        wndController = ctr;

        BorderPane lo = new BorderPane();
        ScrollPane scroll = new ScrollPane();
        lo.setCenter(scroll);

        /* ObservableList<CustomImage> imgList = FXCollections.observableArrayList();
        CustomImage item_1 = new CustomImage(new ImageView(new Image("folder.gif")));
        CustomImage item_2 = new CustomImage(new ImageView(new Image("folder.gif")));
        imgList.addAll(item_1, item_2); */

        Map<Integer, Double> mcw = Str.decodeNumStr(colw);

        TableView<FileData> table = new TableView<FileData>();
        tableView = table;
        table.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        TableColumn<FileData, FileData.Info> fileNameCol = new TableColumn<FileData, FileData.Info>(CFG.getTextResource().getString("pan_col_name"));
        fileNameCol.setId("name");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("info"));
        fileNameCol.setCellFactory(
                new Callback<TableColumn<FileData, FileData.Info>, TableCell<FileData, FileData.Info>>() {
                    @Override
                    public TableCell<FileData, FileData.Info> call(TableColumn<FileData, FileData.Info> param){
                        TableCell cell = new IcoTabCell();
                        return cell;
                    }
                }
        );
        int cc = 0;
        setColW(fileNameCol, cc++, mcw);

        // TableColumn<FileData, String> extCol = new TableColumn<FileData, String>(CFG.getTextResource().getString("pan_col_ext"));
        // TableColumn<FileData, String> fullNameCol = new TableColumn<FileData, String>(CFG.getTextResource().getString("pan_col_filename"));
        // fullNameCol.getColumns().addAll(fileNameCol, extCol);

        TableColumn<FileData, Long> sizeCol = new TableColumn<FileData, Long>(CFG.getTextResource().getString("pan_col_size"));
        sizeCol.setId("size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeCol.setCellFactory(
                new Callback<TableColumn<FileData, Long>, TableCell<FileData, Long>>() {
                    @Override
                    public TableCell<FileData, Long> call(TableColumn<FileData, Long> param){
                        TableCell cell = new FileSizeTabCell(CFG.getTextResource().getString("folder"));
                        return cell;
                    }
                }
        );

        // TableColumn<FileData, Boolean> activeCol = new TableColumn<FileData, Boolean>(CFG.getTextResource().getString("pan_col_active"));
        table.getColumns().addAll(fileNameCol, sizeCol);

        TableColumn<FileData, FileTime> dateCol = new TableColumn<FileData, FileTime>(CFG.getTextResource().getString("pan_col_date"));
        dateCol.setId("date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(column -> {
            return new TableCell<FileData, FileTime>() {
                @Override
                protected void updateItem(FileTime item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText("");
                    } else {
                        DateTimeFormatter fmt = Util.getDateFmt();
                        setText(fmt.format(item.toInstant()));
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
        table.getColumns().addAll(dateCol);

        TableColumn<FileData, FileTime> timeCol = new TableColumn<FileData, FileTime>(CFG.getTextResource().getString("pan_col_time"));
        timeCol.setId("time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellFactory(column -> {
            return new TableCell<FileData, FileTime>() {
                @Override
                protected void updateItem(FileTime item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText("");
                    } else {
                        DateTimeFormatter fmt = Util.getTimeFmt();
                        setText(fmt.format(item.toInstant()));
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        });
        table.getColumns().addAll(timeCol);

        TableColumn<FileData, String> attrCol = new TableColumn<FileData, String>(CFG.getTextResource().getString("pan_col_attr"));
        attrCol.setId("attr");
        attrCol.setCellValueFactory(new PropertyValueFactory<>("attr"));
        table.getColumns().addAll(attrCol);

        table.setPlaceholder(new Label(CFG.getTextResource().getString("pan_no_files")));

        table.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean old, Boolean nevv) {
                if(nevv!=null && nevv.booleanValue()) {
                    ctr.onCmd("focus", bLeft ? "left" : "right", null);
                    table.requestFocus();
                }
            }
        });

        table.sortPolicyProperty().set( new Callback<TableView<FileData>, Boolean>() {
            @Override
            public Boolean call(TableView<FileData> param) {
                Comparator<FileData> comparator = new Comparator<FileData>() {
                    @Override
                    public int compare(FileData r1, FileData r2) {
                        String colId = "";
                        TableColumn.SortType order = null;
                        final List<TableColumn<FileData, ?>> sortOrder = new ArrayList<>(tableView.getSortOrder());
                        try{
                            TableColumn<FileData, ?> col = sortOrder.get(0);
                            colId = col.getId();
                            order = col.getSortType();
                        }catch (Exception e){
                            // TODO:
                        }

                        if (r1.getInfo().isParent()) {
                            return -1;
                        } else if (r2.getInfo().isParent()) {
                            return 1;
                        }else if(r1.getInfo().isFolder() && r2.getInfo().isFolder()==false){
                            return -1;
                        }else if(r1.getInfo().isFolder()==false && r2.getInfo().isFolder()){
                            return 1;
                        } else if (param.getComparator() == null) {
                            return 0;
                        } else {
                            int x = 1;
                            if(TableColumn.SortType.ASCENDING.equals(order)==false)
                                x = -1;

                            if("name".equalsIgnoreCase(colId))
                              return x*(r1.getName().compareToIgnoreCase(r2.getName()));
                            if("size".equalsIgnoreCase(colId))
                              return x*(r1.getSize().compareTo(r2.getSize()));
                            if("date".equalsIgnoreCase(colId)) {
                                FileTime dt1 = FileTime.fromMillis(10000L), dt2 = dt1;
                                if(r1.getDate()!=null) {
                                    dt1 = r1.getDate();
                                }
                                if(r2.getDate()!=null) {
                                    dt2 = r2.getDate();
                                }
                                return x * (dt1.compareTo(dt2));
                            }
                            if("time".equalsIgnoreCase(colId)){
                                Instant in1 = Instant.ofEpochSecond(10000L), in2 = in1;
                                if(r1.getDate()!=null) {
                                    in1 = r1.getDate().toInstant();
                                }
                                if(r2.getDate()!=null) {
                                    in2 = r2.getDate().toInstant();
                                }
                                // Instant inx = in1.truncatedTo(ChronoUnit.DAYS);
                                /* in1 = in1.minusMillis(inx.toEpochMilli());
                                inx = in2.truncatedTo(ChronoUnit.DAYS);
                                in2 = in2.minusMillis(inx.toEpochMilli());
                                Long l1 = in1.toEpochMilli(), l2 = in2.toEpochMilli();
                                if(l1.longValue()>10000L*60*60*24)
                                    l1 = l1-10000L*60*60*24;
                                if(l2.longValue()>10000L*60*60*24)
                                    l2 = l2-10000L*60*60*24;
                                return x*(l1.compareTo(l2)); */
                                LocalTime tm1 = in1.atZone(ZoneOffset.UTC).toLocalTime(), tm2 = in2.atZone(ZoneOffset.UTC).toLocalTime();
                                if(tm1.equals(tm2))
                                    return 0;
                                boolean b = tm1.isAfter(tm2);
                                return x*(b ? 1 : 0);
                                // return x*(tm1.compareTo(tm2));
                            }
                            if("attr".equalsIgnoreCase(colId))
                                return x*(r1.getAttr().compareToIgnoreCase(r2.getAttr()));

                            return 0;
                        }
                    }
                };
                FXCollections.sort(table.getItems(), comparator);
                return true;
            }
        });

        table.getSortOrder().add(fileNameCol);

        // scroll.setContent(table);
        StackPane st = new StackPane();
        st.setPadding(new Insets(0));
        st.getChildren().add(table);
        lo.setCenter(st);

        this.setContent(lo);

        status = new StatusLine();
        status.init(CFG.getTextResource().getString("folder"));
        status.getStyleClass().add("file-panel-status");
        status.setFocusTraversable(false);
        lo.setBottom(status);

        ToolBar tb = buildToolBar(bLeft, ctr, CFG);
        tb.getStyleClass().add("file-panel-toolbar");
        if(bLeft)
            tb.getStyleClass().add("file-panel-toolbar-left");
        else
            tb.getStyleClass().add("file-panel-toolbar-right");

        lo.setTop(tb);

        setTitle();

        table.setOnKeyPressed(new javafx.event.EventHandler<KeyEvent>(){
          @Override
          public void handle(KeyEvent ke){
            if (ke.getCode().equals(KeyCode.ENTER))
              onOpenItem(CFG);
            }
        });

        table.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent me){
              ctr.onCmd("focus", bLeft ? "left" : "right", null);
              Platform.runLater(() -> {
                  tableView.requestFocus();
              });
              if(me.getTarget() instanceof Pane || me.getTarget() instanceof javafx.scene.text.Text) {
                  if (me.getClickCount() == 2 && me.getButton() == MouseButton.PRIMARY) // TODO:
                      onOpenItem(CFG);
              }
            }
        });

        setOnCloseRequest(event -> { // don't allow the last tab to be closed.
            TabPane tabPane = getTabPane();
            if (tabPane.getTabs().size() <= 1) {
                event.consume();
                return;
            }
        });

        setOnSelectionChanged(event -> {
            if (isSelected()) {
                Platform.runLater(() -> {
                    tableView.requestFocus();
                });
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(!multiSelect()) {
                if (newSelection != null) {
                    status.setFileData(newSelection);
                } else {
                    // TODO: no selection
                }
            }
            wndController.onCmd("updatectrl","", null);
        });
    }

    protected ToolBar buildToolBar(boolean bLeft, MainWndController ctr, Config CFG){
        ToolBar toolBar = new ToolBar();

        Image image = null;

        if(Os.isWindows()) {
            MenuItem[] mi = getDiskMenu(bLeft, ctr);

            image = new Image(getClass().getResourceAsStream("/ico_disk.png"));
            MenuButton button = new MenuButton("C:", null, mi);
            button.setFocusTraversable(false);
            button.setTooltip(new Tooltip(CFG.getTextResource().getString("button_disk"))); // TODO:
            button.setGraphic(new ImageView(image));
            toolBar.getItems().add(button);
            btnDisk = button;
            btnDisk.getStyleClass().add("ch-disk-button");
        }

        Button button = new Button("");
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(CFG.getTextResource().getString("root_folder")));
        if(Os.isWindows())
          image = new Image(getClass().getResourceAsStream("/ico_rootw.png"));
        else
          image = new Image(getClass().getResourceAsStream("/ico_rootl.png"));
        button.setGraphic(new ImageView(image));

        button.setOnAction(evt -> ctr.onCmd("root_folder", (bLeft ? "left" : "right"), evt));

        tbRoot = button;
        toolBar.getItems().add(button);

        button = new Button("");
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(CFG.getTextResource().getString("home_folder")));
        image = new Image(getClass().getResourceAsStream("/ico_home.png"));
        button.setGraphic(new ImageView(image));

        button.setOnAction(evt -> ctr.onCmd("home_folder", (bLeft ? "left" : "right"), evt));

        tbHome = button;
        toolBar.getItems().add(button);

        button = new Button("");
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(CFG.getTextResource().getString("refresh").replace("_","")));
        image = new Image(getClass().getResourceAsStream("/ico_refr.png"));
        button.setGraphic(new ImageView(image));

        button.setOnAction(evt->ctr.onCmd("refresh", (bLeft ? "left" : "right"), evt));

        tbRefresh = button;
        toolBar.getItems().add(button);

        button = new Button("");
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(CFG.getTextResource().getString("addpanel")));
        image = new Image(getClass().getResourceAsStream("/addpanel.gif"), 16,16,true,true);
        button.setGraphic(new ImageView(image));

        button.setOnAction(evt->ctr.onCmd("addpanel", (bLeft ? "left" : "right"), evt));

        BorderPane p = new BorderPane();
        p.setCenter(labInfo);
        HBox hb = new HBox();
        hb.setHgrow(p, Priority.ALWAYS);
        labInfo.setAlignment(Pos.CENTER);
        hb.setFillHeight(true);
        hb.setAlignment(Pos.CENTER);
        toolBar.getItems().add(p);
        tbAddPan = button;
        toolBar.getItems().add(button);

        toolBar.setFocusTraversable(false);

        return toolBar;
    }

    private void setColW(TableColumn col, int ix, Map<Integer, Double> mcw){
        Double cw = mcw.get(ix);
        if(cw==null || cw.doubleValue()<0.0)
            return;
        col.setPrefWidth(cw);
    }

    private void setTitle(){
        String path = getPathDef();
        try {
            File f = new File(path);
            String s = f.getCanonicalFile().getName();
            if(s.isEmpty())
              s = f.getCanonicalPath();
            if(s.length()<3)
                s = s+"  ";
            setText(" "+s+" ");
            setTooltip(new Tooltip(f.getCanonicalPath()));
        }catch (IOException e){
            setText(" "+path+" ");
            setTooltip(new Tooltip(path));
        }
    }

    private void setDiskLetter(){
        if(!Os.isWindows())
            return;

        Path p = Paths.get(getPath());
        String rpath = p.getRoot().toString();
        // TODO: network folders

        if(rpath.trim().isEmpty())
            rpath = "?";
        if(rpath.endsWith("\\") ||rpath.endsWith("/"))
            rpath = rpath.substring(0, rpath.length()-1);

        btnDisk.setText(rpath);
    }

    private String getPathDef(){
        String path = this.path;
        if(path==null || path.trim().isEmpty())
            path = ru.pcom.app.Config.get().getDefPanelPath();
        return path;
    }

    public void readFiles(FileData fdata){
        filesRead = true;

        String path = getPathDef();

        fileExplorer = null;
        fxId++;
        if(fxId>=Long.MAX_VALUE)
            fxId = 0L;

        if(mode==0)
            fileExplorer = new FileSysExplorer(this, path, fxId);

        status.setFileData(null);

        synchronized (tableView) {
            tableView.getItems().clear();
            tableView.setEffect(new GaussianBlur(10));
            labInfo.setText(" ");

            if (fdata == null) {
                fdata = new FileData();
                try {
                    fdata.setFile(new File(path));
                } catch (Exception e) { }
            }

            if (fdata.getFile() != null && fdata.getFile().getParentFile() != null) {
                File par = fdata.getFile().getParentFile();
                FileData fd = new FileData(par);
                fd.setName("..");
                fd.getInfo().setParent(true);
                tableView.getItems().add(fd);
                tableView.getSelectionModel().select(0);
            }
        }

        fileExplorer.readFiles();

        setDiskLetter();
    }

    public boolean addFile(FileData fd, long fxId){
        if(fxId!=this.fxId)
            return false;

        synchronized (tableView) {
            // ObservableList v = tableView.getItems();
            tableView.getItems().add(fd); //tableView.setItems(FXCollections.observableArrayList(ff));
        }

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                try {
                    boolean sel = false;
                    if(selFile!=null && selFile.getName().equals(fd.getFile().getName())){
                        sel = true;
                        selFile = null;
                    }
                    if (prevPath != null && prevPath.getName().equals(fd.getFile().getName()) || sel) {
                        synchronized (tableView) {
                            tableView.getSelectionModel().clearSelection(0);
                            tableView.getSelectionModel().select(fd);
                            tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                        }
                    }
                } catch (IndexOutOfBoundsException e) { }
            }
        };
        Platform.runLater(r1);

        return true;
    }

    public void readFinish(long files, long folders, long fsize, String autosz){
        fileExplorer = null;

        wndController.onCmd("panelupdated", "", null);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                synchronized (tableView) {
                    setPanelInfo(files, folders, fsize, autosz);
                    tableView.sort();
                    tableView.refresh();

                    try {
                        if (tableView.getItems().size() > 0) {
                            int ix = tableView.getSelectionModel().getSelectedIndex();
                            tableView.scrollTo(ix >= 0 ? ix : 0);
                        }
                    } catch (IllegalStateException e) { }
                    tableView.setEffect(null);
                }
            }
        });
    }

    public void setPanelInfo(long files, long folders, long fsize, String autosz){
        if(files<1L && folders<1L){
            labInfo.setText(" ");
            return;
        }
        String info = "";
        if(folders>0) {
            info = info + folders + " " + Util.getFolderN(folders);
        }
        if(files>0) {
            if(!info.isEmpty()){
                info = info + ", ";
            }
            info = info + files + " " + Util.getFileN(files) + ", " + autosz;
        }
        labInfo.setText(info);

        if(fsize>0L) {
            NumberFormat nf = NumberFormat.getInstance();
            String tt = "" + nf.format(fsize) + " " + Config.get().getTextResource().getString("val_byte");
            labInfo.setTooltip(new Tooltip(tt));
        }else{
            labInfo.setTooltip(null);
        }
    }

    public boolean multiSelect(){
        ObservableList items = tableView.getSelectionModel().getSelectedItems();
        if(items.size()<2){
            return false;
        }

        long files = 0L, folders = 0L, size = 0L;

        for(Object o : items){
            if(o instanceof FileData){
                FileData fd = (FileData)o;
                if(fd.getInfo()!=null && fd.getInfo().isParent()){
                    continue;
                }
                if(fd.getFile()!=null && fd.getFile().isDirectory()==false){
                    files++;
                    size = size + fd.getFile().length();
                }
                if(fd.getFile()!=null && fd.getFile().isDirectory()){
                    folders++;
                }
            }
        }

        String info = "";
        if(folders>0) {
            info = info + folders + " " + Util.getFolderN(folders);
        }
        if(files>0) {
            if(!info.isEmpty()){
                info = info + ", ";
            }
            info = info + files + " " + Util.getFileN(files) + ", " + Util.getAutoSize(size, Config.get().getUnit());
        }

        String fstatus = Config.get().getTextResource().getString("status_select");
        info = String.format(fstatus, info);
        status.setSelInfo(info, size);
        return true;
    }

    public String getSelectDescr(){
        ObservableList items = tableView.getSelectionModel().getSelectedItems();

        long files = 0L, folders = 0L, size = 0L;

        for(Object o : items){
            if(o instanceof FileData){
                FileData fd = (FileData)o;
                if(fd.getInfo()!=null && fd.getInfo().isParent()){
                    continue;
                }
                if(fd.getFile()!=null && fd.getFile().isDirectory()==false){
                    if(items.size()<2){
                        String s1 = Config.get().getTextResource().getString("file1");
                        return s1+" \""+fd.getName()+"\"";
                    }
                    files++;
                    size = size + fd.getFile().length();
                }
                if(fd.getFile()!=null && fd.getFile().isDirectory()){
                    if(items.size()<2){
                        String s1 = Config.get().getTextResource().getString("folder1a");
                        return s1+" \""+fd.getName()+"\"";
                    }
                    folders++;
                }
            }
        }

        String info = "";
        if(folders>0) {
            info = info + folders + " " + Util.getFolderNa(folders);
        }
        if(files>0) {
            if(!info.isEmpty()){
                info = info + ", ";
            }
            info = info + files + " " + Util.getFileN(files) /* + ", " + Util.getAutoSize(size, Config.get().getUnit()) */;
        }
        return info;
    }

    public void postMsg(String s){ // TODO:
        Platform.runLater(() -> {
            MsgBox.showErrorOk(getClass(), s, null, null);
        });
    }

    private MenuItem[] getDiskMenu(boolean bLeft, MainWndController ctr){
        List<MenuItem> v = new ArrayList<>();
        FileSystem fs = FileSystems.getDefault();
        for(Path r : fs.getRootDirectories()){
            String rpath = r.toFile().getAbsolutePath();

            String txt = rpath;
            if(txt==null)
                txt = "";
            txt = txt.trim();
            if(txt.endsWith("\\") || txt.endsWith("/"))
                txt = txt.substring(0,txt.length()-1);

            try {
                FileStore store = Files.getFileStore(r);
                txt = txt + "    ("+ Util.getAutoSize(store.getUsableSpace())+")";
            }catch (Exception e){}

            MenuItem mi = new MenuItem(txt);
            Image image = new Image(getClass().getResourceAsStream("/ico_disk.png"));
            mi.setGraphic(new ImageView(image));

            mi.setOnAction(evt->ctr.onCmd("ch_disk", (bLeft ? "left" : "right")+":"+rpath, evt));

            v.add(mi);
        }

        return v.toArray(new MenuItem[v.size()]);
    }

    public void updateControls(){

    }

    public void focus(){
        Platform.runLater(() -> {
            tableView.requestFocus();
        });
    }

    public void focus2(){
        tableView.requestFocus();
    }

    public void onChDrive(){
        if(btnDisk!=null)
            btnDisk.fire();
    }

    public void onHistBack(Object param){
        Config CFG = Config.get();
        String msg = CFG.getTextResource().getString("err_open_folder");
        try {
            if (histBack.isEmpty())
                return;
            File f = histBack.pop();

            if (fileExplorer != null)
                fileExplorer.stopRead();

            if (getPath() != null) {
                prevPath = new File(getPath());
                histFwd.push(prevPath);
            }

            setPath(f.getCanonicalPath());
            setTitle();
            setDiskLetter();

            FileData fdata = new FileData(f);
            readFiles(fdata);
        }catch (Exception e){
            MsgBox.showErrorOk(getClass(), msg+": " + e, null, null);
        }
    }

    public void onHistFwd(Object param){
        Config CFG = Config.get();
        String msg = CFG.getTextResource().getString("err_open_folder");
        try {
            if (histFwd.isEmpty())
                return;
            File f = histFwd.pop();

            if (fileExplorer != null)
                fileExplorer.stopRead();

            if (getPath() != null) {
                prevPath = new File(getPath());
                histBack.push(prevPath);
            }

            setPath(f.getCanonicalPath());
            setTitle();
            setDiskLetter();

            FileData fdata = new FileData(f);
            readFiles(fdata);
        }catch (Exception e){
            MsgBox.showErrorOk(getClass(), msg+": " + e, null, null);
        }
    }

    public boolean hasHistBack(){
        return !histBack.isEmpty();
    }

    public boolean hasHistFwd(){
        return !histFwd.isEmpty();
    }

    public boolean isSelectedFileOrFolder(){
        try {
            FileData fdata = null;
            synchronized (tableView) {
                Object o = tableView.getSelectionModel().getSelectedItem();
                if (o instanceof FileData)
                    fdata = (FileData) o;
                else
                    return false;
            }

            if(fdata==null || fdata.getFile()==null)
                return false;

            if("..".equalsIgnoreCase(fdata.getName()))
                return false;

            if(fdata.getInfo()!=null && fdata.getInfo().isParent())
                return false;

            Path file = fdata.getFile().toPath();
            if(Files.isSymbolicLink(file)){ // TODO: Win Junction Point
                /* try {
                    file = Files.readSymbolicLink(file);
                    fdata = new FileData(file.toFile());
                } catch (IOException x) {
                    throw new Exception(CFG.getTextResource().getString("err_open_symlink")+": "+x.getMessage());
                } */
                return true;
            }
            if(fdata.getFile().isDirectory() || fdata.getFile().isFile()) {
                return true;
            }
        }catch (Exception e){}
        return false;
    }

    public void reloadFiles(){
        if (getPath() == null)
            return;

        Config CFG = Config.get();
        String msg = CFG.getTextResource().getString("err_open_folder");
        try {
            File f = new File(getPath());

            if (fileExplorer != null)
                fileExplorer.stopRead();

            setPath(f.getCanonicalPath());
            setTitle();
            setDiskLetter();

            FileData fdata = new FileData(f);
            readFiles(fdata);
        }catch (Exception e){
            MsgBox.showErrorOk(getClass(), msg+": " + e, null, null);
        }
    }

    private void onOpenItem(Config CFG){
      String msg = CFG.getTextResource().getString("err_open_item");
      try {
          FileData fdata = null;
          synchronized (tableView) {
              Object o = tableView.getSelectionModel().getSelectedItem();
              if (o instanceof FileData)
                  fdata = (FileData) o;
              else
                  return; //throw new Exception(CFG.getTextResource().getString("err_incorr_data"));
          }

          if(fdata==null || fdata.getFile()==null)
              throw new Exception(CFG.getTextResource().getString("err_no_data"));

          Path file = fdata.getFile().toPath();
          if(Files.isSymbolicLink(file)){ // TODO: Win Junction Point
              try {
                  file = Files.readSymbolicLink(file);
                  fdata = new FileData(file.toFile());
              } catch (IOException x) {
                  throw new Exception(CFG.getTextResource().getString("err_open_symlink")+": "+x.getMessage());
              }
          }

          if(!fdata.getFile().exists()) {
              if(fdata.getInfo()!=null && fdata.getInfo().isFolder()) {
                  throw new Exception(CFG.getTextResource().getString("err_folder_not_ex")+".");
              }else{
                  throw new Exception(CFG.getTextResource().getString("err_file_not_ex")+".");
              }
          }
          if(fdata.getFile().isDirectory())
              openFolder(fdata, false, CFG);
          else {
              if(fdata.getFile().isFile()) {
                  openFile(fdata, CFG);
              }else {
                  throw new Exception(CFG.getTextResource().getString("err_unknown_fof")+".");
              }
          }
      }catch (Exception e){
          MsgBox.showErrorOk(getClass(), msg+": " + e.getMessage(), null, null);
      }
    }

    public void openFolder(FileData fdata, boolean resetHist, Config CFG) throws Exception{
      if(fileExplorer!=null)
          fileExplorer.stopRead();

      if(getPath()!=null)
        prevPath = new File(getPath());

      setPath(fdata.getFile().getCanonicalPath());
      setTitle();
      setDiskLetter();

      if(fdata.getInfo().isParent() && resetHist==false) {
          if(!histBack.isEmpty())
              histBack.pop();
          if(prevPath!=null)
              histFwd.push(prevPath);
      }else{
          if(prevPath!=null)
            histBack.push(prevPath);
          histFwd.clear();
      }

      readFiles(fdata);
    }

    private void openFile(FileData fdata, Config CFG) throws Exception{
        final File file = fdata.getFile();
        final Class cls = getClass();
        final String errdns = CFG.getTextResource().getString("err_desktop_notsupp");
        final String err = CFG.getTextResource().getString("err_open_file");
        // final HostServices hsvc = CFG.getApplication().getHostServices();
        try {
            if( Desktop.isDesktopSupported() ) {
                new Thread(() -> {
                    try{
                        Desktop.getDesktop().open(file);
                        // hsvc.showDocument(file.toURI().toURL().toExternalForm());
                    }catch (Exception exception){
                        MsgBox.showErrorOk(cls, err+" \""+file.getAbsolutePath()+ "\": "+exception.getMessage()+".", null, null);
                    }
                }).start();
            }else{
                throw new Exception(errdns);
            }
        }catch (Exception exception){
            MsgBox.showErrorOk(cls, err+" \""+file.getAbsolutePath()+ "\": "+exception.getMessage()+".", null, null);
        }
    }

    public Object getSelectedItem(){
        return tableView.getSelectionModel().getSelectedItem();
    }

    public java.util.List getSelectedItems() {
        return tableView.getSelectionModel().getSelectedItems();
    }

    public String getColW(){ // col widths
        String s = "";
        for(int i = 0; i<tableView.getColumns().size();i++) {
            TableColumn column = (TableColumn)tableView.getColumns().get(i);
            double w = column.getWidth();
            if(!s.isEmpty())
                s = s +",";
            s = s + w;
        }
        return s;
    }

    public boolean isFilesRead() {
        return filesRead;
    }

    public void setFilesRead(boolean filesRead) {
        if(true)
            throw new RuntimeException("Not allowed");
        this.filesRead = filesRead;
    }

}
