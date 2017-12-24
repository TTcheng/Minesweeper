package com.example.wangchuncheng.minesweeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by WangChunCheng on 2017/12/16.
 */

public class Playground extends SurfaceView implements View.OnTouchListener {

    private static final String TAG = "Playground";
    //LEVEL1 mines/boxes = 15/100
    private static final int LEVEL_1 = 1;
    private static final int MINE_1 = 15;
    private static final int MAP_SIZE_1 = 10;
    //LEVEL2 mines/boxes = 30/225 = 13.3%
    private static final int LEVEL_2 = 2;
    private static final int MINE_2 = 30;
    private static final int MAP_SIZE_2 = 15;
    //LEVEL3 mines/boxes = 45/225 = 20%
    private static final int LEVEL_3 = 3;
    private static final int MINE_3= 45;
    private static final int MAP_SIZE_3 = 15;

    private int mMines = 10;
    private int boxWidth;
    private int difficulty = LEVEL_1;
    private int mapSize;
    private int drawTextSize;
    private Box[][] mMatrix;
    private int mMarginTop;
    private int sceenWidth;
    //grid edge arguments
    private int startLeft;
    private int startTop;
    private int stopRight;
    private int stopBottom;

    private AlertDialog.Builder gameOverDialogBuider = new AlertDialog.Builder(getContext());


    private int confirmedSafeBoxes = 0;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(mCallback);
        setOnTouchListener(this);
        gameOverDialogBuider.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGame();//restart game
                redraw();
            }
        });
        gameOverDialogBuider.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        gameOverDialogBuider.setCancelable(false);

        Log.d(TAG, "create Playground");

        startGame();
        //redraw();
    }

    private void initGame() {

        //difficulty = getDifficulty();
        if (getDifficulty()==LEVEL_1) {             //mines/boxes = 15/100
            mapSize = MAP_SIZE_1;
            mMines = MINE_1;
            drawTextSize = 80;
        }else if (getDifficulty()==LEVEL_2){
            mapSize = MAP_SIZE_2;                   //mines/boxes = 40/225
            mMines = MINE_2;
            drawTextSize = 40;
        }
        else if (getDifficulty()==LEVEL_3) {        // mines/boxes = 60/225
            mapSize = MAP_SIZE_3;
            mMines = MINE_3;
            drawTextSize = 40;
        }
        boxWidth = sceenWidth/(mapSize+1);
        mMatrix = new Box[mapSize][mapSize];
        //init matrix
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                mMatrix[i][j] = new Box(i, j);
            }
        }
        //init grid edge arguments
        startLeft = boxWidth / 2;
        startTop = boxWidth / 2 + mMarginTop;
        stopRight = startLeft + boxWidth * mapSize;
        stopBottom = startTop + boxWidth * mapSize;
        //create mines
        for (int i = 0; i < mMines; ) {
            int x = (int) ((Math.random() * 1000) % mapSize);
            int y = (int) ((Math.random() * 1000) % mapSize);
            if (!getBox(x, y).isMine) {
                getBox(x, y).isMine = true;
                getBox(x, y).setMineAround(Box.MINE_AROUND_MAX);
                i++;
            }
        }
        //create neighbor num of mines
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (!mMatrix[i][j].isMine)
                    mMatrix[i][j].setMineAround(getNeighborMines(i, j));
            }
        }
    }

    private int getNeighborMines(int x, int y) {
        int sum = 0;
        if (x - 1 >= 0 && y - 1 >= 0 && mMatrix[x - 1][y - 1].isMine) sum++;              //left-top
        if (x - 1 >= 0 && mMatrix[x - 1][y].isMine) sum++;              //top
        if (x - 1 >= 0 && y + 1 < mapSize && mMatrix[x - 1][y + 1].isMine)
            sum++;              //right-top
        if (y + 1 < mapSize && mMatrix[x][y + 1].isMine) sum++;              //right
        if (x + 1 < mapSize && y + 1 < mapSize && mMatrix[x + 1][y + 1].isMine)
            sum++;              //right-bottom
        if (x + 1 < mapSize && mMatrix[x + 1][y].isMine) sum++;              //bottom
        if (x + 1 < mapSize && y - 1 >= 0 && mMatrix[x + 1][y - 1].isMine)
            sum++;              //left-bottom
        if (y - 1 >= 0 && mMatrix[x][y - 1].isMine) sum++;              //left
        return sum;
    }

    private Box getBox(int x, int y) {
        return mMatrix[x][y];
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    private void redraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        Paint backgroundPaint = new Paint();

        int borderWidth = boxWidth / 2;
        //draw game panel
        backgroundPaint.setColor(Color.GRAY);
        canvas.drawRect(startLeft, startTop, stopRight, stopBottom, backgroundPaint);//GRAY
        //draw grid
        backgroundPaint.setColor(Color.DKGRAY);
        for (int i = 1; i < mapSize + 1; i++) {
            //draw row lines
            canvas.drawLine(startLeft, startTop + boxWidth * i, stopRight, startTop + boxWidth * i, backgroundPaint);
            //draw column lines
            canvas.drawLine(startLeft + boxWidth * i, startTop, startLeft + boxWidth * i, stopBottom, backgroundPaint);
        }

        //draw border frame
        backgroundPaint.setColor(Color.DKGRAY);
        float frameRadius = borderWidth;
        canvas.drawRoundRect(new RectF(0, mMarginTop, borderWidth, stopBottom + borderWidth), frameRadius, frameRadius, backgroundPaint);                         //left border
        canvas.drawRoundRect(new RectF(0, mMarginTop, sceenWidth, startTop), frameRadius, frameRadius, backgroundPaint);                               //top border
        canvas.drawRoundRect(new RectF(stopRight, mMarginTop, sceenWidth, stopBottom + boxWidth / 2), frameRadius, frameRadius, backgroundPaint);                          //right border
        canvas.drawRoundRect(new RectF(0, stopBottom, sceenWidth, stopBottom + boxWidth / 2), frameRadius, frameRadius, backgroundPaint);                          //bottom border

        Paint textPaint = new Paint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLUE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(drawTextSize);

        Paint minePaint = new Paint();
        minePaint.setColor(Color.RED);
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Box box = getBox(i, j);
                if (box.getStatus() == Box.STATUS_KNOWN) {
                    backgroundPaint.setColor(Color.LTGRAY);
                    //draw blank box with LTGRAY;
                    canvas.drawRect(startLeft + box.getX() * boxWidth, startTop + box.getY() * boxWidth, startLeft + box.getX() * boxWidth + boxWidth, startTop + box.getY() * boxWidth + boxWidth, backgroundPaint);
                    if (box.isMine) {    //draw mine BOOM
                        canvas.drawOval(new RectF(startLeft + (box.getX()) * boxWidth, startTop + (box.getY() * boxWidth), startLeft + (box.getX() + 1) * boxWidth, startTop + (box.getY() + 1) * boxWidth), minePaint);
                    } else if (box.getMineAround() != 0) {
                        //draw minesAround
                        int x = startLeft + box.getX() * boxWidth + boxWidth / 2;
                        int y = startTop + (box.getY() + 1) * boxWidth;
                        int margin = boxWidth / 5;
                        canvas.drawText(String.valueOf(box.getMineAround()), x, y - margin, textPaint);
                    }
                }
            }
        }

        getHolder().unlockCanvasAndPost(canvas);
    }

    private void searchSecuriytyNeighbor(Box box) {
        int x = box.getX();
        int y = box.getY();
        if (box.getStatus() == Box.STATUS_UNKNOWN) {
            box.setStatus(Box.STATUS_KNOWN);
            confirmedSafeBoxes++;
            if (box.getMineAround() < 1) {
                if (x - 1 >= 0 && y - 1 >= 0)
                    searchSecuriytyNeighbor(getBox(x - 1, y - 1));         //left-top
                if (x - 1 >= 0)
                    searchSecuriytyNeighbor(getBox(x - 1, y));             //top
                if (x - 1 >= 0 && y + 1 < mapSize)
                    searchSecuriytyNeighbor(getBox(x - 1, y + 1));             //right-top
                if (y + 1 < mapSize)
                    searchSecuriytyNeighbor(getBox(x, y + 1));              //right
                if (x + 1 < mapSize && y + 1 < mapSize)
                    searchSecuriytyNeighbor(getBox(x + 1, y + 1));             //right-bottom
                if (x + 1 < mapSize)
                    searchSecuriytyNeighbor(getBox(x + 1, y));             //bottom
                if (x + 1 < mapSize && y - 1 >= 0)
                    searchSecuriytyNeighbor(getBox(x + 1, y - 1));             //left-bottom
                if (y - 1 >= 0)
                    searchSecuriytyNeighbor(getBox(x, y - 1));             //left
            }
        }
        return;
    }

    private void changeStatus(Box box) {
        if (box.getMineAround() == 0)
            searchSecuriytyNeighbor(box);
        else {
            box.setStatus(Box.STATUS_KNOWN);
            confirmedSafeBoxes++;
        }
        redraw();
        //drawChange(box);
        if (box.isMine) {
            loseGame();
        } else {
            if (confirmedSafeBoxes == mapSize * mapSize - mMines) {
                winGame();
            }
        }
    }


    private void startGame() {
        confirmedSafeBoxes = 0;
        AlertDialog.Builder startGameDialogBuilder = new AlertDialog.Builder(getContext());
        startGameDialogBuilder.setPositiveButton("困难", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDifficulty(LEVEL_3);
                initGame();
                redraw();
            }
        });
        startGameDialogBuilder.setNegativeButton("中等", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDifficulty(LEVEL_2);
                initGame();
                redraw();
            }
        });
        startGameDialogBuilder.setNeutralButton("简单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDifficulty(LEVEL_1);
                initGame();
                redraw();
            }
        });
        AlertDialog startGameAlertDialog = startGameDialogBuilder.create();
        startGameAlertDialog.setTitle("选择难度");
        startGameAlertDialog.setMessage("请选择游戏难度：\n若您的屏幕小于4.5寸，请选择简单");
        startGameAlertDialog.setCancelable(false);
        startGameAlertDialog.show();
    }

    private void winGame() {
        AlertDialog winDialog = gameOverDialogBuider.create();
        winDialog.setTitle("WIN");
        winDialog.setMessage("Lucky! You Win! Do you want to try again?");
        winDialog.show();
    }

    private void loseGame() {
        AlertDialog loseDialog = gameOverDialogBuider.create();
        loseDialog.setTitle("LOSE");
        loseDialog.setMessage("Unlucky You Lose! Do you want to continue?");
        loseDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x, y;
            if (event.getX() < startLeft || event.getX() > stopRight || event.getY() < startTop||event.getY() > stopBottom) {
                //touch out of game panel
            } else {
                //touch in game map
                x = (int) ((event.getX() - boxWidth / 2) / boxWidth);
                y = (int) ((event.getY() - boxWidth / 2 - mMarginTop) / boxWidth);
                if (getBox(x, y).getStatus() == Box.STATUS_UNKNOWN) {
                    changeStatus(getBox(x, y));
                }
            }
        }
        return true;
    }


    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            sceenWidth = width;
            //boxWidth = width / (mapSize + 1);
            mMarginTop = (height - width) / 2;
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };


}
