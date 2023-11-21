package ru.pcom.app.file;

import java.io.File;
import java.nio.file.attribute.FileTime;

public class FileData {

    public FileData(){
        setInfo(new Info());
    }

    public FileData(File f){
        this();
        setFile(f);
    }

    private File file;
    private String name, attr;
    private Info info;
    private Long size;
    private FileTime date;

    public FileTime getDate() {
        return date;
    }

    public void setDate(FileTime date) {
        this.date = date;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File f) {
        this.file = f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        getInfo().setName(name);
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String toString(){
        return getName();
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }


    public class Info {

        private String name;
        private boolean isFolder = false;
        private boolean isParent = false;

        public boolean isParent() {
            return isParent;
        }

        public void setParent(boolean parent) {
            isParent = parent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isFolder() {
            return isFolder;
        }

        public void setFolder(boolean folder) {
            isFolder = folder;
        }

        public String toString(){
            return getName();
        }

    }

}
