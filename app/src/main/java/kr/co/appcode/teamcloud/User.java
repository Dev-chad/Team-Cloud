package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chad on 2017-04-21.
 */

public class User implements Parcelable{
    private String id;
    private String nickname;
    private String name;
    private int credit;
    private int used_capacity;
    private int max_capacity;
    private String accountType;
    private String sessionKey;

    public User(String id, String nickname, String name, int credit, int used_capacity, int max_capacity, String accountType) {
        this.id = id;
        this.nickname = nickname;
        this.name = name;
        this.credit = credit;
        this.used_capacity = used_capacity;
        this.max_capacity = max_capacity;
        this.accountType = accountType;
    }

    public User(JSONObject jsonObject){
        try {
            id = jsonObject.getString("id");
            nickname = jsonObject.getString("nickname");
            name = jsonObject.getString("name");
            credit = jsonObject.getInt("credit");
            used_capacity = jsonObject.getInt("used_capacity");
            max_capacity = jsonObject.getInt("max_capacity");
            accountType = jsonObject.getString("account_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(JSONObject jsonObject, String type){
        try {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            accountType = type;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected User(Parcel in) {
        id = in.readString();
        nickname = in.readString();
        name = in.readString();
        credit = in.readInt();
        used_capacity = in.readInt();
        max_capacity = in.readInt();
        accountType = in.readString();
        sessionKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nickname);
        dest.writeString(name);
        dest.writeInt(credit);
        dest.writeInt(used_capacity);
        dest.writeInt(max_capacity);
        dest.writeString(accountType);
        dest.writeString(sessionKey);
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

    public int getUsed_capacity() {
        return used_capacity;
    }

    public void setUsed_capacity(int used_capacity) {
        this.used_capacity = used_capacity;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity = max_capacity;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
