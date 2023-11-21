package ru.pcom.app.file;

import ru.pcom.app.Config;
import ru.pcom.app.Constants;
import ru.pcom.app.util.FileUtil;
import ru.pcom.app.util.Util;

import java.io.File;

public class FileSysExplorer extends  Thread implements FileExplorer {

    String path = "";
    IFilePanel target;
    private boolean bStop = false, bStopped = false;
    private long fxId = -1L;

    public FileSysExplorer(IFilePanel target, String path, long fxId){
      this.path = path;
      this.target = target;
      this.fxId = fxId;
    }

    public void readFiles(){
        start();
    }

    public void run(){
        bStopped = false;

        File folder = new File(path);
        File[] files =  folder.listFiles();

        Config CFG = Config.get();
        boolean bHideFiles = CFG.isHideFiles();

        if(files==null){
            target.readFinish(-1L,-1L,-1L, "");
            target.postMsg(CFG.getTextResource().getString("err_list_folder")+".");
            return;
        }

        long filez = 0L, folderz = 0L, fsize = 0L;

        for(File f : files){
            if (bStop) {
                bStopped = true;
                return;
            }

            if(bHideFiles && f.isHidden()){
                continue;
            }

            FileData fd = new FileData();
            fd.setFile(f);
            fd.setName(f.getName());
            if(f.isDirectory()) {
                fd.getInfo().setFolder(true);
                fd.setSize(Constants.FOLDER);
                folderz++;
            }else {
                fd.setSize(f.length());
                fsize = fsize + fd.getSize().longValue();
                filez++;
            }

            try {
                fd.setDate(FileUtil.getFileTime(f));
            }catch (Exception e){
                // TODO:
            }

            boolean tryPosix = true;
            try {
                String fa = FileUtil.getFileAttr(f);
                if(bHideFiles && (fa.contains("H") || fa.contains("S"))){
                    continue;
                }
                fd.setAttr(fa);
                tryPosix = false;
            } catch (UnsupportedOperationException x) {
                if(f.isHidden())
                    fd.setAttr("H");
            }catch (Exception e){
                // TODO:
            }

            if(tryPosix) {
                try {
                    fd.setAttr(FileUtil.getFilePxAttr(f));
                } catch (UnsupportedOperationException x) {
                    if (f.isHidden())
                        fd.setAttr("H");
                } catch (Exception e) {
                    // TODO:
                }
            }

            if(!target.addFile(fd, fxId)) {
                bStopped = true;
                return;
            }
        }

        target.readFinish(filez, folderz, fsize, Util.getAutoSize(fsize, Config.get().getUnit()));
        bStopped = true;
    }

    public void stopRead(){
        bStop = true;
        while(!bStopped){
            try{
                Thread.sleep(300L);
            }catch (Exception e){}
        }
    }

}
