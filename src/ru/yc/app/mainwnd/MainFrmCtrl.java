package ru.yc.app.mainwnd;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.yc.app.Config;
import ru.yc.app.dlgabout.DlgAbout;
import ru.yc.app.dlgfreplace.DlgFileReplace;
import ru.yc.app.dlgname.DlgCopyTarget;
import ru.yc.app.dlgname.DlgInputName;
import ru.yc.app.dlgoper.DlgDeleteOperation;
import ru.yc.app.dlgoper.DlgOperation;
import ru.yc.app.dlgopts.DlgOptions;
import ru.yc.app.file.FileData;
import ru.yc.app.file.IFileOperation;
import ru.yc.app.file.SysFileOperation;
import ru.yc.app.gui.MsgBox;
import ru.yc.app.sys.Environ;
import ru.yc.app.sys.Os;
import ru.yc.app.util.CfgUtil;
import ru.yc.app.util.FileUtil;
import ru.yc.app.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class MainFrmCtrl extends MainFrmMenuToolBars {

    protected Config CFG;
    protected List<FilePanel> filePanels = new ArrayList<>();
    protected TabPane tab1, tab2;
    protected SplitPane split;
    protected String lastFocus = "left";

    public MainFrmCtrl(){
    }

    public FilePanel getActivePanel(TabPane tab){
        FilePanel fp = null;
        if(tab!=null && tab.getTabs().isEmpty()==false) {
            Tab t = tab.getSelectionModel().getSelectedItem();
            if (t != null && t instanceof FilePanel) {
                fp = (FilePanel) t;
            }
        }
        return fp;
    }

    public void addPanel(Object param){
        TabPane tab = tab1;
        boolean bLeft = true;
        if("right".equals(param)) {
            tab = tab2;
            bLeft = false;
        }

        String path = "/";
        Tab t = tab.getSelectionModel().getSelectedItem();
        if(t!=null && t instanceof  FilePanel){
            FilePanel fp = (FilePanel)t;
            path = fp.getPath();
        }

        FilePanel fp = new FilePanel();
        fp.setPath(path);
        fp.onCreate(bLeft, ctr, CFG, null);
        tab.getTabs().add(fp);
        tab.getSelectionModel().select(fp);
        filePanels.add(fp);

        fp.readFiles(null);
    }

    public void openRootFolder(Object param) {
        TabPane tab = tab1;
        if("right".equals(param))
            tab = tab2;

        String path = "/";
        FilePanel fp = null;
        Tab t = tab.getSelectionModel().getSelectedItem();
        if(t!=null && t instanceof  FilePanel){
            try {
                fp = (FilePanel) t;
                Path p = Paths.get(fp.getPath());
                path = p.getRoot().toAbsolutePath().toString();
            }catch (Exception e){ }
        }

        if(fp!=null) {
            FileData fdata = new FileData();
            try {
                fdata.setFile(new File(path));
                fdata.getInfo().setParent(true);
                fp.openFolder(fdata, true, CFG);
            }catch (Exception e){
                MsgBox.showErrorOk(getClass(), fdata.getFile().getAbsolutePath(), null, null);
            }
        }
    }

    public void openHomeFolder(Object param) {
        if(param!=null)
            onChFocus(param);

        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();

        String path = Environ.getHomeFolder();
        try {
            if(path.isEmpty())
                throw new Exception(CFG.getTextResource().getString("err_home_folder_no"));
            File f = new File(path);
            if(!f.exists())
                throw new Exception(CFG.getTextResource().getString("err_home_folder_not_ex"));
            if(!f.isDirectory())
                throw new Exception(CFG.getTextResource().getString("err_home_folder_not_dir"));
        }catch (Exception e){
            String s = CFG.getTextResource().getString("err_home_folder");
            MsgBox.showErrorOk(getClass(), s + ": "+e.getMessage(), null, null);
            return;
        }

        FilePanel fp = getActivePanel(tab);
        if(fp!=null) {
            FileData fdata = new FileData();
            try {
                fdata.setFile(new File(path));
                fdata.getInfo().setParent(true);
                fp.openFolder(fdata, true, CFG);
            }catch (Exception e){
                MsgBox.showErrorOk(getClass(), fdata.getFile().getAbsolutePath(), null, null);
            }
        }
    }

    public void chDisk(Object param) {
        TabPane tab = tab1;

        String par = (String)param;
        if(par.startsWith("right:")) {
            tab = tab2;
            par = par.substring(6);
        }else if(par.startsWith("left:")) {
            par = par.substring(5);
        }

        FilePanel fp = getActivePanel(tab);
        if(fp!=null) {
            FileData fdata = new FileData();
            try {
                fdata.setFile(new File(par));
                fdata.getInfo().setParent(true);
                fp.openFolder(fdata, true, CFG);
            }catch (Exception e){
                MsgBox.showErrorOk(getClass(), fdata.getFile().getAbsolutePath(), null, null);
            }
        }
    }

    public void chDiskMenu(Object param) {
        TabPane tab = tab1;

        String par = (String)param;
        if(par.startsWith("right:")) {
            tab = tab2;
        }

        FilePanel fp = getActivePanel(tab);
        if(fp!=null) {
            fp.onChDrive();
        }
        tab.requestFocus();
    }

    public void onMkDir(Object param) {
        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();
        FilePanel fp = getActivePanel(tab);

        String def = "";
        for(;;) {
            DlgInputName dialog = new DlgInputName(getPrimStage(), CFG.getTextResource().getString("New Folder"));
            dialog.setContentText(CFG.getTextResource().getString("Folder name") + ":");
            dialog.setValue(def);

            /* TextInputDialog dialog = new TextInputDialog(def); // XXX:
            dialog.setGraphic(null);
            dialog.setTitle(CFG.getTextResource().getString("New Folder"));
            dialog.setHeaderText(null);
            dialog.setContentText(CFG.getTextResource().getString("Folder name") + ":");
            String css = CfgUtil.loadCSS(getClass(),"/app.css");
            if(css!=null)
                dialog.getDialogPane().getStylesheets().add(css); */

            dialog.showAndWait();
            if (dialog.isResult()) {
                try {
                    def = dialog.getValue().trim();
                    if (def.isEmpty())
                        throw new IllegalArgumentException(CFG.getTextResource().getString("not_specified"));
                    try {
                        Util.checkFileName(def);
                    }catch (Exception e1){
                        throw new Exception(CFG.getTextResource().getString("folder_invalid_sym"));
                    }

                    File p = new File(fp.getPath());
                    File f = new File(p, def);
                    if (f.exists())
                        throw new Exception(CFG.getTextResource().getString("folder_exists"));
                    if (!f.mkdirs())
                        throw new Exception(CFG.getTextResource().getString("folder_invalid"));
                    fp.reloadFiles();
                    break;
                } catch (IllegalArgumentException iae){
                    MsgBox.showWarnOk(getClass(), iae.getMessage() + ".", CFG.getTextResource().getString("err_mk_dir"), null);
                } catch (Exception e) {
                    MsgBox.showErrorOk(getClass(), e.getMessage() + ".", CFG.getTextResource().getString("err_mk_dir"), null);
                }
            }else{
                break;
            }
        } // for

        Platform.runLater(() -> fp.focus());
    }

    public void onRename(Object param) {
        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();
        FilePanel fp = getActivePanel(tab);

        FileData fileData = (FileData)fp.getSelectedItem();
        if(fileData==null || fileData.getFile()==null || "..".equals(fileData.getName()))
            return;
        if(fileData.getInfo()!=null && fileData.getInfo().isParent()) {
            tab.requestFocus();
            fp.focus();
            return;
        }

        String def = fileData.getFile().getName();
        String def1 = def;
        String prompt = CFG.getTextResource().getString("QuickRenamePrompt") + ":";
        for(;;) {
            DlgInputName dialog = new DlgInputName(getPrimStage(), CFG.getTextResource().getString("QuickRename"));
            dialog.setContentText(String.format(prompt, def1));
            dialog.setValue(def);

            /* TextInputDialog dialog = new TextInputDialog(def); // XXX:
            dialog.setGraphic(null);
            dialog.setTitle(CFG.getTextResource().getString("QuickRename"));
            dialog.setHeaderText(null);
            String prompt = CFG.getTextResource().getString("QuickRenamePrompt") + ":";
            dialog.setContentText(String.format(prompt, def));
            String css = CfgUtil.loadCSS(getClass(),"/app.css");
            if(css!=null)
                dialog.getDialogPane().getStylesheets().add(css); */

            dialog.showAndWait();
            if (dialog.isResult()) {
                try {
                    def = dialog.getValue().trim(); // TODO: use [*.*]
                    if (def.isEmpty())
                        throw new IllegalArgumentException(CFG.getTextResource().getString("not_specified"));
                    try {
                        Util.checkFileName(def);
                    }catch (Exception e1){
                        throw new Exception(CFG.getTextResource().getString("name_invalid_sym"));
                    }

                    File p = fileData.getFile();
                    File f = new File(p.getParentFile(), def);
                    if (f.exists()) {
                        if(f.isDirectory())
                            throw new Exception(CFG.getTextResource().getString("folder_exists"));
                        int rpl = onFileReplace(f, p);
                        if(rpl<0)
                            break;
                        if(rpl==0)
                            continue;
                        if(!FileUtil.deleteFile(f))
                            throw new Exception(CFG.getTextResource().getString("unable_delete_ren"));
                    }
                    if (!p.renameTo(f))
                        throw new Exception(CFG.getTextResource().getString("folder_invalid"));
                    fp.setSelFile(f);
                    fp.reloadFiles();
                    break;
                } catch (IllegalArgumentException iae){
                    MsgBox.showWarnOk(getClass(), iae.getMessage() + ".", CFG.getTextResource().getString("err_rename"), null);
                } catch (Exception e) {
                    MsgBox.showErrorOk(getClass(), e.getMessage() + ".", CFG.getTextResource().getString("err_rename"), null);
                }
            }else{
                break;
            }
        } // for

        Platform.runLater(() -> fp.focus());
    }

    public void onCopyMove(String cmd, Object param) {
        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();
        FilePanel fp = getActivePanel(tab);

        List selFiles = fp.getSelectedItems();
        if(selFiles==null || selFiles.isEmpty()){
            return;
        }
        if(selFiles.size()==1){
            FileData fd = (FileData)selFiles.get(0);
            if(fd.getInfo()!=null && fd.getInfo().isParent()){
                return;
            }
        }

        String def = "";

        FilePanel fp2 = null;
        if("left".equalsIgnoreCase(lastFocus)){
            fp2 = getActivePanel(tab2);
        }else {
            fp2 = getActivePanel(tab1);
        }
        if(fp2!=null){
            def = fp2.getPath();
            if(Os.isWindows()) {
                def = def + "\\";
                def = def.replace("\\\\", "\\");
            }else{
                if(!def.endsWith("/")) {
                    def = def + "/";
                }
            }
            def = def + "*.*";
        }

        boolean bMove = false;
        String op = "copy";
        if("fmove".equalsIgnoreCase(cmd)){
            bMove = true;
            op = "move";
        }
        IFileOperation fop = new SysFileOperation(CFG,true, bMove, false);

        boolean breakOnErr = false;
        for(;;) {
            DlgCopyTarget dialog = new DlgCopyTarget(getPrimStage(), CFG.getTextResource().getString(cmd));
            dialog.setContentText(String.format(CFG.getTextResource().getString(cmd+2), fp.getSelectDescr()) + ":");
            dialog.setValue(def);

            dialog.showAndWait();
            if (dialog.isResult()) {
                try {
                    def = dialog.getValue().trim();
                    if (def.isEmpty())
                        throw new IllegalArgumentException(CFG.getTextResource().getString("not_specified"));
                    try {
                        // TODO: Util.checkFileName(def);
                        fop.setSourceTarget(fp.getPath(), def);
                    }catch (FileNotFoundException fex){
                        throw new Exception(CFG.getTextResource().getString("err_invalid_path"));
                    }

                    DlgOperation dlgOp = new DlgOperation(getPrimStage(), "", cmd);
                    dlgOp.show();
                    breakOnErr = true;
                    fop.run("err_"+op+"_file", selFiles, getClass(), ctr, dlgOp);
                    break;
                } catch (IllegalArgumentException iae){
                    MsgBox.showWarnOk(getClass(), iae.getMessage() + ".", CFG.getTextResource().getString("err_"+op+"_file"), null); // TODO:
                    if(breakOnErr){
                        break;
                    }
                } catch (Exception e) {
                    MsgBox.showErrorOk(getClass(), e.getMessage() + ".", CFG.getTextResource().getString("err_"+op+"_file"), null); // TODO:
                    if(breakOnErr){
                        break;
                    }
                }
            }else{
                break;
            }
        } // for

        Platform.runLater(() -> fp.focus());
    }

    public void onDelete(Object param) {
        if(tbDel.isDisabled()){
            return;
        }

        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();
        FilePanel fp = getActivePanel(tab);

        List selFiles = fp.getSelectedItems();
        if(selFiles==null || selFiles.isEmpty()){
            return;
        }
        if(selFiles.size()==1){
            FileData fd = (FileData)selFiles.get(0);
            if(fd.getInfo()!=null && fd.getInfo().isParent()){
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        MsgBox.setYN(alert, CFG);
        alert.setTitle(CFG.getTextResource().getString("Confirm Delete"));
        alert.setHeaderText(null);
        alert.setContentText(String.format(CFG.getTextResource().getString("delete_prompt"), fp.getSelectDescr()));
        alert.initOwner(this.getPrimStage().getOwner());

        DialogPane dialogPane = alert.getDialogPane();
        String css = CfgUtil.loadCSS(getClass(),"/app.css");
        if(css!=null)
            dialogPane.getStylesheets().add(css);
        // dialogPane.getStyleClass().add("myDialog");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/app.png")));

        dialogPane.requestFocus();
        Optional<ButtonType> res = alert.showAndWait();

        if(res.isPresent()){
            ButtonType btt = res.get();
            if(MsgBox.equalCode(btt, ButtonType.YES)==false){
                return;
            }
        }else {
            return;
        }

        DlgOperation dlgOp = new DlgDeleteOperation(getPrimStage(), "", "fdelete");
        dlgOp.show();

        IFileOperation fop = new SysFileOperation(CFG,false, true, false);
        try{
            fop.run("err_del", selFiles, getClass(), ctr, dlgOp);
        } catch (Exception e) {
            MsgBox.showErrorOk(getClass(), e.getMessage() + ".", CFG.getTextResource().getString("err_del"), null); // TODO:
        }

        Platform.runLater(() -> fp.focus());
    }

    public void onEndOper(){
        TabPane taba = getActiveTabPane();
        for(int i=0;i<2;i++) {
            TabPane tab = tab1;
            if(i>0){
                tab = tab2;
            }
            if (tab == null)
                continue;
            // tab.requestFocus();
            FilePanel fp = getActivePanel(tab);
            fp.reloadFiles();
        }
        taba.requestFocus();
        FilePanel fp = getActivePanel(taba);
        fp.focus2();
    }

    public void onSplitPos(String cmd, Object side){
        if(side!=null)
            onChFocus(side);
        if(side==null)
            side = lastFocus;

        double p = 0.5;
        if("5050".equalsIgnoreCase(cmd)){
            p = 0.5;
        }else{
            if("left".equalsIgnoreCase(side.toString())){
                p = 1.0;
            }else{
                p = 0.0;
            }
        }
        split.setDividerPositions(p);
    }

    public void onMMenu(){ // TODO: not work
        KeyEvent ke = new KeyEvent(this.primStage, this.primStage, KeyEvent.KEY_PRESSED, "", "", KeyCode.F10, false, false, false, false);
        Platform.runLater(() -> this.primStage.fireEvent(ke));
    }

    public int onFileReplace(File fs, File ft) {
        DlgFileReplace dialog = new DlgFileReplace(getPrimStage(), CFG.getTextResource().getString("file_replace_confirm"));
        dialog.setFiles(fs, ft);
        dialog.showAndWait();
        return dialog.getResult();
    }

    public  void onAbout(Object param){
        DlgAbout dlg = new DlgAbout(getPrimStage());
        dlg.showAndWait();
    }

    public void onHistBack(Object param){
        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;

        FilePanel fp = null;
        Tab t = tab.getSelectionModel().getSelectedItem();
        if(t!=null && t instanceof FilePanel){
            fp = (FilePanel) t;
            fp.onHistBack(param);
        }
    }

    public void onHistFwd(Object param){
        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;

        FilePanel fp = null;
        Tab t = tab.getSelectionModel().getSelectedItem();
        if(t!=null && t instanceof FilePanel){
            fp = (FilePanel) t;
            fp.onHistFwd(param);
        }
    }

    public void onRefresh(Object param){
        if(param!=null)
            onChFocus(param);

        TabPane tab = getActiveTabPane();
        if(tab==null)
            return;
        tab.requestFocus();
        FilePanel fp = getActivePanel(tab);
        fp.reloadFiles();
    }

    public  void onOptions(Object param){
        DlgOptions dlg = new DlgOptions(getPrimStage());
        dlg.showAndWait();
        if(dlg.isResult()) {
            CfgUtil.saveCfg(getClass());
            CfgUtil.saveCfgCss(getClass());
        }
        onRefresh("left");
        onRefresh("right");
    }

    public void onChFocus(Object param){
      lastFocus = ""+param;
    }

    public TabPane getActiveTabPane(){
        TabPane tab = null;
        if("left".equalsIgnoreCase(lastFocus))
            tab = tab1;
        if("right".equalsIgnoreCase(lastFocus))
            tab = tab2;
        return tab;
    }

}
