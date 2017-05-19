package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-04-30.
 */

public class LatestContentItem {
    private int idx;
    private String title;
    private String writer;
    private String date;

    public LatestContentItem(int idx, String title, String writer, String date) {
        this.idx = idx;
        this.title = title;
        this.writer = writer;
        this.date = date;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setDate(String date) {
        this.date = date;
    }
}
