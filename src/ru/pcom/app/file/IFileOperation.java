package ru.pcom.app.file;

import ru.pcom.app.Config;
import ru.pcom.app.dlgoper.DlgOperation;
import ru.pcom.app.mainwnd.Controller;

public interface IFileOperation {
    void setSourceTarget(String s, String t) throws Exception;
    void run(String op, java.util.List files, Class clazz, Controller ctr, DlgOperation dlgOp) throws Exception;
}
