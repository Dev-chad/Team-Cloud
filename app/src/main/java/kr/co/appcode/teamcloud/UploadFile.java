package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chad on 2017-06-04.
 */

public class UploadFile implements Parcelable{
    public static final int TYPE_DIRECTORY = 1;
    public static final int TYPE_FILE = 2;

    private String fileName;
    private String fileUri;
    private String fileSize;
    private String fileDate;
    private int innerCount;
    private int fileType;

    public UploadFile(String fileName, String fileUri, int innerCount){
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.innerCount = innerCount;
        fileType = TYPE_DIRECTORY;
    }

    public UploadFile(String fileName, String fileUri, String fileSize, String fileDate) {
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        fileType = TYPE_FILE;
    }

    protected UploadFile(Parcel in) {
        fileName = in.readString();
        fileUri = in.readString();
        fileSize = in.readString();
        fileDate = in.readString();
        fileType = in.readInt();
        innerCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(fileUri);
        dest.writeString(fileSize);
        dest.writeString(fileDate);
        dest.writeInt(fileType);
        dest.writeInt(innerCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadFile> CREATOR = new Creator<UploadFile>() {
        @Override
        public UploadFile createFromParcel(Parcel in) {
            return new UploadFile(in);
        }

        @Override
        public UploadFile[] newArray(int size) {
            return new UploadFile[size];
        }
    };

    public int getInnerCount() {
        return innerCount;
    }

    public void setInnerCount(int innerCount) {
        this.innerCount = innerCount;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}
