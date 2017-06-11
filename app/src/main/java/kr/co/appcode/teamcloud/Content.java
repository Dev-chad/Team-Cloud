package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chad on 2017-05-24.
 */

public class Content implements Parcelable{
    private String idx;
    private String writer;
    private String title;
    private String desc;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String writeDate;
    private long fileSize;
    private int readAuth;
    private int ver;

    public Content(String idx, String writer, String title, String desc, String fileName, String fileUrl, String fileType, String writeDate, long fileSize, int readAuth, int ver) {
        this.idx = idx;
        this.writer = writer;
        this.title = title;
        this.desc = desc;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.writeDate = writeDate;
        this.fileSize = fileSize;
        this.readAuth = readAuth;
        this.ver = ver;
    }

    public Content(String idx, String writer, String title, String desc, String writeDate, int readAuth, int ver) {
        this.idx = idx;
        this.writer = writer;
        this.title = title;
        this.desc = desc;
        this.writeDate = writeDate;
        this.readAuth = readAuth;
        this.ver = ver;
    }

    protected Content(Parcel in) {
        idx = in.readString();
        writer = in.readString();
        title = in.readString();
        desc = in.readString();
        fileName = in.readString();
        fileUrl = in.readString();
        fileType = in.readString();
        writeDate = in.readString();
        fileSize = in.readLong();
        readAuth = in.readInt();
        ver = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idx);
        dest.writeString(writer);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(fileName);
        dest.writeString(fileUrl);
        dest.writeString(fileType);
        dest.writeString(writeDate);
        dest.writeLong(fileSize);
        dest.writeInt(readAuth);
        dest.writeInt(ver);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getReadAuth() {
        return readAuth;
    }

    public void setReadAuth(int readAuth) {
        this.readAuth = readAuth;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }
}
