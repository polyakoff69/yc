package ru.yc.app.mainwnd;

import java.util.EventObject;

public interface MainWndController {
    void onCmd(String id, Object param, EventObject eo);
}
