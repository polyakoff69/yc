package ru.yc.app.util;

import ru.yc.app.Config;

public class UiUtil {
  public static double getWidgetWidth(int w){
    int f = Config.get().getFontSz();
    w = w + (f-12)*5;
    return w;
  }
}
