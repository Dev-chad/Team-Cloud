package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-05-07.
 */

public class Board {

    private String name;
    private int writeAuth;
    private int readAuth;

    public Board(String name, int writeAuth, int readAuth) {
        this.name = name;
        this.writeAuth = writeAuth;
        this.readAuth = readAuth;
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
