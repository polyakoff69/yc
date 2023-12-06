package ru.yc.app.file;

public class BaseFileOperation {
    protected String trgName, trgExt;
    public String applyMask(String fname, String mask){
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

    public void setTargetNameExt(String nx){
        if(nx.contains(".")==false){
            trgName = nx;
            trgExt = "";
            return;
        }

        int pt = nx.lastIndexOf(".");
        trgName = nx.substring(0, pt);
        trgExt = nx.substring(pt+1);
    }
}
