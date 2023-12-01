package ru.yc.app.util;

import ru.yc.app.sys.Os;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;

public class FileUtil {
    public static boolean deleteFile(File f){
        return f.delete(); // TODO: recycle bin optionally
    }

    public static FileTime getFileTime(File f) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
        FileTime ftm = attr.lastModifiedTime();
        if(ftm==null)
            ftm = attr.creationTime();
        return ftm;
    }

    public static String getFileAttr(File f) throws IOException {
        DosFileAttributes attr2 = Files.readAttributes(f.toPath(), DosFileAttributes.class);
        String s = "";
        if(attr2.isReadOnly())
            s+="R";
        if(attr2.isSystem())
            s+="S";
        if(attr2.isHidden())
            s+="H";
        if(attr2.isArchive())
            s+="A";
        return s;
    }

    public static String getFilePxAttr(File f) throws IOException {
        PosixFileAttributes attr2 = Files.readAttributes(f.toPath(), PosixFileAttributes.class);
        return PosixFilePermissions.toString(attr2.permissions());
    }

    public static boolean isRO(Path file){
        File f = file.toFile();
        if(f.exists()==false){
            return false;
        }
        if(f.isHidden() || f.canWrite()==false){
            return true;
        }
        if(Os.isWindows()){
            try {
                String a = getFileAttr(f);
                if(a.contains("R") || a.contains("S") || a.contains("H")){ // TODO: unix
                    return true;
                }
            }catch (Exception e){}
        }
        return false;
    }

    public static void cleanRO(Path p){
        p.toFile().setWritable(true);
        if(Os.isWindows()){
            try {
                Files.setAttribute(p, "dos:readonly", false); // TODO: unix?
            }catch (Exception e){}
        }
    }
}
