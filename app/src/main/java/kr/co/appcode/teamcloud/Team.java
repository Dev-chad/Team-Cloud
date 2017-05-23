package kr.co.appcode.teamcloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chad on 2017-05-21.
 */

public class Team implements Parcelable{
    private String idx;
    private String name;
    private String master;
    private double usedCapacity;
    private int maxCapacity;
    private boolean isPulic;
    private boolean isAutoJoin;
    private boolean isAdminManageMember;
    private boolean isAdminManageBoard;
    private boolean isAdminManageContents;
    private String teamMarkUrl;

    public Team(String idx, String name, String master, double usedCapacity, int maxCapacity, boolean isPulic, boolean isAutoJoin, boolean isAdminManageMember, boolean isAdminManageBoard, boolean isAdminManageContents, String teamMarkUrl) {
        this.idx = idx;
        this.name = name;
        this.master = master;
        this.usedCapacity = usedCapacity;
        this.maxCapacity = maxCapacity;
        this.isPulic = isPulic;
        this.isAutoJoin = isAutoJoin;
        this.isAdminManageMember = isAdminManageMember;
        this.isAdminManageBoard = isAdminManageBoard;
        this.isAdminManageContents = isAdminManageContents;
        this.teamMarkUrl = teamMarkUrl;
    }

    private Team(JSONObject jsonObject){
        try {
            idx = jsonObject.getString("idx");
            name = jsonObject.getString("name");
            master = jsonObject.getString("master");
            usedCapacity = jsonObject.getDouble("usedCapacity");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
