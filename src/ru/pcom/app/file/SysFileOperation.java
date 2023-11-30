package ru.pcom.app.file;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ru.pcom.app.Config;
import ru.pcom.app.dlgoper.DlgOperation;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.mainwnd.Controller;
import ru.pcom.app.sys.Os;
import ru.pcom.app.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SysFileOperation implements IFileOperation {
    private boolean bCopy = false, bDelete = false, bDeletePost = false;
    private boolean bSkipAllMkDirErr = false, bSkipAllCopyErr = false, bSkipAllDelErr = false, bSkipCopyExists = false,
                    bRewriteAll = false, bSkipRO = false, bForceRO = false;
    private String trgFolder, trgNameExt, trgName, trgExt, srcFolder;
    private Path srcPath, trgPath;
    private Config CFG;
    private DlgOperation dlgOp;
    private int total = 0, cnt = 0;

    public SysFileOperation(Config cfg, boolean bCopy, boolean bDelete, boolean bDeletePost) {
        CFG = cfg;
        this.bCopy = bCopy;
        this.bDelete = bDelete;
        this.bDeletePost = bDeletePost;
    }

    public void setSourceTarget(String s, String t) throws Exception{
        setSource(s);
        setTarget(t);
    }
    public void setTarget(String t) throws Exception{
        t = transformRel(t);
        t = t.trim();
        t = fixWinRoot(t);
        int pt = t.lastIndexOf("\\");
        int pt1 = t.lastIndexOf("/");
        if((pt<0 && pt1>=0) || (pt>=0 && pt1>pt)){
            pt = pt1;
        }
        boolean dot2 = false;
        if(pt<0){
            pt = t.lastIndexOf(":");
            if(pt>0){
                dot2 = true;
            }
        }

        String path1 = "", path2 = "";
        if(dot2){
            path1 = t.substring(0, pt+1);
            path2 = t.substring(pt+1);
        }else {
            if (pt >= 0) {
                path1 = t.substring(0, pt);
                path2 = t.substring(pt + 1);
            } else {
                if (t.contains("?") || t.contains("*")) {
                    path1 = "";
                    path2 = t;
                } else {
                    path2 = "";
                    path1 = t;
                }
            }
        }

        boolean bSlash1 = false;
        if(t.startsWith("\\") || t.startsWith("/")){
            bSlash1 = true;
        }

        if(!path1.isEmpty()){
            if(Os.isWindows() && path1.length()==2 && path1.endsWith(":")){
                path1 = path1 + File.separator;
            }

            File f = null;
            if(path1.contains("\\") || path1.contains("/") || path1.contains(":")) {
                f = new File(path1);
                if (!f.exists()) {
                    throw new FileNotFoundException(CFG.getTextResource().getString("err_folder_not_ex"));
                }
            }
            if(!path2.isEmpty() && path2.contains("?")==false && path2.contains("*")==false){
                File f2 = (f==null ? new File(path2) : new File(f, path2));
                if(f2.exists() && f2.isDirectory()){
                    path1 = f2.getAbsolutePath();
                    path2 = "";
                }
            }
        }

        trgFolder = path1;
        trgNameExt = path2;
        File f = new File(trgFolder);
        if(f.exists() && f.isDirectory()) {
            trgFolder = f.getCanonicalPath();
        }
        if(f.exists()==false && f.getParentFile()!=null && f.getParentFile().exists() && f.getParentFile().isDirectory()){
            trgFolder = f.getParentFile().getCanonicalPath();
            trgNameExt = f.getName();
        }

        if(trgFolder.isEmpty()==false && trgNameExt.isEmpty() &&
           trgFolder.contains(":")==false && trgFolder.contains("/")==false && trgFolder.contains("\\")==false){
            f = new File(trgFolder);
            if(f.isDirectory()==false){
                trgNameExt = trgFolder;
                trgFolder = "";
            }
        }

        if(Os.isUnix() && trgFolder.isEmpty() && bSlash1){
            trgFolder = "/";
        }

        if(trgFolder.isEmpty()){
            trgFolder = srcFolder;
        }

        setTarget2(trgNameExt);

        System.out.println("> "+trgFolder);
        System.out.println("> "+trgNameExt);
        System.out.println("> "+trgName);
        System.out.println("> "+trgExt);
    }

    private void setTarget2(String nx){
        if(nx.contains(".")==false){
            trgName = trgNameExt;
            trgExt = "";
            return;
        }

        int pt = nx.lastIndexOf(".");
        trgName = nx.substring(0, pt);
        trgExt = nx.substring(pt+1);
    }

    public void setSource(String s){
        srcFolder = s;
    }

    private String fixWinRoot(String s){
        if(Os.isWindows()==false){
            return s;
        }
        if((s.startsWith("\\") && s.startsWith("\\\\")==false) || s.startsWith("/")){
            if(srcFolder.contains(":")){
               int p = srcFolder.indexOf(":");
               if(p>0){
                   return srcFolder.substring(0,p+1)+s;
               }
            }
        }
        return s;
    }

    private String transformRel(String t){
        if(t==null){
            return t;
        }
        t = t.trim();
        if(t.startsWith("/") || t.startsWith("\\") || t.contains(":")){
            return t;
        }
        File cd = new File(srcFolder);
        File f = new File(cd, t);
        if(f.exists() && f.isDirectory()){
            try {
                return f.getCanonicalPath();
            }catch (Exception e){}
        }
        if(t.contains("/") || t.contains("\\")) {
            File p = f.getParentFile();
            if (p != null && p.exists() && p.isDirectory()) {
                try{
                    return f.getCanonicalPath();
                }catch (Exception e){}
            }
        }
        return t;
    }

    public void run(String rsid, java.util.List files, Class clazz, Controller ctr, DlgOperation dlgOp) throws Exception{
        this.dlgOp = dlgOp;

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{
                    process(files, ctr);
                } catch (IllegalArgumentException iae){
                    msgBox(iae,rsid, ctr);
                    return null;
                } catch (FileAlreadyExistsException fex) {
                    Exception e = new Exception(CFG.getTextResource().getString("file_exists")+": "+fex.getMessage());
                    msgBox(e,rsid, ctr);
                    return null;
                } catch (AccessDeniedException adx) {
                    Exception e = new Exception(CFG.getTextResource().getString("Access denied")+": "+adx.getMessage());
                    msgBox(e,rsid, ctr);
                    return null;
                } catch (Exception e) {
                    msgBox(e,rsid, ctr);
                    return null;
                }

                Platform.runLater(() -> {dlgOp.close(); ctr.onCmd("endoper", "", null);});
                return null;
            }
        };
        new Thread(task).start();
    }

    public void process(java.util.List files, Controller ctr) throws Exception{
        List<File> vfiles = new ArrayList<>();
        for(Object o : files){
            FileData fd = (FileData)o;
            if(fd.getInfo()!=null && fd.getInfo().isParent()){
                continue;
            }
            if(fd.getFile().isFile()){
                vfiles.add(fd.getFile());
            }
            if(fd.getFile().isDirectory()){
                process(fd.getFile(), vfiles);
            }
            // TODO: not file not dir ???
        }

        total = vfiles.size();

        if(bCopy) {
            File sp = new File(srcFolder);
            srcPath = sp.toPath();
            sp = new File(trgFolder);
            trgPath = sp.toPath();
        }

        if(dlgOp.isCancelled()){
            Platform.runLater(() -> ctr.onCmd("endoper", "", null));
            return;
        }

        try {
            for (Object o : vfiles) {
                if (o instanceof Path) {
                    if (bCopy) {
                        copy((Path) o);
                    }
                    if (bDelete && bDeletePost == false) {
                        delete((Path) o);
                    }
                }
                if (o instanceof File) {
                    if (bCopy) {
                        copy((File) o);
                    }
                    if (bDelete && bDeletePost == false) {
                        delete((File) o);
                    }
                }
                cnt++;

                if (dlgOp.isCancelled()) {
                    Platform.runLater(() -> ctr.onCmd("endoper", "", null));
                    return;
                }
            }

            if (bDelete && bDeletePost) {
                for (Object o : vfiles) {
                    if (dlgOp.isCancelled()) {
                        Platform.runLater(() -> ctr.onCmd("endoper", "", null));
                        return;
                    }

                    if (o instanceof Path) {
                        delete((Path) o);
                    }
                    if (o instanceof File) {
                        delete((File) o);
                    }
                    cnt++;
                }
            }
        }catch (AbortOpException abox){
            Platform.runLater(() -> ctr.onCmd("endoper", "", null));
            return;
        }

        // TODO: on finish
    }

    private void process(File dir, java.util.List files) throws Exception{
        final Path source = dir.toPath();
        // final Path target = ...
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if(dlgOp.isCancelled()){
                        return FileVisitResult.TERMINATE;
                    }
                    files.add(dir);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(dlgOp.isCancelled()){
                        return FileVisitResult.TERMINATE;
                    }
                    files.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });
    }

    private void copy(File f) throws Exception{
        copy(f.toPath());
    }

    private void copy(Path file) throws Exception{
        if(Files.isDirectory(file)){
            mkdir(getTargetPath(file));
            return;
        }
        Path tp = getTargetPath(file);
        System.out.println("COPY : "+file.toFile().getAbsolutePath() +" >> "+tp.toFile().getAbsolutePath());
        updateDlgOp(file.toFile(), tp.toFile());
        Path par = tp.getParent();
        if(par!=null && !Files.exists(par)){
            mkdir(par);
        }
        copy(file, tp);
    }

    private void copy(Path src, Path trg) throws Exception{
        boolean bRewrite = false, bRewriteRO = false;
        if(bRewriteAll){
            bRewrite = true;
        }
        if(bForceRO){
            bRewriteRO = true;
        }

        do {
            try {
                if(FileUtil.isRO(trg)){
                    if(bRewriteRO){
                        FileUtil.cleanRO(trg);
                    }else {
                        throw new ROException(trg.toString());
                    }
                }
                if(bRewrite){
                    Files.copy(src, trg, StandardCopyOption.REPLACE_EXISTING);
                }else {
                    Files.copy(src, trg);
                }
                break;
            } catch (FileAlreadyExistsException fex){
                if(bSkipCopyExists){
                    return;
                }
                try {
                    Semaphore sem = new Semaphore(1);
                    sem.acquire();
                    Platform.runLater(() -> dlgOp.showRewrite(CFG.getTextResource().getString("file_replace_confirm"), trg.toFile(), src.toFile(), sem));
                    sem.acquire();
                    sem.release();
                }catch (InterruptedException iex){
                    throw new RuntimeException(iex.getMessage());
                }
                int r = dlgOp.getRewriteResult();
                if(r<=0){
                    throw new AbortOpException();
                }
                if(r==2){
                    break;
                }
                if(r==3) {
                    bSkipCopyExists = true;
                    break;
                }
                if(r==4){
                    bRewriteAll = true;
                }
                if(r==1){
                    bRewrite = true;
                }
            } catch (ROException rox){
                if(bSkipRO){
                    return;
                }
                try {
                    Semaphore sem = new Semaphore(1);
                    sem.acquire();
                    Platform.runLater(() -> dlgOp.showWarn(CFG.getTextResource().getString("file_replace_confirm"),
                           trg.toFile(), "file1", CFG.getTextResource().getString("file_replace_ro_confirm"),
                           sem));
                    sem.acquire();
                    sem.release();
                }catch (InterruptedException iex){
                    throw new RuntimeException(iex.getMessage());
                }
                int r = dlgOp.getWarnResult();
                if(r<=0){
                    throw new AbortOpException();
                }
                if(r==2){
                    break;
                }
                if(r==3) {
                    bSkipRO = true;
                    break;
                }
                if(r==4){
                    bForceRO = true;
                }
                if(r==1){
                    bRewriteRO = true;
                }
            } catch (Exception e) {
                if(bSkipAllCopyErr){
                    return;
                }
                try {
                    Semaphore sem = new Semaphore(1);
                    sem.acquire();
                    Platform.runLater(() -> dlgOp.showError(CFG.getTextResource().getString("err_copy_file"), src.toFile(), "file1", e.getMessage() + getExceptionDescr(e)+".", sem));
                    sem.acquire();
                    sem.release();
                }catch (InterruptedException iex){
                    throw new RuntimeException(iex.getMessage());
                }
                int r = dlgOp.getErrResult();
                if(r<=0){
                    throw new AbortOpException();
                }
                if(r==2){
                    break;
                }
                if(r==3) {
                    bSkipAllCopyErr = true;
                    break;
                }
            }
        }while (true);
    }

    private void mkdir(Path dir){
        do {
            try {
                Path p = getTargetPath(dir);
                File f = p.toFile();
                Files.createDirectories(f.getCanonicalFile().toPath());
                break;
            } catch (Exception e) {
                if(bSkipAllMkDirErr){
                    return;
                }
                try {
                    Semaphore sem = new Semaphore(1);
                    sem.acquire();
                    Platform.runLater(() -> dlgOp.showError(CFG.getTextResource().getString("err_mk_dir"), dir.toFile(), "folder", e.getMessage() + getExceptionDescr(e), sem));
                    sem.acquire();
                    sem.release();
                }catch (InterruptedException iex){
                    throw new RuntimeException(iex.getMessage());
                }
                int r = dlgOp.getErrResult();
                if(r<=0){
                    throw new AbortOpException();
                }
                if(r==2){
                    break;
                }
                if(r==3) {
                    bSkipAllMkDirErr = true;
                    break;
                }
            }
        }while (true);
    }

    private void delete(File f) throws Exception{
        delete(f.toPath());
    }

    private void delete(Path file) throws Exception{
        System.out.println("DEL : "+file.getFileName());
        if(bDelete){
            updateDlgOp(file.toFile(), null);
        }
        delete2(file);
    }

    private void delete2(Path file) throws Exception{
        do {
            try {
                Files.delete(file);
                break;
            } catch (Exception e) {
                if(bSkipAllDelErr){
                    return;
                }
                try {
                    Semaphore sem = new Semaphore(1);
                    sem.acquire();
                    Platform.runLater(() -> dlgOp.showError(CFG.getTextResource().getString("err_del"), file.toFile(), "file1", e.getMessage() + getExceptionDescr(e), sem));
                    sem.acquire();
                    sem.release();
                }catch (InterruptedException iex){
                    throw new RuntimeException(iex.getMessage());
                }
                int r = dlgOp.getErrResult();
                if(r<=0){
                    throw new AbortOpException();
                }
                if(r==2){
                    break;
                }
                if(r==3) {
                    bSkipAllDelErr = true;
                    break;
                }
            }
        }while (true);
    }

    private Path getTargetPath(Path src){
        Path p2 = src;
        try {
            Path p1 = srcPath.relativize(src);
            p2 = trgPath.resolve(p1);
        }catch (IllegalArgumentException ix){
            p2 = src;
        }
        return applyMask(p2);
    }

    private Path applyMask(Path src){
        if(trgNameExt==null || trgNameExt.trim().isEmpty()){
            return src;
        }
        trgNameExt = trgNameExt.trim();
        if(trgNameExt.contains("*") || trgNameExt.contains("?")) {
            File f = src.toFile();
            File f1 = new File(f.getParentFile(), applyMask(f.getName(), trgNameExt));
            return f1.toPath();
        }else{
            File f = src.toFile();
            File f1 = new File(f.getParentFile(), trgNameExt);
            return f1.toPath();
        }
    }

    private String applyMask(String fname, String mask){
        if(mask.contains(".")){
            int pos = fname.lastIndexOf(".");
            if(pos<0){
                return applyMask(fname, trgName);
            }
            String fname1 = fname.substring(0,pos);
            String ext = fname.substring(pos+1);
            return applyMask(fname1, trgName)+"."+applyMask(ext, trgExt);
        }
        if("*".equals(mask)){
            return fname;
        }
        if(mask.startsWith("*")){
            return fname+mask.substring(1);
        }
        int l1 = fname.length();
        int l2 = mask.length();
        String ret = "";
        for(int i=0; (i<l1 || i<l2); i++){
            char cn = '\0', cm = '\0', c = '\0';
            if(i<l1){
                cn = fname.charAt(i);
            }
            if(i<l2){
                cm = mask.charAt(i);
            }
            if('?'==cm){
                c = cn;
            }else if('*'==cm){
                ret = ret + fname.substring(i);
                break;
            }else{
                c = cm;
            }
            if(c!='\0') {
                ret = ret + c;
            }
        }
        return ret;
    }

    private void updateDlgOp(File fs, File ft){
        double total2 = total;
        if(bCopy && bDelete && bDeletePost){
            total2 = total2*2;
        }
        double x = 0, prc = 0;
        if(cnt>0) {
            x = total2 / (double) cnt;
        }else{
            x = 100;
        }
        if(x>0) {
            prc = 100 / x;
        }else{
            prc = 100;
        }
        final int p = (int)prc;
        Platform.runLater(() -> dlgOp.setStatus(fs, ft, p, total, cnt+1));
    }

    private String getExceptionDescr(Exception e){
        if(e instanceof AccessDeniedException){
            return ". "+CFG.getTextResource().getString("Access denied");
        }
        if(e instanceof FileAlreadyExistsException){
            return ". "+CFG.getTextResource().getString("folder_exists");
        }
        return "";
    }

    private void warnBox(Exception e, String rsid, Controller ctr) {
        Platform.runLater(() -> {
            MsgBox.showWarnOk(dlgOp.getClass(), e.getMessage() + ".", CFG.getTextResource().getString(rsid), null); // TODO:
            dlgOp.close();
            ctr.onCmd("endoper", "", null);
        });
    }

    private void msgBox(Exception e, String rsid, Controller ctr) {
        Platform.runLater(() -> {
            MsgBox.showErrorOk(dlgOp.getClass(), e.getMessage() + ".", CFG.getTextResource().getString(rsid), null); // TODO:
            dlgOp.close();
            ctr.onCmd("endoper", "", null);
        });
    }

}

class AbortOpException extends RuntimeException {
    public AbortOpException(){
        super();
    }
}

class ROException extends AccessDeniedException {
    public ROException(String file){
        super(file);
    }
}