package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-04-30.
 */

public class LatestFileItem {
    private String name;
    private String type;
    private String extension;
    private String writer;
    private String date;
    private double size;

    public LatestFileItem(String name, String type, String extension, String writer, String date) {
        this.name = name;
        this.type = type;
        this.extension = extension;
        this.writer = writer;
        this.date = date;
    }

    public LatestFileItem(String name, String date, double size) {
        this.name = name;
        this.date = date;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
