package com.ucascourse.hw2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class RankScore {
	private SharedPreferences sp;
	int[] rankArray=new int[6];
	
	
	public RankScore(Context context){

		sp = context.getSharedPreferences("TopScore", context.MODE_PRIVATE);
	}

	public int getTopScore(){

		int topScore = sp.getInt("rank1", 0);
		return topScore;
	}
	public int getLowScore(){

		int lowScore = sp.getInt("rank5", 0);
		return lowScore;
	}

	public String[] getRankScore(){
		String[] rankS =new String[5];
		rankS[0] = "第一名："+sp.getInt("rank1",0);
		rankS[1] = "第二名："+sp.getInt("rank2",0);
		rankS[2] = "第三名："+sp.getInt("rank3",0);
		rankS[3] = "第四名："+sp.getInt("rank4",0);
		rankS[4] = "第五名："+sp.getInt("rank5",0);
		return rankS;
	}
	public void setRankScore(int lastScore){
		rankArray[0]=lastScore;
		rankArray[1]=sp.getInt("rank5",0);
		rankArray[2]=sp.getInt("rank4",0);
		rankArray[3]=sp.getInt("rank3",0);
		rankArray[4]=sp.getInt("rank2",0);
		rankArray[5]=sp.getInt("rank1",0);
		Arrays.sort(rankArray);
		Editor editor = sp.edit();
		editor.putInt("rank1", rankArray[5]);
		editor.putInt("rank2", rankArray[4]);
		editor.putInt("rank3", rankArray[3]);
		editor.putInt("rank4", rankArray[2]);
		editor.putInt("rank5", rankArray[1]);
		editor.commit();
	}
}
