package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chad on 2017-05-21.
 */

public class Team implements Parcelable {
    private String idx;
    private String name;
    private String master;
    private long usedCapacity;
    private int maxCapacity;
    private boolean isPublic;
    private boolean isAutoJoin;
    private boolean isAdminManageMember;
    private boolean isAdminManageBoard;
    private boolean isAdminManageContents;
    private String teamMarkUrl;
    private int level;

    public Team() {
    }

    public Team(String idx, String name, String master, long usedCapacity, int maxCapacity, boolean isPublic, boolean isAutoJoin, boolean isAdminManageMember, boolean isAdminManageBoard, boolean isAdminManageContents, String teamMarkUrl) {
        this.idx = idx;
        this.name = name;
        this.master = master;
        this.usedCapacity = usedCapacity;
        this.maxCapacity = maxCapacity;
        this.isPublic = isPublic;
        this.isAutoJoin = isAutoJoin;
        this.isAdminManageMember = isAdminManageMember;
        this.isAdminManageBoard = isAdminManageBoard;
        this.isAdminManageContents = isAdminManageContents;
        this.teamMarkUrl = teamMarkUrl;
    }

    public Team(String idx, String name, String master, long usedCapacity, int maxCapacity, int isPublic, int isAutoJoin, int isAdminManageMember, int isAdminManageBoard, int isAdminManageContents, String teamMarkUrl) {
        this.idx = idx;
        this.name = name;
        this.master = master;
        this.usedCapacity = usedCapacity;
        this.maxCapacity = maxCapacity;

        if (isPublic == 1) {
            this.isPublic = true;
        }

        if (isAutoJoin == 1) {
            this.isAutoJoin = true;
        }

        if (isAdminManageMember == 1) {
            this.isAdminManageMember = true;
        }

        if (isAdminManageBoard == 1) {
            this.isAdminManageBoard = true;
        }

        if (isAdminManageContents == 1) {
            this.isAdminManageContents = true;
        }

        this.teamMarkUrl = teamMarkUrl;
    }

    private Team(JSONObject jsonObject) {
        try {
            idx = jsonObject.getString("idx");
            name = jsonObject.getString("name");
            master = jsonObject.getString("master");
            usedCapacity = jsonObject.getLong("usedCapacity");
            maxCapacity = jsonObject.getInt("maxCapacity");
            if (jsonObject.getInt("isPublic") == 1) {
                isPublic = true;
            }

            if (jsonObject.getInt("isAutoJoin") == 1) {
                isAutoJoin = true;
            }

            if (jsonObject.getInt("isAdminManageMember") == 1) {
                isAdminManageMember = true;
            }

            if (jsonObject.getInt("isAdminManageBoard") == 1) {
                isAdminManageBoard = true;
            }

            if (jsonObject.getInt("isAdminContents") == 1) {
                isAdminManageContents = true;
            }

            teamMarkUrl = jsonObject.getString("teamMarkUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Team(Parcel in) {
        idx = in.readString();
        name = in.readString();
        master = in.readString();
        usedCapacity = in.readLong();
        maxCapacity = in.readInt();
        isPublic = in.readByte() != 0;
        isAutoJoin = in.readByte() != 0;
        isAdminManageMember = in.readByte() != 0;
        isAdminManageBoard = in.readByte() != 0;
        isAdminManageContents = in.readByte() != 0;
        teamMarkUrl = in.readString();
        level = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idx);
        dest.writeString(name);
        dest.writeString(master);
        dest.writeDouble(usedCapacity);
        dest.writeInt(maxCapacity);
        dest.writeInt(level);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeByte((byte) (isAutoJoin ? 1 : 0));
        dest.writeByte((byte) (isAdminManageMember ? 1 : 0));
        dest.writeByte((byte) (isAdminManageBoard ? 1 : 0));
        dest.writeByte((byte) (isAdminManageContents ? 1 : 0));
        dest.writeString(teamMarkUrl);
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

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public long getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(long usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isAutoJoin() {
        return isAutoJoin;
    }

    public void setAutoJoin(boolean autoJoin) {
        isAutoJoin = autoJoin;
    }

    public boolean isAdminManageMember() {
        return isAdminManageMember;
    }

    public void setAdminManageMember(boolean adminManageMember) {
        isAdminManageMember = adminManageMember;
    }

    public boolean isAdminManageBoard() {
        return isAdminManageBoard;
    }

    public void setAdminManageBoard(boolean adminManageBoard) {
        isAdminManageBoard = adminManageBoard;
    }

    public boolean isAdminManageContents() {
        return isAdminManageContents;
    }

    public void setAdminManageContents(boolean adminManageContents) {
        isAdminManageContents = adminManageContents;
    }

    public String getTeamMarkUrl() {
        return teamMarkUrl;
    }

    public void setTeamMarkUrl(String teamMarkUrl) {
        this.teamMarkUrl = teamMarkUrl;
    }

    public void update(JSONObject jsonObject) {
        try {
            idx = jsonObject.getString("idx");
            name = jsonObject.getString("name");
            master = jsonObject.getString("master");
            usedCapacity = jsonObject.getLong("usedCapacity");
            maxCapacity = jsonObject.getInt("maxCapacity");
            if (jsonObject.getInt("isPublic") == 1) {
                isPublic = true;
            } else {
                isPublic = false;
            }

            if (jsonObject.getInt("isAutoJoin") == 1) {
                isAutoJoin = true;
            } else {
                isAutoJoin = false;
            }

            if (jsonObject.getInt("isAdminManageMember") == 1) {
                isAdminManageMember = true;
            } else {
                isAdminManageMember = false;
            }

            if (jsonObject.getInt("isAdminManageBoard") == 1) {
                isAdminManageBoard = true;
            } else {
                isAdminManageBoard = false;
            }

            if (jsonObject.getInt("isAdminManageContents") == 1) {
                isAdminManageContents = true;
            } else {
                isAdminManageContents = false;
            }

            teamMarkUrl = jsonObject.getString("teamMarkUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
