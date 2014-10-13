package com.jiuzhansoft.ehealthtec.game;

import org.cocos2dx.lib.Cocos2dxActivity;

import com.jiuzhansoft.ehealthtec.log.Log;

import android.os.Bundle;

public class Hamster extends Cocos2dxActivity{

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
    static {
         System.loadLibrary("game");
    }
}
