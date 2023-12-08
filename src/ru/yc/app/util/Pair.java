package ru.yc.app.util;

public class Pair {
  public Pair(String id, String name){
    this.id = id;
    this.name = name;
  }

  public String id, name;

  public String toString(){
    return name;
  }
}
