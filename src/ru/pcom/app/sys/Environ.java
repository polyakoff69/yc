package ru.pcom.app.sys;

import ru.pcom.app.util.Str;

import java.io.File;

public class Environ {
    public static String getHomeFolder(){
        String h = Str.trim(System.getProperty("user.home"));
        if(!h.isEmpty())
            return h;

        h = Str.trim(System.getenv("USERPROFILE"));
        if(!h.isEmpty())
            return h;

        if(Os.isWindows()){
            try {
                String s = Str.trim(System.getenv("HOMEDRIVE"));
                File fd = new File(s);
                s = Str.trim(System.getenv("HOMEPATH"));
                fd = new File(fd, s);
                if(fd.exists() && fd.isDirectory()){
                    return fd.getAbsolutePath();
                }
            }catch (Exception e){
            }
        }
        return "";
    }
}
