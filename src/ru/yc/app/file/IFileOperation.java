package ru.yc.app.file;

import ru.yc.app.dlgoper.DlgOperation;
import ru.yc.app.mainwnd.Controller;

public interface IFileOperation {
    void setSourceTarget(String s, String t) throws Exception;
    void run(String op, java.util.List files, Class clazz, Controller ctr, DlgOperation dlgOp) throws Exception;
}
