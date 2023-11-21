package ru.pcom.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
}
