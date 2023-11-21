package ru.pcom.app.mainwnd;

import java.util.EventObject;

public interface MainWndController {
    void onCmd(String id, Object param, EventObject eo);
}
