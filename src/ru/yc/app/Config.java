package ru.yc.app;

import javafx.application.Application;
import ru.yc.app.rs.Text;
import ru.yc.app.rs.TextRU;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Config {
    private static Config c;

    private Config(){
        textResource = new Text();
    }

    public static Config get(){
        if(c==null){
            c = new Config();
        }
        return c;
    }

    private Application application = null;

    private ResourceBundle textResource = null;

    private String loadErr = "";

    private String defPanelPath = "."; // TODO:

    private int defTabL = 0;
    private int defTabR = 0;
    private String defSide = "";

    private Map<String, String[]> tabs = new HashMap<>();

    private int frmW = 1000;
    private int frmH = 800;
    private int frmX = -1;
    private int frmY = -1;

    private float split = 0.5F;

    private int fontSz = 12;
    private String font = "";

    private String lang = "EN";

    private boolean eagerLoad = false;
    private boolean readOnActiv = true;
    private boolean hideFiles = true;

    private int unit = 0; // file size unit 0 = auto / 1 = byte / 2 = Kb

    private java.util.List<FileHandler> editors = new ArrayList<>();

    private java.util.List<FileHandler> viewers = new ArrayList<>();

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ResourceBundle getTextResource() {
        return textResource;
    }

    public void setTextResource(ResourceBundle textResource) {
        this.textResource = textResource;
    }

    public void setTextResourceL(String lang) {
        if("EN".equalsIgnoreCase(lang)){
            textResource = new Text();
        }else if("RU".equalsIgnoreCase(lang)){
            textResource = new TextRU();
        }else{
            textResource = new Text();
        }
    }

    public String getLoadErr() {
        return loadErr;
    }

    public void setLoadErr(String loadErr) {
        this.loadErr = loadErr;
    }

    public int getFrmX() {
        return frmX;
    }

    public void setFrmX(int frmX) {
        this.frmX = frmX;
    }

    public int getFrmY() {
        return frmY;
    }

    public void setFrmY(int frmY) {
        this.frmY = frmY;
    }

    public int getFrmW() {
        return frmW;
    }

    public int getFrmH() {
        return frmH;
    }

    public void setFrmW(int frmW) {
        this.frmW = frmW;
    }

    public void setFrmH(int frmH){
        this.frmH = frmH;
    }

    public int getFontSz() {
        return fontSz;
    }

    public void setFontSz(int fontSz) {
        this.fontSz = fontSz;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDefPanelPath() {
        return defPanelPath;
    }

    public void setDefPanelPath(String defPanelPath) {
        this.defPanelPath = defPanelPath;
    }

    public int getDefTabL() {
        return defTabL;
    }

    public void setDefTabL(int defTabL) {
        this.defTabL = defTabL;
    }

    public int getDefTabR() {
        return defTabR;
    }

    public void setDefTabR(int defTabR) {
        this.defTabR = defTabR;
    }

    public String getDefSide() {
        return defSide;
    }

    public void setDefSide(String defSide) {
        this.defSide = defSide;
    }

    public boolean isEagerLoad() {
        return eagerLoad;
    }

    public void setEagerLoad(boolean eagerLoad) {
        this.eagerLoad = eagerLoad;
    }

    public boolean isReadOnActiv() {
        return readOnActiv;
    }

    public void setReadOnActiv(boolean readOnActiv) {
        this.readOnActiv = readOnActiv;
    }

    public boolean isHideFiles() {
        return hideFiles;
    }

    public void setHideFiles(boolean hideFiles) {
        this.hideFiles = hideFiles;
    }

    public float getSplit() {
        return split;
    }

    public void setSplit(float split) {
        this.split = split;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public void  setTabs(Map<String, String[]> m){
        tabs = m;
    }

    public Map<String, String[]> getTabs(){
        return tabs;
    }

    public List<FileHandler> getEditors() {
        if(editors==null){
            editors = new ArrayList<>();
        }
        return editors;
    }

    public void setEditors(List<FileHandler> editors) {
        this.editors = editors;
    }

    public List<FileHandler> getViewers() {
        if(viewers==null){
            viewers = new ArrayList<>();
        }
        return viewers;
    }

    public void setViewers(List<FileHandler> viewers) {
        this.viewers = viewers;
    }


    public Properties getAllProps(){
        Properties pp = new Properties();
        pp.setProperty("frame.w", ""+frmW);
        pp.setProperty("frame.h", ""+frmH);
        pp.setProperty("frame.x", ""+frmX);
        pp.setProperty("frame.y", ""+frmY);
        pp.setProperty("font", ""+font);
        pp.setProperty("font.sz", ""+fontSz);
        pp.setProperty("deftabl", ""+defTabL);
        pp.setProperty("deftabr", ""+defTabR);
        pp.setProperty("defside", ""+defSide);
        pp.setProperty("eagerLoad", ""+eagerLoad);
        pp.setProperty("readOnActiv", ""+readOnActiv);
        pp.setProperty("hideFiles", ""+hideFiles);
        pp.setProperty("split", ""+split);
        pp.setProperty("unit", ""+unit);

        if(lang==null || lang.trim().isEmpty())
            lang = "EN";
        pp.setProperty("lang", ""+lang);

        Map<String, String[]> m = getTabs();
        if(m!=null && m.isEmpty()==false) {
            String[] pans = new String[]{"l", "r"};
            for (String pan : pans) {
                String[] paths = m.get(pan);
                if(paths==null || paths.length<1) {
                    pp.setProperty("tabs." + pan, "" + 0);
                    continue;
                }

                pp.setProperty("tabs." + pan, "" + paths.length);
                int i = 0;
                for (String path : paths) {
                    pp.setProperty("tab." + pan+"."+(++i), "" + path);
                } // for
            } // for

            pans = new String[]{"wl", "wr"};
            for (String pan : pans) {
                String[] colw = m.get(pan);
                if(colw==null || colw.length<1) {
                    pp.setProperty("tabscolw." + pan, "" + 0);
                    continue;
                }

                pp.setProperty("tabscolw." + pan, "" + colw.length);
                int i = 0;
                for (String w : colw) {
                    pp.setProperty("tabcolw." + pan+"."+(++i), "" + w);
                } // for
            } // for
        }// if

        pp.setProperty("viewers", ""+getViewers().size());
        int i = 0;
        for (FileHandler fh : getViewers()){
            pp.setProperty("vwr_cmd."+i, ""+fh.getCmd());
            pp.setProperty("vwr_dir."+i, ""+fh.getDir());
            pp.setProperty("vwr_env."+i, ""+fh.getEnv());
            pp.setProperty("vwr_par."+i, ""+fh.getParam());
            pp.setProperty("vwr_mask."+i, ""+fh.getMask());
            pp.setProperty("vwr_mode."+i, ""+fh.getMode());
            i++;
        }

        pp.setProperty("editors", ""+getEditors().size());
        i = 0;
        for (FileHandler fh : getEditors()){
            pp.setProperty("edt_cmd."+i, ""+fh.getCmd());
            pp.setProperty("edt_dir."+i, ""+fh.getDir());
            pp.setProperty("edt_env."+i, ""+fh.getEnv());
            pp.setProperty("edt_par."+i, ""+fh.getParam());
            pp.setProperty("edt_mask."+i, ""+fh.getMask());
            pp.setProperty("edt_mode."+i, ""+fh.getMode());
            i++;
        }

        return  pp;
    }

    public void load(String file) throws IOException {
        Properties pp = new Properties();
        pp.loadFromXML(new FileInputStream(file));

        try{
            frmW = Integer.parseInt(pp.getProperty("frame.w", "1000"));
        }catch (Exception e){
            frmW = 1000;
        }

        try{
            frmH = Integer.parseInt(pp.getProperty("frame.h", "800"));
        }catch (Exception e){
            frmH = 800;
        }

        try{
            frmX = Integer.parseInt(pp.getProperty("frame.x", "-1"));
        }catch (Exception e){
            frmX = -1;
        }

        try{
            frmY = Integer.parseInt(pp.getProperty("frame.y", "-1"));
        }catch (Exception e){
            frmY = -1;
        }

        try{
            fontSz = Integer.parseInt(pp.getProperty("font.sz", "12"));
        }catch (Exception e){
            fontSz = 12;
        }

        try{
            font = pp.getProperty("font");
        }catch (Exception e){
            font = "";
        }

        try{
            defSide = pp.getProperty("defside");
        }catch (Exception e){
            defSide = "";
        }

        try{
            defTabL = Integer.parseInt(pp.getProperty("deftabl", "0"));
        }catch (Exception e){
            defTabL = 0;
        }

        try{
            defTabR = Integer.parseInt(pp.getProperty("deftabr", "0"));
        }catch (Exception e){
            defTabR = 0;
        }

        try{
            eagerLoad = Boolean.parseBoolean(pp.getProperty("eagerLoad", "false"));
        }catch (Exception e){
            eagerLoad = false;
        }

        try{
            readOnActiv = Boolean.parseBoolean(pp.getProperty("readOnActiv", "true"));
        }catch (Exception e){
            readOnActiv = true;
        }

        try{
            hideFiles = Boolean.parseBoolean(pp.getProperty("hideFiles", "true"));
        }catch (Exception e){
            hideFiles = true;
        }

        try{
            lang = pp.getProperty("lang", "EN");
        }catch (Exception e){
            lang = "EN";
        }

        try{
            split = Float.parseFloat(pp.getProperty("split", "0.5"));
        }catch (Exception e){
            split = 0.5F;
        }

        try{
            unit = Integer.parseInt(pp.getProperty("unit", "0"));
        }catch (Exception e){
            unit = 0;
        }

        tabs = new HashMap<>();
        String[] pans = new String[]{"l", "r"};
        for (String pan : pans) {
            int c = 0;
            try{
                c = Integer.parseInt(pp.getProperty("tabs." + pan));
            }catch (Exception e){
                c = -1;
            }

            List<String> v = new ArrayList<>(), v1 = new ArrayList<>();

            if(c<1){
                v.add("/");
            }

            for (int i=1;i<=c;i++) {
                String path = pp.getProperty("tab." + pan+"."+i, "");
                if(path.trim().isEmpty())
                    continue;
                v.add(path);

                String colw = pp.getProperty("tabcolw.w" + pan+"."+i, "");
                v1.add(colw);
            } // for

            tabs.put(pan, v.toArray(new String[v.size()]));
            tabs.put("w"+pan, v1.toArray(new String[v1.size()]));
        } // for

        for(int j=0;j<2;j++) {
            int cnt = 0;
            try {
                cnt = Integer.parseInt(pp.getProperty((j==0 ? "viewers" : "editors"), "0"));
            } catch (Exception e) {
                cnt = 0;
            }

            String id = "vwr";
            if(j>0){
                id = "edt";
            }
            for (int i = 0; i < cnt; i++) {
                FileHandler fh = new FileHandler();
                fh.setCmd(pp.getProperty(id+"_cmd." + i, ""));
                fh.setDir(pp.getProperty(id+"_dir." + i, ""));
                fh.setEnv(pp.getProperty(id+"_env." + i, ""));
                fh.setParam(pp.getProperty(id+"_par." + i, ""));
                fh.setMask(pp.getProperty(id+"_mask." + i, ""));
                fh.setMode(pp.getProperty(id+"_mode." + i, ""));
                if(j==0){
                    getViewers().add(fh);
                }else {
                    getEditors().add(fh);
                }
            }
        }

    }

}
