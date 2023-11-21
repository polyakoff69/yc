package ru.pcom.app.util;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;

public class Str {

    public static String trim(String s){
        if(s==null)
            return "";
        return s.trim();
    }

    public static boolean isEmpty(String s){
        return trim(s).isEmpty();
    }

    public static java.util.Map<Integer, Double> decodeNumStr(String s){
        java.util.Map<Integer, Double> m = new HashMap<>();
        if(s==null || s.trim().isEmpty())
            return m;
        String[] ss = s.split(",");
        if(ss==null || ss.length<1)
            return m;
        int i = 0;
        for(String ds : ss){
            double d = -1.0;
            try {
                d = Double.parseDouble(ds);
            }catch (Exception e){
                d = -1.0;
            }
            m.put(i++,d);
        } // for
        return m;
    }

    public static String limb(String s, int lim){
        if(s==null || s.length()<=lim)
            return s;
        int pos = s.length()-lim;
        return "..." + s.substring(pos);
    }

}
