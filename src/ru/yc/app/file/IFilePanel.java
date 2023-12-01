package ru.yc.app.file;

public interface IFilePanel {

    boolean addFile(FileData fd, long fxId);
    void postMsg(String s);
    void readFinish(long files, long folders, long fsize, String autosz);

}
