package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-04-30.
 */

public class LatestFileItem {
    private int idx;
    private String name;
    private String type;
    private String writer;
    private String date;
    private double size;

    public LatestFileItem(int idx, String name, String type, String writer, double size, String date) {
        this.idx = idx;
        this.name = name;
        this.type = type;
        this.writer = writer;
        this.date = date;
        this.size = size;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
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
