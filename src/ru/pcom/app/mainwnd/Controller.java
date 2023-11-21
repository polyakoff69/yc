package ru.pcom.app.mainwnd;

import java.util.EventObject;

public class Controller implements MainWndController {

    MainFrm frame;

    Controller(MainFrm frame){
        this.frame = frame;
    }

    public synchronized void onCmd(String id, Object param, EventObject eo){
        onCmd2(id, param, eo);
        frame.updateControls(id);
    }

    private void onCmd2(String id, Object param, EventObject eo){
      if("focus".equalsIgnoreCase(id)) {
        frame.onChFocus(param);
        return;
      }
      if("addpanel".equalsIgnoreCase(id)) {
          frame.addPanel(param);
          return;
      }
      if("root_folder".equalsIgnoreCase(id)) {
          frame.openRootFolder(param);
          return;
      }
      if("ch_disk".equalsIgnoreCase(id)) {
          frame.chDisk(param);
          return;
      }
      if("about".equalsIgnoreCase(id)) {
          frame.onAbout(param);
          return;
      }
      if("options".equalsIgnoreCase(id)) {
          frame.onOptions(param);
          return;
      }
      if("history<".equalsIgnoreCase(id)) {
          frame.onHistBack(param);
          return;
      }
      if("history>".equalsIgnoreCase(id)) {
          frame.onHistFwd(param);
          return;
      }
      if("refresh".equalsIgnoreCase(id)) {
        frame.onRefresh(param);
        return;
      }
      if("home_folder".equalsIgnoreCase(id)) {
        frame.openHomeFolder(param);
        return;
      }
      if("chdriveL".equalsIgnoreCase(id)){
          frame.chDiskMenu("left:");
          return;
      }
      if("chdriveR".equalsIgnoreCase(id)){
          frame.chDiskMenu("right:");
          return;
      }
      if("mkdir".equalsIgnoreCase(id)){
          frame.onMkDir(param);
          return;
      }
      if("rename".equalsIgnoreCase(id)){
        frame.onRename(param);
        return;
      }
      if("fcopy".equalsIgnoreCase(id) || "fmove".equalsIgnoreCase(id)){
        frame.onCopyMove(id, param);
        return;
      }
      if("fdelete".equalsIgnoreCase(id)){
        frame.onDelete(param);
        return;
      }
      if("endoper".equalsIgnoreCase(id)){
          frame.onEndOper();
          return;
      }
      if("5050".equalsIgnoreCase(id) || "100".equalsIgnoreCase(id)){
        frame.onSplitPos(id, param);
        return;
      }
      if("menu".equalsIgnoreCase(id)){
        frame.onMMenu();
        return;
      }
    }

}
