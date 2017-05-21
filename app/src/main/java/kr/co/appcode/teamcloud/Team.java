package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chad on 2017-05-21.
 */

public class Team implements Parcelable{
    private int idx;
    private String name;

    public Team(int idx, String name) {
        this.idx = idx;
        this.name = name;
    }

    protected Team(Parcel in) {
        idx = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idx);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

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
}
