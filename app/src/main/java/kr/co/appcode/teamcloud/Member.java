package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-05-27.
 */

public class Member extends User {
    private boolean isManageMember;
    private boolean isManageBoard;
    private boolean isManageContents;
    private String accessDate;
    private String joinDate;

    public Member(String nickname, int level, int isManageMember, int isManageBoard, int isManageContents, String accessDate, String joinDate) {
        super.setNickname(nickname);
        super.setLevel(level);
        this.isManageMember = (isManageMember == 1);
        this.isManageContents = (isManageContents == 1);
        this.isManageBoard = (isManageBoard == 1);
        this.accessDate = accessDate;
        this.joinDate = joinDate;
    }

    public boolean isManageMember() {
        return isManageMember;
    }

    public void setManageMember(boolean manageMember) {
        isManageMember = manageMember;
    }

    public boolean isManageBoard() {
        return isManageBoard;
    }

    public void setManageBoard(boolean manageBoard) {
        isManageBoard = manageBoard;
    }

    public boolean isManageContents() {
        return isManageContents;
    }

    public void setManageContents(boolean manageContents) {
        isManageContents = manageContents;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
}
