package com.example.wangchuncheng.minesweeper;

/**
 * Created by WangChunCheng on 2017/12/16.
 */

public class Box {
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_KNOWN = 1;

    public static final int MINE_AROUND_MAX = 9;

    public boolean isMine;// = false;
    private int x, y;
    private int status;
    private int mineAround;

    public Box(int x, int y) {
        setXY(x,y);
        setMineAround(0);
        isMine = false;
        setStatus(STATUS_UNKNOWN);
    }

    public int getMineAround() {
        return mineAround;
    }

    public void setMineAround(int mineAround) {
        this.mineAround = mineAround;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
