package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chad on 2017-05-07.
 */

public class Board implements Parcelable{

    private String idx;
    private String name;
    private int writeAuth;
    private int readAuth;

    public Board(String idx, String name, int writeAuth, int readAuth) {
        this.idx = idx;
        this.name = name;
        this.writeAuth = writeAuth;
        this.readAuth = readAuth;
    }

    public Board(String name, int writeAuth, int readAuth) {
        this.name = name;
        this.writeAuth = writeAuth;
        this.readAuth = readAuth;
    }

    protected Board(Parcel in) {
        idx = in.readString();
        name = in.readString();
        writeAuth = in.readInt();
        readAuth = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idx);
        dest.writeString(name);
        dest.writeInt(writeAuth);
        dest.writeInt(readAuth);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWriteAuth() {
        return writeAuth;
    }

    public void setWriteAuth(int writeAuth) {
        this.writeAuth = writeAuth;
    }

    public int getReadAuth() {
        return readAuth;
    }

    public void setReadAuth(int readAuth) {
        this.readAuth = readAuth;
    }
}
