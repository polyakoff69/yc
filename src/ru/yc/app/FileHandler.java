package ru.yc.app;

public class FileHandler {

  private String mask = "";

  public String getMask() {
    return mask;
  }

  public void setMask(String mask) {
    this.mask = mask;
  }

  public String toString(){
    if(mask==null){
      return "";
    }
    return mask;
  }

}