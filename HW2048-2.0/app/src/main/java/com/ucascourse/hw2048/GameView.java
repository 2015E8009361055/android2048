package com.ucascourse.hw2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/4/16.
 */
public class GameView extends GridLayout {
    private Card[][] cardsMap=new Card[4][4];
    private List<Point> emptyPoints=new ArrayList<Point>();
    private static GameView gameView;
    private Stack<Integer> historyStack;
    private RankScore rankScore;

    public  int getCardWidth() {
        return cardWidth;
    }

    public static int cardWidth;
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameView=this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameView=this;
        initGame();
    }

    public String[] getRank(){
        return rankScore.getRankScore();
    }

    public GameView(Context context) {
        super(context);
        gameView=this;
        initGame();
    }
    public static GameView getGameView(){
        return gameView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
         cardWidth = (Math.min(w, h)-10)/4;

        addCards(cardWidth, cardWidth);

        startGame(0);

    }

    public void startGame(int lastScore){

        MainActivity.mainActivity.clearScore();
        historyStack.clear();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                cardsMap[x][y].setNum(0);
            }
        }
        MainActivity.mainActivity.setTopScore(rankScore.getTopScore());
        MainActivity.mainActivity.setScore(0);
        updateRankScore(lastScore);

        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum(){

        emptyPoints.clear();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardsMap[x][y].getNum()<=0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
        cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
    }

   public void addCards(int cardWidth,int cardHeight){

        Card c;
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    c = new Card(getContext());
                    c.setNum(0);
                    addView(c, cardWidth, cardHeight);
                    cardsMap[x][y] = c;
                }
            }
    }
    public void undoDraw(){
        int temp;
        if(historyStack.isEmpty())
            return;

        for (int x = 3; x > -1; x--) {
                    for (int y = 3; y > -1; y--) {
                    temp= historyStack.pop();
                    cardsMap[x][y].setNum(temp);
                }
            }
        MainActivity.mainActivity.setScore(historyStack.pop());

    }

    public  void initGame() {
        historyStack = new Stack<Integer>();
        setColumnCount(4);
        setBackgroundColor(0xffbbada0);
        rankScore =new RankScore(getContext());
        setOnTouchListener(new View.OnTouchListener() {
            private float startx, starty, offsetx, offsety;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startx = event.getX();
                        starty = event.getY();
                        //保存状态
                        if (!historyStack.isEmpty())
                            historyStack.clear();
                        historyStack.push(MainActivity.mainActivity.getScore());
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 4; y++) {
                                historyStack.push(cardsMap[x][y].getNum());
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetx = event.getX() - startx;
                        offsety = event.getY() - starty;


                        if (Math.abs(offsetx) > Math.abs(offsety)) {
                            if (offsetx < -5)
                                slideLeft();
                            else if (offsetx > 5)
                                slideRight();
                        } else {
                            if (offsety < -5)
                                slideUp();
                            else if (offsety > 5)
                                slideDown();
                        }


                        break;
                }
                return true;
            }
        });
}
    public void updateRankScore(int lastScore){


        if(lastScore> rankScore.getLowScore())
            rankScore.setRankScore(lastScore);
    }

    private void checkFinish(){

        boolean finish = true;

        ALL:
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardsMap[x][y].getNum()==0||
                        (x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<3&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<3&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {

                    finish = false;
                    break ALL;
                }
            }
        }

        if (finish) {
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setPositiveButton("重来", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame(MainActivity.mainActivity.getScore());
                }
            }).show();
        }

    }

    private void slideLeft(){

        boolean merge = false;

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {

                for (int x1 = x+1; x1 < 4; x1++) {
                    if (cardsMap[x1][y].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x--;

                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);

                            MainActivity.mainActivity.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {

            addRandomNum();
            checkFinish();
        }
    }


    private void slideRight(){

        boolean merge = false;

        for (int y = 0; y < 4; y++) {
            for (int x = 3; x >=0; x--) {

                for (int x1 = x-1; x1 >=0; x1--) {
                    if (cardsMap[x1][y].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.mainActivity.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {

            addRandomNum();
            checkFinish();
        }
    }
    private void slideUp(){

        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                for (int y1 = y+1; y1 < 4; y1++) {
                    if (cardsMap[x][y1].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y--;

                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.mainActivity.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;

                    }
                }
            }
        }

        if (merge) {

            addRandomNum();
            checkFinish();
        }
    }
    private void slideDown(){

        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 3; y >=0; y--) {

                for (int y1 = y-1; y1 >=0; y1--) {
                    if (cardsMap[x][y1].getNum()>0) {

                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.mainActivity.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {

            addRandomNum();
            checkFinish();
        }
    }
}
