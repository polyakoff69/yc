package ru.yc.app.util;

import ru.yc.app.Config;
import ru.yc.app.FileHandler;

import java.io.File;
import java.util.List;

public class FileHandlerUtil {
    public static boolean handleByMask(File f, List<FileHandler> handlers, Config CFG, boolean bEdit) throws Exception{
        if(f==null){
            throw new Exception(CFG.getTextResource().getString("no_file"));
        }
        if(!f.isFile()){
            throw new Exception(CFG.getTextResource().getString("no_file2"));
        }
        if(handlers==null || handlers.size()==0){
            throw new Exception(CFG.getTextResource().getString(bEdit ? "no_file_handlers_e" : "no_file_handlers_v"));
        }

        String fname = f.getName();
        for(FileHandler fh : handlers){
            if(match(fname, fh)){
                exec(f, fname, fh, bEdit, CFG);
                return true;
            }
        }
        throw new Exception(CFG.getTextResource().getString(bEdit ? "not_match_handler_e" : "not_match_handler_v"));
    }

    public static boolean match(String fname, FileHandler h) throws Exception {
      String mask = h.getMask(), fname1 = "", fname2 = "", mask1 = "", mask2 = "";
      int pos = fname.lastIndexOf(".");
      if(pos>=0){
          fname1 = fname.substring(0,pos);
          fname2 = fname.substring(pos+1);
      }else{
          fname1 = fname;
          fname2 = "";
      }
      pos = mask.lastIndexOf(".");
      if(pos>=0){
        mask1 = mask.substring(0,pos);
        mask2 = mask.substring(pos+1);
      }else{
        mask1 = mask;
        mask2 = "";
      }
      if(match(fname1, mask1, 1) && match(fname2, mask2, 2)){
        return true;
      }
      return false;
    }

    public static boolean match(String fname, String mask, int part) throws Exception {
        if("*".equals(mask)){
            return true;
        }
        int pos = mask.indexOf("*");
        if(pos>=0){
            mask = mask.substring(0,pos);
            for(int i=0;i<fname.length();i++){
                mask = mask + "?";
            }
        }
        for(int i=0;i<fname.length();i++){
            char fn = fname.charAt(i);
            if(mask.length()<=i){
                return false;
            }
            char ms = mask.charAt(i);
            if(ms=='?'){
                ms = fn;
            }
            if(fn!=ms){
                return false;
            }
        }
        return true;
    }

    public static void exec(File f, String fname, FileHandler h, boolean bEdit, Config CFG) throws Exception {
        String cmd = h.getCmd();
        if(cmd==null || cmd.trim().isEmpty()){
            throw new Exception(CFG.getTextResource().getString(bEdit ? "no_cmd_e" : "no_cmd_v"));
        }
        File dir = null;
        if(h.getDir()!=null && h.getDir().trim().isEmpty()==false){
            dir = new File(h.getDir().trim());
        }
        String[] env = null;
        if(h.getEnv()!=null && h.getEnv().trim().isEmpty()==false){
            String e = h.getEnv().trim();
            env = e.split(";");
            for(int i=0; i< env.length; i++){
                env[i] = env[i].trim();
            }
        }
        cmd = resolve(cmd, f);
        String par = resolve(h.getParam(), f);
        if(!par.isEmpty()){
            cmd = cmd + " " + par;
        }
        Runtime.getRuntime().exec(cmd , env, dir);
    }

    public static String resolve(String s, File f) throws Exception {
        if(s==null){
            s = "";
        }
        return s.replace("${file}", f.getCanonicalPath());
    }

}
