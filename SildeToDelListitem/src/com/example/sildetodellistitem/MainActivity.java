package com.example.sildetodellistitem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.sildetodellistitem.SlideCutListView.RemoveDirection;
import com.example.sildetodellistitem.SlideCutListView.RemoveListener;

public class MainActivity extends Activity implements RemoveListener{
	private SlideCutListView scl;
	private ArrayAdapter<String> adapter;
	private List<String> dataSourceList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scl = (SlideCutListView) findViewById(R.id.scl);
        init();
    }

	private void init() {
		for(int i =0;i <20; i++){
			dataSourceList.add("滑动删除" + i);
		}
		adapter = new ArrayAdapter<String>(this, R.layout.list_item,R.id.list_item,dataSourceList);
		scl.setAdapter(adapter);
		scl.setOnRmoveListener(this);
	}

	@Override
	public void removeItem(RemoveDirection direction, int position) {
		adapter.remove(adapter.getItem(position));  
		switch (direction) {  
		case RIGHT:
			Toast.makeText(this, "向右删除  "+ position, Toast.LENGTH_SHORT).show();
			break;
		case LEFT:
			Toast.makeText(this, "向左删除  "+ position, Toast.LENGTH_SHORT).show();  
			break;
		}
	}


    
}
