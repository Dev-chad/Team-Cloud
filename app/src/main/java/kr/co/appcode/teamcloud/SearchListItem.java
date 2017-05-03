package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-04-30.
 */

public class SearchListItem {
    private String teamName;
    private String masterName;
    private int level;

    public SearchListItem(String teamName, String masterName, int level) {
        this.teamName = teamName;
        this.masterName = masterName;
        this.level = level;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
