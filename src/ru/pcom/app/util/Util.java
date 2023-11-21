package ru.pcom.app.util;

import ru.pcom.app.Config;
import ru.pcom.app.sys.Os;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    public static long K = 1024L;
    public static String B = "B", KB = "KB", MB= "MB", GB = "GB", TB = "TB", PB = "PB";

    public static void initB(){
        Config CFG = Config.get();
        String s = CFG.getTextResource().getString("val_byte");
        if(!Str.isEmpty(s))
            B = s;
        s = CFG.getTextResource().getString("val_kb");
        if(!Str.isEmpty(s))
            KB = s;
        s = CFG.getTextResource().getString("val_mb");
        if(!Str.isEmpty(s))
            MB = s;
        s = CFG.getTextResource().getString("val_gb");
        if(!Str.isEmpty(s))
            GB = s;
        s = CFG.getTextResource().getString("val_tb");
        if(!Str.isEmpty(s))
            TB = s;
        s = CFG.getTextResource().getString("val_pb");
        if(!Str.isEmpty(s))
            PB = s;
    }

    public static String getAutoSize(long n){
        if(n<10L*K)
            return formatLong(n)+" "+B;
        if(n<K*K)
            return formatLong(n/K)+" "+KB;
        if(n<K*K*K)
            return formatDiv(n,(K*K))+" "+MB; // return formatLong(n/(K*K))+" "+MB;
        if(n<K*K*K*K)
            return formatDiv(n,(K*K*K))+" "+GB;
        if(n<K*K*K*K*K)
            return formatDiv(n,(K*K*K*K))+" "+TB;
        if(true || n<K*K*K*K*K*K)
            return formatDiv(n,(K*K*K*K*K))+" "+PB;
        return "";
    }

    public static String getAutoSize(long n, int mode){
        if(mode==1 || n<10L){
            return formatLong(n)+" "+B;
        }
        if(mode==2){
            if(n<102400L){
                return formatDiv(n,K)+" "+KB;
            }
            return formatLong(n/K)+" "+KB;
        }
        return getAutoSize(n);
    }

    public static String formatLong(long n){
        return formatDiv(n,1L);
    }

    public static String formatDiv(long n1, long n2){
        double d = ((double)n1/(double)n2);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(d);
    }

    public static void checkFileName(String f) throws Exception{
        if(Os.isWindows()){
            if(f.contains("/") || f.contains("\\") || f.contains("*") || f.contains(">") || f.contains("<") ||
               f.contains("?") || f.contains("\"") || f.contains("|") || f.contains(":"))
                throw new Exception("Invalid folder name");
        }else {
            if (f.contains("/"))
                throw new Exception("Invalid folder name");
        }
    }

    public static String getFileN(long c){
        return getItemN(c, "file1", "files", "files2");
    }

    public static String getFolderN(long c){
        return getItemN(c, "folder1", "folders", "folders2");
    }

    public static String getFolderNa(long c){
        return getItemN(c, "folder1a", "folders", "folders2");
    }

    public static String getItemN(long c, String item1, String items, String items2){
        Config CFG = Config.get();
        if(c==1L){
            return CFG.getTextResource().getString(item1);
        }
        long ed = c % 10L;
        long dec = c % 100L;
        if(11L<=dec && dec<=20L){
            return CFG.getTextResource().getString(items2);
        }
        if(dec!=11L){
            if(ed==1L){
                return CFG.getTextResource().getString(item1);
            }
            if(2L<=ed && ed<=4L){
                return CFG.getTextResource().getString(items);
            }
        }
        return CFG.getTextResource().getString(items2);
    }

    public static DateTimeFormatter getDateFmt(){
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate( FormatStyle.SHORT )
                .withLocale( Locale.getDefault() )
                .withZone( ZoneId.of(TimeZone.getDefault().getID()) )
                ;
        return fmt;
    }

    public static DateTimeFormatter getTimeFmt(){
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedTime( FormatStyle.SHORT ) // TODO: время может отличаться на 1 час
                .withLocale( Locale.getDefault() )
                .withZone( ZoneId.of(TimeZone.getDefault().getID()) )
        ;
        return fmt;
    }

}
