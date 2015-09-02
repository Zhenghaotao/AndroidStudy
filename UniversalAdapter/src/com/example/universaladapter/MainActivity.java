package com.example.universaladapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.example.universaladapter.adapter.CommonAdapter;
import com.example.universaladapter.adapter.ViewHolder;

public class MainActivity extends Activity {

	private List<String> list = new ArrayList<String>();
	private ListView lv_data;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        lv_data.setAdapter(new CommonAdapter<String>(MainActivity.this,list,R.layout.listitem) {


			@Override
			public void convert(ViewHolder viewHolder, String item) {
				viewHolder.setText(R.id.tv_title, item);
			}
		});
        
    }

	private void initView() {
		lv_data = (ListView) findViewById(R.id.lv_data);
	}

	private void initData() {
		list.add("andorid");
		list.add("python");
		list.add("javascript");
		list.add("thinkphp");
		list.add("springmvc");
	}
	

}
