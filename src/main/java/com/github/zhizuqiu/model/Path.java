package com.github.zhizuqiu.model;

public class Path implements Comparable<Path> {
    private Integer Id;
    private Boolean IsDir;
    private String LastModified;
    private Long Length;
    private String Name;
    private String Path;

    public Path(Integer id, Boolean isDir, String lastModified, Long length, String name, String path) {
        this.Id = id;
        this.IsDir = isDir;
        this.LastModified = lastModified;
        this.Length = length;
        this.Name = name;
        this.Path = path;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        this.Id = id;
    }

    public Boolean getDir() {
        return IsDir;
    }

    public void setDir(Boolean dir) {
        IsDir = dir;
    }

    public String getLastModified() {
        return LastModified;
    }

    public void setLastModified(String lastModified) {
        this.LastModified = lastModified;
    }

    public Long getLength() {
        return Length;
    }

    public void setLength(Long length) {
        this.Length = length;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        this.Path = path;
    }

    @Override
    public String toString() {
        return "Path{" +
                "id=" + Id +
                ", isDir=" + IsDir +
                ", lastModified='" + LastModified + '\'' +
                ", length=" + Length +
                ", name='" + Name + '\'' +
                ", path='" + Path + '\'' +
                '}';
    }

    @Override
    public int compareTo(Path o) {
        if (!this.IsDir && o.IsDir) {
            return 1;
        }
        if (this.IsDir && !o.IsDir) {
            return -1;
        }
        return this.Name.compareTo(o.Name);
    }
}
