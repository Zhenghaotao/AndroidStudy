package com.example.guessmusic.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Util {
	public static View getView(int layoutId, ViewGroup parent,Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return inflater.inflate(layoutId, parent,false);
	}
	
	public static View getView(int layoutId,Context context) {
		return getView(layoutId,null, context);
		
	}
}
