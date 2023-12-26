package ru.yc.app;

public class FileHandler {

  public static final String INTERNAL = "I", EXTERNAL = "X";

  public FileHandler(){
  }

  public FileHandler(String mask){
    this();
    setMask(mask);
  }

  private String mode = INTERNAL;
  private String mask = "";
  private String cmd;
  private String param = "";
  private String env = "";
  private String dir = "";

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getMask() {
    return mask;
  }

  public void setMask(String mask) {
    this.mask = mask;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public String getEnv() {
    return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public String getDir() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public String toString(){
    if(mask==null){
      return "";
    }
    return mask;
  }

}