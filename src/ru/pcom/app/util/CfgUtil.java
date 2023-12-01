package ru.pcom.app.util;

import ru.pcom.app.Config;
import ru.pcom.app.gui.MsgBox;
import ru.pcom.app.sys.Environ;
import ru.pcom.app.sys.Os;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;

public class CfgUtil {
  public static void saveCfg(Class clazz){
    Config CFG = Config.get();
    try {
      File f = new File(Environ.getHomeFolder());
      f = new File(f,".yc");
      if(!f.exists()){
        f.mkdir();
      }
      f = new File(f,"yc.xml");
      CFG.getAllProps().storeToXML(new FileOutputStream(f), "The Ypsilon Commander configuration", "UTF-8");
    }catch(Exception e){
      MsgBox.showErrorOk(clazz, CFG.getTextResource().getString("err_cfg_wrt")+": " + e.getMessage(), null, null);
    }
  }

  public static String loadCSS(Class clazz, String css){
    try {
      String css1 = css.trim();
      if(css1.startsWith("/") || css1.startsWith("\\"))
        css1 = css1.substring(1);
      File f = new File(Environ.getHomeFolder());
      f = new File(f,".yc");
      f = new File(f,css1);
      if(f.exists() && f.isFile()) {
        String l = "";
        if(Os.isWindows()){
          l = "/";
        }
        css1 = "file:"+l+f.getAbsolutePath();
        css1 = css1.replace("\\","/");
        return css1;
      }
    }catch (Exception e){
      // TODO: log
    }

    URL url = clazz.getResource(css);
    if(url!=null) {
      String css1 = url.toExternalForm();
      return css1;
    }
    return null;
  }

  public static void saveCfgCss(Class clazz){
    String[] css = {"base.css", "app.css", "dlg.css"};
    Config CFG = Config.get();
    try {
      File f = new File(Environ.getHomeFolder());
      f = new File(f,".yc");

      String font = CFG.getFont();
      int ifsz = CFG.getFontSz();
      if(font==null || font.trim().isEmpty()){
        if(12==ifsz || ifsz<0){
          deleteUsrCss(f, css);
          return;
        }
      }

      if(!f.exists()){
        f.mkdir();
      }

      Exception ex = null;
      for (String cssfile : css) {
        try {
          File f1 = new File(f, cssfile);
          copyUsrCss(clazz,"/"+cssfile, f1);
        }catch (Exception e1){
          ex = e1;
        }
      }
      if(ex!=null)
        throw ex;
    }catch(Exception e){
      MsgBox.showErrorOk(clazz, CFG.getTextResource().getString("err_cfg_wrt")+": " + e.getMessage(), null, null);
    }
  }

  public static void deleteUsrCss(File folder,String[] css) throws Exception{
    if(!folder.exists())
      return;

    Exception ex = null;
    for (String cssfile : css) {
      try {
        File f1 = new File(folder, cssfile);
        if(f1.exists())
          f1.delete();
      }catch (Exception e1){
        ex = e1;
      }
    }
    if(ex!=null) {
      Config CFG = Config.get();
      throw new Exception(CFG.getTextResource().getString("err_delete_file") + ": " + ex.getMessage());
    }
  }

  public static void copyUsrCss(Class clazz, String src, File trg) throws Exception{
    URL url = clazz.getResource(src);
    if(url!=null) {
      src = url.toExternalForm();
      if(src.startsWith("file:")){
        src = src.substring(5);
      }
      copyUsrCss(new File(src), trg);
    }else{
      Config CFG = Config.get();
      throw new Exception(CFG.getTextResource().getString("err_get_resource"));
    }
  }

  public static void copyUsrCss(File fin, File dest) throws Exception {
    FileInputStream fis = new FileInputStream(fin);
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));

    FileWriter fstream = new FileWriter(dest, false);
    BufferedWriter out = new BufferedWriter(fstream);

    Config CFG = Config.get();

    String aLine = null;
    while ((aLine = in.readLine()) != null) {
      aLine = processCssStr(aLine, CFG.getFont(), CFG.getFontSz());
      out.write(aLine);
      out.newLine();
    }
    in.close();
    out.close();
  }

  public static String processCssStr(String s, String font, int sz){
    if(s.contains("-fx-font-size:")){
      return "  -fx-font-size: "+sz+";";
    }

    if(s.contains("-fx-font-family:")){
      if(font.contains(" ")){
        font = "\""+font+"\"";
      }
      return s.replace("-fx-font-family:", "-fx-font-family: "+font+", ");
    }
    return s;
  }

}
