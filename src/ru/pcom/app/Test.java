package ru.pcom.app;

import ru.pcom.app.file.SysFileOperation;

public class Test {
    static String trgNameExt = "x??????????????.7?7",
                  trgName = "x?????????????",
                  trgExt = "7?7";

    public static void main(String[] args) {
        System.out.println(applyMask("filename.ext", trgNameExt));

        if(true)
            return;

        Config CFG = Config.get();
        SysFileOperation fop = new SysFileOperation(CFG, true, false, true);
        String[] tt = {
            "file.ext",
            "C:\\",
            "C:/",
            "C:\\TEMP\\",
            "C:TEMP\\",
            "C:TEMP\\Low",
            "C:\\file",
            "C:file",
            "C:*.*",
            "C:\\*.*",
            "C:\\temp/*.*",
            "E:\\PIC/*.*",
            "E:\\PIC/xxx",
            "E:/PIC/*.*",
            "E:/PIC/???.*",
            "E:/PIC\\???abc.1*",
            "E:/PIC/..\\???abc.1*",
            "..\\*.*",
            "..\\../*.*",
            ".\\../*.*",
            "./000",
            "111",
            "111.*",
            "\\",
            "\\*",
            "\\temp",
            "\\temp\\",
            "\\temp\\*.*",
            "\\temp\\file.ext",
            "/file.ext",
            "555/333/000/*.*"
        };
        fop.setSource("C:\\TEMP");
        for(String t : tt) {
            System.out.println(">>> "+t);
            try {
                fop.setTarget(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String applyMask(String fname, String mask){
        if(mask.contains(".")){
            int pos = fname.lastIndexOf(".");
            String fname1 = fname.substring(0,pos);
            String ext = fname.substring(pos+1);
            return applyMask(fname1, trgName)+"."+applyMask(ext, trgExt);
        }
        if("*".equals(mask)){
            return fname;
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
}
