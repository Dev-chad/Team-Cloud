package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chad on 2017-04-21.
 */

public class User implements Parcelable {
    private String idx;
    private String id;
    private String nickname;
    private String name;
    private int credit;
    private int usedCapacity;
    private int availableCapacity;
    private int maxCapacity;
    private String accountType;
    private String sessionInfo;
    private int level;

    public User(String id, String nickname, String name, int credit, int usedCapacity, int maxCapacity, String accountType) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.credit = credit;
        this.usedCapacity = usedCapacity;
        this.maxCapacity = maxCapacity;
        this.availableCapacity = maxCapacity - usedCapacity;
        this.accountType = accountType;
    }

    public User(JSONObject jsonObject) {
        try {
            idx = jsonObject.getString("idx");
            id = jsonObject.getString("id");
            nickname = jsonObject.getString("nickname");
            name = jsonObject.getString("name");
            credit = jsonObject.getInt("credit");
            usedCapacity = jsonObject.getInt("usedCapacity");
            maxCapacity = jsonObject.getInt("maxCapacity");
            sessionInfo = jsonObject.getString("sessionInfo");
            accountType = jsonObject.getString("accountType");
            availableCapacity = maxCapacity - usedCapacity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected User(Parcel in) {
        idx = in.readString();
        id = in.readString();
        nickname = in.readString();
        name = in.readString();
        credit = in.readInt();
        usedCapacity = in.readInt();
        maxCapacity = in.readInt();
        accountType = in.readString();
        sessionInfo = in.readString();
        availableCapacity = in.readInt();
        level = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idx);
        dest.writeString(id);
        dest.writeString(nickname);
        dest.writeString(name);
        dest.writeInt(credit);
        dest.writeInt(usedCapacity);
        dest.writeInt(maxCapacity);
        dest.writeString(accountType);
        dest.writeString(sessionInfo);
        dest.writeInt(availableCapacity);
        dest.writeInt(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(int usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(String sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public void update(JSONObject jsonObject) {
        try {
            idx = jsonObject.getString("idx");
            id = jsonObject.getString("id");
            nickname = jsonObject.getString("nickname");
            name = jsonObject.getString("name");
            credit = jsonObject.getInt("credit");
            usedCapacity = jsonObject.getInt("usedCapacity");
            maxCapacity = jsonObject.getInt("maxCapacity");
            sessionInfo = jsonObject.getString("sessionInfo");
            accountType = jsonObject.getString("accountType");
            availableCapacity = maxCapacity - usedCapacity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
